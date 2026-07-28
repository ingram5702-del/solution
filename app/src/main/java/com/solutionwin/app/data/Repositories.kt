package com.solutionwin.app.data

import com.solutionwin.app.domain.Note
import com.solutionwin.app.domain.SportEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val dao: NoteDao,
) {
    fun observeAll(): Flow<List<Note>> = dao.observeAll().map { notes -> notes.map(NoteEntity::toDomain) }

    suspend fun add(title: String, body: String) {
        dao.insert(NoteEntity(title = title.trim(), body = body.trim(), createdAt = System.currentTimeMillis()))
    }

    suspend fun delete(note: Note) = dao.delete(note.toEntity())
}

@Singleton
class SportEventRepository @Inject constructor(
    private val dao: SportEventDao,
) {
    fun observeAll(): Flow<List<SportEvent>> = dao.observeAll().map { events -> events.map(SportEventEntity::toDomain) }

    suspend fun add(event: SportEvent): SportEvent {
        val id = dao.insert(event.toEntity())
        return event.copy(id = id)
    }

    suspend fun delete(event: SportEvent) = dao.delete(event.toEntity())
}
