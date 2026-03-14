package com.example.edustream.features.marketplace.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.edustream.features.marketplace.data.local.dao.CourseDao
import com.example.edustream.features.marketplace.data.local.dao.LectureDao
import com.example.edustream.features.marketplace.data.local.entities.CourseEntity
import com.example.edustream.features.marketplace.data.local.entities.LectureEntity
import com.example.edustream.features.marketplace.data.remote.api.CourseApiService
import com.example.edustream.features.marketplace.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepositoryImpl @Inject constructor(
    private val courseDao: CourseDao,
    private val lectureDao: LectureDao,
    private val apiService: CourseApiService
) : CourseRepository {

    private val dummyCourses = listOf(
        CourseEntity("1", "Android Development with Jetpack Compose", "Master modern Android UI development.", "instr_1", "https://developer.android.com/static/images/courses/android-basics-compose.png", "", 4999.0, 9999.0, "Development", 4.8f, 1200, 50, 36000, false, System.currentTimeMillis()),
        CourseEntity("2", "UI/UX Design Essentials", "Learn to create beautiful user interfaces.", "instr_2", "https://mir-s3-cdn-cf.behance.net/project_modules/max_1200/27cc6a105243111.5f749a099092b.png", "", 2999.0, null, "Design", 4.5f, 800, 30, 25000, true, System.currentTimeMillis() - 86400000),
        CourseEntity("3", "Business Strategy for Beginners", "Fundamentals of running a successful business.", "instr_3", "https://img-c.udemycdn.com/course/750x422/123456_1234.jpg", "", 1999.0, 3999.0, "Business", 4.2f, 500, 20, 15000, false, System.currentTimeMillis() - 172800000)
    )

    private val dummyLectures = listOf(
        LectureEntity("l1", "1", "Introduction to Compose", "What is Jetpack Compose?", "https://storage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", 300, 1, true),
        LectureEntity("l2", "1", "Composables and State", "Deep dive into state management.", "https://storage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", 600, 2, false),
        LectureEntity("l3", "2", "Introduction to Figma", "Getting started with Figma.", "https://storage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4", 450, 1, true)
    )

    override fun getCourses(category: String?): Flow<PagingData<CourseEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { 
                if (category == null) courseDao.getAllCourses() 
                else courseDao.getCoursesByCategory(category)
            }
        ).flow
    }

    override fun getCourseDetail(courseId: String): Flow<CourseEntity?> {
        return courseDao.getCourseById(courseId)
    }

    override fun getLectures(courseId: String): Flow<List<LectureEntity>> {
        return lectureDao.getLecturesByCourseId(courseId)
    }

    override fun getPurchasedCourses(): Flow<List<CourseEntity>> {
        return courseDao.getPurchasedCourses()
    }

    override fun searchCourses(query: String, category: String?): Flow<PagingData<CourseEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { courseDao.getAllCourses() }
        ).flow
    }

    override suspend fun refreshCourses() {
        courseDao.insertAll(dummyCourses)
    }

    override suspend fun refreshLectures(courseId: String) {
        lectureDao.insertAll(dummyLectures.filter { it.courseId == courseId })
    }

    override suspend fun purchaseCourse(courseId: String) {
        courseDao.markAsPurchased(courseId)
    }
}
