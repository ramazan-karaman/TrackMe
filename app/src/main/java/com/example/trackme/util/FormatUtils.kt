package com.example.trackme.util


import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

fun formatDuration(ms: Long): String {
    var milliseconds = ms
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    milliseconds -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

    return when {
        hours > 0 -> "${hours}s ${minutes}dk ${seconds}sn"
        minutes > 0 -> "${minutes}dk ${seconds}sn"
        else -> "${seconds}sn"
    }
}

fun formatTimestamp(ms: Long): String {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("tr"))
    return dateFormat.format(Date(ms))
}

fun formatDistance(meters: Int): String {
    val km = meters / 1000f
    return String.format(Locale.US, "%.1f km", km)
}

fun formatSpeed(speedKmh: Float): String {
    return String.format(Locale.US, "%.1f km/s", speedKmh)
}