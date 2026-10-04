package org.filtersaathi.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_test_readings")
data class WaterTestReadingEntity(
    @PrimaryKey
    val id: String,
    val filterId: String,
    val timestamp: Long,
    val location: String,
    val tdsPpm: Float,
    val ph: Float,
    val chlorineMgL: Float,
    val nitrateMgL: Float,
    val isSafe: Boolean,
    val syncedWithBackend: Boolean = false
)
