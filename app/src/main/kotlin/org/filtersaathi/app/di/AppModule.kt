package org.filtersaathi.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.filtersaathi.app.data.local.dao.FilterDao
import org.filtersaathi.app.data.local.dao.WaterTestDao
import org.filtersaathi.app.data.local.database.AppDatabase
import org.filtersaathi.app.data.remote.api.FilterSaathiApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "filter_saathi.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFilterDao(database: AppDatabase): FilterDao {
        return database.filterDao()
    }

    @Provides
    fun provideWaterTestDao(database: AppDatabase): WaterTestDao {
        return database.waterTestDao()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient): FilterSaathiApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.filtersaathi.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FilterSaathiApiService::class.java)
    }
}
