package io.github.sickandpanik

import kotlin.reflect.full.memberProperties

fun objectWithPropertiesToString(obj: Any, propertiesToIgnore: Collection<String> = listOf()): String {
    val name = "${obj::class.simpleName}"
    val propertiesWithValues = obj::class.memberProperties
        .filter { it.name !in propertiesToIgnore }
        .map {
            "${it.name}: ${it.call(obj)}"
        }

    return "$name${if (propertiesWithValues.isEmpty()) "" else ", "}${propertiesWithValues.joinToString(", ")}"
}