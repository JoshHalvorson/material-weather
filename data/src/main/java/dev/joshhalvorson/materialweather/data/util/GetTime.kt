package dev.joshhalvorson.materialweather.data.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.TimeZone

fun getTimeString(dateTime: Long): String {
    val localTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(dateTime), TimeZone.getDefault().toZoneId()
    ).plusDays(1)
    val localizedTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
    return localTime.format(localizedTimeFormatter)
}

fun getTime(dateTime: Long): String {
    val localTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(dateTime), TimeZone.getDefault().toZoneId()
    ).plusDays(1)
    val localizedTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    return localTime.format(localizedTimeFormatter)
}