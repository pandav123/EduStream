package com.example.edustream.features.downloads.data.local.dao

import androidx.room.*
import com.example.edustream.features.downloads.data.local.entities.DownloadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM download_table")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM download_table WHERE lectureId = :lectureId")
    suspend fun getDownloadByLectureId(lectureId: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDownload(download: DownloadEntity)

    @Query("UPDATE download_table SET downloadedBytes = :bytes, status = :status WHERE lectureId = :lectureId")
    suspend fun updateProgress(lectureId: String, bytes: Long, status: String)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)
}
