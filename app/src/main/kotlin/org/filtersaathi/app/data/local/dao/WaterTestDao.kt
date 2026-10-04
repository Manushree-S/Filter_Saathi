package org.filtersaathi.app.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.filtersaathi.app.data.local.entity.WaterTestReadingEntity

@Dao
interface WaterTestDao {
    @Query("SELECT * FROM water_test_readings ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTestReadingLiveData(): LiveData<WaterTestReadingEntity?>

    @Query("SELECT * FROM water_test_readings ORDER BY timestamp DESC")
    fun getAllReadingsLiveData(): LiveData<List<WaterTestReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: WaterTestReadingEntity)

    @Query("SELECT COUNT(*) FROM water_test_readings WHERE isSafe = 0")
    suspend fun getUnsafeReadingsCount(): Int
}
