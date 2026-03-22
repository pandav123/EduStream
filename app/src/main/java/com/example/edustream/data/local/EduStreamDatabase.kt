package com.example.edustream.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.edustream.features.marketplace.data.local.dao.*
import com.example.edustream.features.marketplace.data.local.entities.*
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
        QuizEntity::class,
        QuestionEntity::class,
        NoteEntity::class,
        PlaybackProgressEntity::class,
        DownloadEntity::class,
        OrderEntity::class,
        SubscriptionEntity::class,
        LectureCompletionEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EduStreamDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun lectureDao(): LectureDao
    abstract fun quizDao(): QuizDao
    abstract fun noteDao(): NoteDao
    abstract fun playbackProgressDao(): PlaybackProgressDao
    abstract fun downloadDao(): DownloadDao
    abstract fun orderDao(): OrderDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun lectureCompletionDao(): LectureCompletionDao
}
