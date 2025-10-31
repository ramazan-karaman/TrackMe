package com.example.trackme.ui.tracking

import android.location.Location

data class TrackingUiState(
    val isTracking: Boolean= false,
    val pathPoints: List<Location> = emptyList(),
    val currentTimeMillis: Long= 0L,
    val distanceInMeters: Int= 0,
    val avgSpeedKMH: Float= 0f
)
