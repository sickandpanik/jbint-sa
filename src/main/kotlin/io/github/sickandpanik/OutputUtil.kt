package io.github.sickandpanik

import kotlin.reflect.KProperty

fun objectWithPropertiesToString(obj: Any, propertiesToIgnore: Collection<String> = listOf()): String {
    val name = "${obj::class.simpleName}"
    val propertiesWithValues = obj::class.members
        .filterIsInstance(KProperty::class.java)
        .filter { it.name !in propertiesToIgnore }
        .map {
            "${it.name}: ${it.call(obj)}"
        }

    return "$name${if (propertiesWithValues.isEmpty()) "" else ", "}${propertiesWithValues.joinToString(", ")}"
}