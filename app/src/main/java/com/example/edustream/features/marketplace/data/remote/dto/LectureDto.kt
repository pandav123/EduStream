package com.example.edustream.features.marketplace.data.remote.dto

import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.google.gson.annotations.SerializedName

data class LectureDto(
    @SerializedName("lectureId") val lectureId: String,
    @SerializedName("courseId") val courseId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("videoUrl") val videoUrl: String,
    @SerializedName("duration") val duration: Long,
    @SerializedName("orderIndex") val orderIndex: Int,
    @SerializedName("isPreview") val isPreview: Boolean
)

fun LectureDto.toEntity(): LectureEntity {
    return LectureEntity(
        lectureId = lectureId,
        courseId = courseId,
        title = title,
        description = description,
        videoUrl = videoUrl,
        duration = duration,
        orderIndex = orderIndex,
        isPreview = isPreview
    )
}
