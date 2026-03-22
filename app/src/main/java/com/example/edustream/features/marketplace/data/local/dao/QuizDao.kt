package com.example.edustream.features.marketplace.data.local.dao

import androidx.room.*
import com.example.edustream.features.marketplace.data.local.entities.QuestionEntity
import com.example.edustream.features.marketplace.data.local.entities.QuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_table WHERE courseId = :courseId ORDER BY orderIndex ASC")
    fun getQuizzesByCourseId(courseId: String): Flow<List<QuizEntity>>

    @Query("SELECT * FROM question_table WHERE quizId = :quizId")
    fun getQuestionsByQuizId(quizId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM quiz_table WHERE quizId = :quizId")
    suspend fun getQuizById(quizId: String): QuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)
}
