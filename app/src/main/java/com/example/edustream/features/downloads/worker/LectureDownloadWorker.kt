package com.example.edustream.features.downloads.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.edustream.features.downloads.data.local.dao.DownloadDao
import com.example.edustream.features.downloads.data.local.entities.DownloadStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class LectureDownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val downloadDao: DownloadDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): androidx.work.ListenableWorker.Result {
        val lectureId = inputData.getString("lectureId") ?: return androidx.work.ListenableWorker.Result.failure()
        val downloadUrl = inputData.getString("downloadUrl") ?: return androidx.work.ListenableWorker.Result.failure()

        return try {
            // Update status to DOWNLOADING
            downloadDao.updateProgress(lectureId, 0, DownloadStatus.DOWNLOADING.name)

            // Simulate download process
            for (i in 1..10) {
                delay(1000) // Simulate network delay
                val progress = i * 10L
                setProgress(workDataOf("progress" to i * 10))
                downloadDao.updateProgress(lectureId, progress, DownloadStatus.DOWNLOADING.name)
            }

            // Update status to COMPLETED
            downloadDao.updateProgress(lectureId, 100, DownloadStatus.COMPLETED.name)

            androidx.work.ListenableWorker.Result.success()
        } catch (e: Exception) {
            downloadDao.updateProgress(lectureId, 0, DownloadStatus.FAILED.name)
            androidx.work.ListenableWorker.Result.failure()
        }
    }
}
