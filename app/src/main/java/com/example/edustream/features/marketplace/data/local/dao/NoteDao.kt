package com.example.edustream.features.marketplace.data.local.dao

import androidx.room.*
import com.example.edustream.features.marketplace.data.local.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table WHERE courseId = :courseId ORDER BY createdAt ASC")
    fun getNotesByCourseId(courseId: String): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NoteEntity>)

    @Query("DELETE FROM note_table WHERE courseId = :courseId")
    suspend fun deleteNotesByCourseId(courseId: String)
}
