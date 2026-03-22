package com.example.edustream.features.marketplace.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "note_table",
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
data class NoteEntity(
    @PrimaryKey
    val noteId: String,
    val courseId: String,
    val title: String,
    val pdfUrl: String,
    val fileSize: String,
    val createdAt: Long
)
