package com.example.edustream.features.downloads.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DownloadStatus {
    QUEUED, DOWNLOADING, COMPLETED, FAILED
}

@Entity(tableName = "download_table")
data class DownloadEntity(
    @PrimaryKey
    val downloadId: String,
    val lectureId: String,
    val courseId: String,
    val localFilePath: String?,
    val fileSizeBytes: Long,
    val downloadedBytes: Long,
    val status: DownloadStatus,
    val quality: String,
    val expiresAt: Long,
    val createdAt: Long
)
