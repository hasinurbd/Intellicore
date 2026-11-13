package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "simulation_logs")
data class SimulationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val siteName: String,
    val timestampMillis: Long,
    val durationHours: Double,
    val distanceKm: Double,
    val energyConsumedKWh: Double,
    val batteryRemainingPercent: Double,
    val volatilesExtractedKg: Double,
    val statusSummary: String
)
