package org.filtersaathi.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.filtersaathi.app.data.local.dao.FilterDao
import org.filtersaathi.app.data.local.dao.WaterTestDao
import org.filtersaathi.app.data.local.entity.FilterEntity
import org.filtersaathi.app.data.local.entity.WaterTestReadingEntity

@Database(
    entities = [FilterEntity::class, WaterTestReadingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun filterDao(): FilterDao
    abstract fun waterTestDao(): WaterTestDao
}
