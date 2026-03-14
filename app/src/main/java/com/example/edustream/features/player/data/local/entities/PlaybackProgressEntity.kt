package com.example.edustream.features.player.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_progress_table")
data class PlaybackProgressEntity(
    @PrimaryKey
    val lectureId: String,
    val courseId: String,
    val userId: String,
    val lastPosition: Long, // in ms
    val totalDuration: Long, // in ms
    val lastUpdated: Long, // epoch ms
    val isCompleted: Boolean
)
