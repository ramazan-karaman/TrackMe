package com.example.trackme.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "runs_table")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTimeMillis: Long,
    val timeInMillis: Long,
    val avgSpeedKMH: Float,
    val distanceInMeters: Int
)
