package com.github.pxsdirac.ccp.core.parser

import shapeless.{::, Default, HList, HNil, LabelledGeneric, Lazy, Typeable, Witness}
import shapeless.labelled.{FieldType, field}
import shapeless.ops.record.Keys

import scala.util.{Failure, Success}

/**
  * provide low priority type class derivation for [[CaseClassParser]]
  */
trait LowPriorityCaseClassParser {
  type WithDefaultValueAndNameMapping[D, A] = (Map[String, Any], NameMapping[A]) => CaseClassParser[D, A]

  implicit def hNilParser[D]: WithDefaultValueAndNameMapping[D, HNil] = (_, _) => _ => Success(HNil)

  implicit def hConsParser[D, K <: Symbol, H, T <: HList](
    implicit witness: Witness.Aux[K],
    typeable: Typeable[H],
    hKeyValueParser: Lazy[KeyValueParser[D, H]],
    tProductParser: WithDefaultValueAndNameMapping[D, T]
  ): WithDefaultValueAndNameMapping[D, FieldType[K, H] :: T] =
    (defaultValueMap, nameMapping) =>
      data => {
        val name = witness.value.name
        val mappedName = nameMapping.get(name)
        val hTry = hKeyValueParser.value.parseValueByKey(data, mappedName) match {
          case v @ Success(_: None.type) =>
            defaultValueMap.get(name).flatMap(typeable.cast) match {
              case Some(h) => Success(h)
              case _       => v
            }
          case v @ Success(_) => v
          case f @ Failure(_) =>
            defaultValueMap.get(name).flatMap(typeable.cast) match {
              case Some(h) => Success(h)
              case _       => f // cast should never has exception, if get none here, no default value is set.
            }
        }
        val tTry = tProductParser(defaultValueMap, NameMapping.fromNameMapping[T](nameMapping)).parse(data)
        for {
          h <- hTry
          t <- tTry
        } yield {
          field[K](h) :: t
        }
      }

  implicit def genricParser[D, A, R1 <: HList, R2 <: HList, R3 <: HList](
    implicit
    defaultAux: Default.Aux[A, R1],
    labelledGeneric: LabelledGeneric.Aux[A, R2],
    keysAux: Keys.Aux[R2, R3],
    withDefaultValue: Lazy[WithDefaultValueAndNameMapping[D, R2]],
    nameMapping: NameMapping[A] = NameMapping.empty[A]
  ): CaseClassParser[D, A] = {
    val values = defaultAux()
    val keys = keysAux()
    implicit val defaultValueMap = getDefaultValueMap(values, keys)
    val r2CaseClassParser = withDefaultValue.value(defaultValueMap, NameMapping.fromNameMapping[R2](nameMapping))
    (data: D) => {
      val r2Try = r2CaseClassParser.parse(data)
      r2Try map labelledGeneric.from
    }
  }

  private def getDefaultValueMap[R1 <: HList, R2 <: HList](values: R1, keys: R2): Map[String, Any] = {
    import scala.{:: => Cons}
    def hListToList(hList: HList): List[Any] = hList match {
      case h :: t => Cons(h, hListToList(t))
      case HNil   => Nil
    }

    hListToList(keys)
      .zip(hListToList(values))
      .map {
        case (k, v) => (k.asInstanceOf[Symbol].name -> v.asInstanceOf[Option[_]])
      }
      .collect {
        case (k, Some(v)) => (k, v)
      }
      .toMap
  }
}
