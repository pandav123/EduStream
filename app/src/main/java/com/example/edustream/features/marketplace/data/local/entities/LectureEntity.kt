package com.example.edustream.features.marketplace.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lecture_table",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["courseId"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["courseId"])]
)
data class LectureEntity(
    @PrimaryKey
    val lectureId: String,
    val courseId: String,
    val title: String,
    val description: String,
    val videoUrl: String,
    val duration: Long, // in seconds
    val orderIndex: Int,
    val isPreview: Boolean = false
)
