package br.com.gn.utils

import java.math.BigDecimal
import java.time.LocalDate

fun String.toLocalDate(): LocalDate? {
    if (isNullOrBlank())
        return null

    return try {
        LocalDate.parse(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("Could not parse date from $this")
    }
}

fun String.toBigDecimal(): BigDecimal? {
    if (isNullOrBlank())
        return null

    return try {
        BigDecimal(this)
    } catch (e: Exception) {
        throw IllegalArgumentException("Could not parse BigDecimal from $this")
    }
}

inline fun <reified T : Enum<T>> String.toEnum(): T? {
    if (startsWith("UNK"))
        return null
    return enumValueOf<T>(this)
}