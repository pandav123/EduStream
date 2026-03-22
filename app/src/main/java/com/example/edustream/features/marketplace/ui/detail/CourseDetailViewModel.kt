package com.example.edustream.features.marketplace.ui.detail

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.marketplace.data.local.entities.NoteEntity
import com.example.edustream.features.marketplace.data.local.entities.QuizEntity
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import com.example.edustream.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CourseDetailState(
    val course: CourseEntity? = null,
    val lectures: List<LectureEntity> = emptyList(),
    val quizzes: List<QuizEntity> = emptyList(),
    val notes: List<NoteEntity> = emptyList()
)

data class DownloadProgress(
    val noteId: String,
    val progress: Float, // 0.0 to 1.0
    val status: Int // DownloadManager.STATUS_*
)

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    application: Application,
    private val repository: CourseRepository,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val courseId: String = checkNotNull(savedStateHandle["courseId"])

    private val _uiState = MutableStateFlow<UiState<CourseDetailState>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadProgress = _downloadProgress.asStateFlow()

    private val downloadManager = application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var progressJob: Job? = null

    init {
        loadCourseData()
        refreshData()
    }

    private fun loadCourseData() {
        viewModelScope.launch {
            combine(
                repository.getCourseDetail(courseId),
                repository.getLectures(courseId),
                repository.getQuizzes(courseId),
                repository.getNotes(courseId)
            ) { course, lectures, quizzes, notes ->
                if (course != null) {
                    UiState.Success(CourseDetailState(course, lectures, quizzes, notes))
                } else {
                    UiState.Error("Course not found")
                }
            }.collect {
                _uiState.value = it
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            repository.refreshLectures(courseId)
            repository.refreshQuizzes(courseId)
            repository.refreshNotes(courseId)
        }
    }

    fun purchaseCourse() {
        viewModelScope.launch {
            repository.purchaseCourse(courseId)
        }
    }

    fun downloadNote(note: NoteEntity) {
        val request = DownloadManager.Request(Uri.parse(note.pdfUrl))
            .setTitle(note.title)
            .setDescription("Downloading study note")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Note_${note.noteId}.pdf")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadId = downloadManager.enqueue(request)
        startTrackingProgress(note.noteId, downloadId)
    }

    private fun startTrackingProgress(noteId: String, downloadId: Long) {
        if (progressJob == null || progressJob?.isCompleted == true) {
            progressJob = viewModelScope.launch {
                while (true) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor: Cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                        val progress = if (bytesTotal > 0) bytesDownloaded.toFloat() / bytesTotal else 0f
                        
                        _downloadProgress.update { it + (noteId to DownloadProgress(noteId, progress, status)) }

                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                            cursor.close()
                            break
                        }
                    }
                    cursor.close()
                    delay(500)
                }
            }
        }
    }
}
