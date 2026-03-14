package com.example.edustream.features.marketplace.data.remote.dto

import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("courseId") val courseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("instructorId") val instructorId: String,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String,
    @SerializedName("previewVideoUrl") val previewVideoUrl: String,
    @SerializedName("price") val price: Double,
    @SerializedName("discountPrice") val discountPrice: Double?,
    @SerializedName("category") val category: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("enrollmentCount") val enrollmentCount: Int,
    @SerializedName("totalLectures") val totalLectures: Int,
    @SerializedName("totalDuration") val totalDuration: Long,
    @SerializedName("isPurchased") val isPurchased: Boolean,
    @SerializedName("createdAt") val createdAt: Long
)

fun CourseDto.toEntity(): CourseEntity {
    return CourseEntity(
        courseId = courseId,
        title = title,
        description = description,
        instructorId = instructorId,
        thumbnailUrl = thumbnailUrl,
        previewVideoUrl = previewVideoUrl,
        price = price,
        discountPrice = discountPrice,
        category = category,
        rating = rating,
        enrollmentCount = enrollmentCount,
        totalLectures = totalLectures,
        totalDuration = totalDuration,
        isPurchased = isPurchased,
        createdAt = createdAt
    )
}
