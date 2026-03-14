package com.example.edustream.features.marketplace.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_table")
data class CourseEntity(
    @PrimaryKey
    val courseId: String,
    val title: String,
    val description: String,
    val instructorId: String,
    val thumbnailUrl: String,
    val previewVideoUrl: String,
    val price: Double,
    val discountPrice: Double?,
    val category: String,
    val rating: Float,
    val enrollmentCount: Int,
    val totalLectures: Int,
    val totalDuration: Long,
    val isPurchased: Boolean,
    val createdAt: Long
)
