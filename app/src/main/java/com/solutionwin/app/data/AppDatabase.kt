package com.solutionwin.app.data

import androidx.room.Database
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.solutionwin.app.domain.Note
import com.solutionwin.app.domain.SportEvent
import com.solutionwin.app.domain.SportEventType
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val createdAt: Long,
)

@Entity(tableName = "sport_events")
data class SportEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String,
    val startAt: Long,
    val reminderMinutes: Int,
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<NoteEntity>>

    @Insert
    suspend fun insert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}

@Dao
interface SportEventDao {
    @Query("SELECT * FROM sport_events ORDER BY startAt ASC")
    fun observeAll(): Flow<List<SportEventEntity>>

    @Insert
    suspend fun insert(event: SportEventEntity): Long

    @Delete
    suspend fun delete(event: SportEventEntity)
}

@Database(
    entities = [NoteEntity::class, SportEventEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun sportEventDao(): SportEventDao
}

fun NoteEntity.toDomain() = Note(id, title, body, createdAt)
fun Note.toEntity() = NoteEntity(id, title, body, createdAt)

fun SportEventEntity.toDomain() = SportEvent(
    id = id,
    title = title,
    type = runCatching { SportEventType.valueOf(type) }.getOrDefault(SportEventType.MATCH),
    startAt = startAt,
    reminderMinutes = reminderMinutes,
)

fun SportEvent.toEntity() = SportEventEntity(id, title, type.name, startAt, reminderMinutes)
