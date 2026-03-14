package com.example.edustream.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.edustream.features.marketplace.data.local.dao.CourseDao
import com.example.edustream.features.marketplace.data.local.dao.LectureDao
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.player.data.local.dao.PlaybackProgressDao
import com.example.edustream.features.player.data.local.entities.PlaybackProgressEntity
import com.example.edustream.features.downloads.data.local.dao.DownloadDao
import com.example.edustream.features.downloads.data.local.entities.DownloadEntity
import com.example.edustream.features.payments.data.local.dao.OrderDao
import com.example.edustream.features.payments.data.local.dao.SubscriptionDao
import com.example.edustream.features.payments.data.local.entities.OrderEntity
import com.example.edustream.features.payments.data.local.entities.SubscriptionEntity
import com.example.edustream.features.progress.data.local.dao.LectureCompletionDao
import com.example.edustream.features.progress.data.local.entities.LectureCompletionEntity

@Database(
    entities = [
        CourseEntity::class,
        LectureEntity::class,
        PlaybackProgressEntity::class,
        DownloadEntity::class,
        OrderEntity::class,
        SubscriptionEntity::class,
        LectureCompletionEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class EduStreamDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun lectureDao(): LectureDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun downloadDao(): DownloadDao
    abstract fun orderDao(): OrderDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun lectureCompletionDao(): LectureCompletionDao
}
