package com.example.edustream.features.marketplace.data.remote.api

import com.example.edustream.features.marketplace.data.remote.dto.CourseDto
import com.example.edustream.features.marketplace.data.remote.dto.LectureDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CourseApiService {
    @GET("courses")
    suspend fun getCourses(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("category") category: String? = null
    ): List<CourseDto>

    @GET("courses/featured")
    suspend fun getFeaturedCourses(): List<CourseDto>

    @GET("courses/search")
    suspend fun searchCourses(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Query("minRating") minRating: Float? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): List<CourseDto>

    @GET("courses/{courseId}")
    suspend fun getCourseDetail(
        @Path("courseId") courseId: String
    ): CourseDto

    @GET("courses/{courseId}/lectures")
    suspend fun getLectures(
        @Path("courseId") courseId: String
    ): List<LectureDto>
}
