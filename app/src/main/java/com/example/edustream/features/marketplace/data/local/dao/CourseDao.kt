package com.example.edustream.features.marketplace.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM course_table ORDER BY createdAt DESC")
    fun getAllCourses(): PagingSource<Int, CourseEntity>

    @Query("SELECT * FROM course_table WHERE category = :category ORDER BY createdAt DESC")
    fun getCoursesByCategory(category: String): PagingSource<Int, CourseEntity>

    @Query("SELECT * FROM course_table WHERE courseId = :courseId")
    fun getCourseById(courseId: String): Flow<CourseEntity?>

    @Query("SELECT * FROM course_table WHERE isPurchased = 1")
    fun getPurchasedCourses(): Flow<List<CourseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("UPDATE course_table SET isPurchased = 1 WHERE courseId = :courseId")
    suspend fun markAsPurchased(courseId: String)

    @Query("DELETE FROM course_table")
    suspend fun clearAll()
}
