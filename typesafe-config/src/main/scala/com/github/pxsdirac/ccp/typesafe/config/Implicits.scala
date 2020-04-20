package com.github.pxsdirac.ccp.typesafe.config

import com.github.pxsdirac.ccp.core.parser.{KeyValueParser, LowPriorityCaseClassParser, LowPriorityKeyValueParser}

trait Implicits
    extends PrimitiveTypeSupport
    with EnrichedTypeSupport
    with CollectionTypeSupport
    with LowPriorityKeyValueParser
    with LowPriorityCaseClassParser
