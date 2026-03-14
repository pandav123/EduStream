package com.example.edustream.features.progress.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecture_completion_table")
data class LectureCompletionEntity(
    @PrimaryKey
    val lectureId: String,
    val courseId: String,
    val userId: String,
    val completedAt: Long, // epoch ms
    val watchPercent: Float
)
