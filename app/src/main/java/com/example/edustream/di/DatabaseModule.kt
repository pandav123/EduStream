package com.example.edustream.di

import android.content.Context
import androidx.room.Room
import com.example.edustream.data.local.EduStreamDatabase
import com.example.edustream.features.marketplace.data.local.dao.CourseDao
import com.example.edustream.features.marketplace.data.local.dao.LectureDao
import com.example.edustream.features.player.data.local.dao.PlaybackProgressDao
import com.example.edustream.features.downloads.data.local.dao.DownloadDao
import com.example.edustream.features.payments.data.local.dao.OrderDao
import com.example.edustream.features.payments.data.local.dao.SubscriptionDao
import com.example.edustream.features.progress.data.local.dao.LectureCompletionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): EduStreamDatabase {
        return Room.databaseBuilder(
            context,
            EduStreamDatabase::class.java,
            "edustream_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCourseDao(database: EduStreamDatabase): CourseDao {
        return database.courseDao()
    }

    @Provides
    fun provideLectureDao(database: EduStreamDatabase): LectureDao {
        return database.lectureDao()
    }

    @Provides
    fun providePlaybackProgressDao(database: EduStreamDatabase): PlaybackProgressDao {
        return database.playbackProgressDao()
    }

    @Provides
    fun provideDownloadDao(database: EduStreamDatabase): DownloadDao {
        return database.downloadDao()
    }

    @Provides
    fun provideOrderDao(database: EduStreamDatabase): OrderDao {
        return database.orderDao()
    }

    @Provides
    fun provideSubscriptionDao(database: EduStreamDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }

    @Provides
    fun provideLectureCompletionDao(database: EduStreamDatabase): LectureCompletionDao {
        return database.lectureCompletionDao()
    }
}
