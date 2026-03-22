package com.example.edustream.features.marketplace.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "quiz_table",
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
data class QuizEntity(
    @PrimaryKey
    val quizId: String,
    val courseId: String,
    val title: String,
    val description: String,
    val totalQuestions: Int,
    val orderIndex: Int
)

@Entity(
    tableName = "question_table",
    foreignKeys = [
        ForeignKey(
            entity = QuizEntity::class,
            parentColumns = ["quizId"],
            childColumns = ["quizId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["quizId"])]
)
data class QuestionEntity(
    @PrimaryKey
    val questionId: String,
    val quizId: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String? = null
)
