package org.filtersaathi.app.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.filtersaathi.app.data.local.entity.FilterEntity

@Dao
interface FilterDao {
    @Query("SELECT * FROM filters")
    fun getAllFiltersLiveData(): LiveData<List<FilterEntity>>

    @Query("SELECT * FROM filters")
    suspend fun getAllFilters(): List<FilterEntity>

    @Query("SELECT * FROM filters WHERE isUrgent = 1 LIMIT 1")
    fun getMostUrgentFilterLiveData(): LiveData<FilterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilters(filters: List<FilterEntity>)

    @Update
    suspend fun updateFilter(filter: FilterEntity)
}
