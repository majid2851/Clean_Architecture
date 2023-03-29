package com.majid2851.clean_architecture.framework.datasource.cache.implementation

import com.majid2851.clean_architecture.framework.datasource.cache.database.NoteDao
import com.majid2851.clean_architecture.framework.datasource.cache.database.returnOrderedQuery
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.framework.datasource.cache.abstraction.NoteDaoService
import com.majid2851.clean_architecture.framework.datasource.cache.mapper.CacheMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDaoServiceImpl @Inject constructor(
    private val noteDao:NoteDao,
    private val noteMapper:CacheMapper,
    private val dateUtil:DateUtil
):NoteDaoService
{
    //the main reason for mapping is unitTest , because unitTest just know Note objet,
    //not NoteCacheEntity object
    override suspend fun insertNote(note: Note): Long {
        return noteDao.insertNote(noteMapper.mapToEntity(note))
    }

    override suspend fun insertNotes(notes: List<Note>): LongArray {
        return noteDao.insertNotes(
            noteMapper.noteListToEntityList(notes)
        )
    }

    override suspend fun searchNoteById(id: String): Note? {
        return noteDao.searchNoteById(id)
            ?.let {
                noteMapper.mapFromEntity(it)
            }

    }

    override suspend fun updateNote(primaryKey: String, title: String, body: String?): Int {
        return noteDao.updateNote(
            primaryKey=primaryKey,
            title=title,
            body=body,
            updated_at = dateUtil.getCurrentTimeStamp()
        )
    }

    override suspend fun deleteNote(primaryKey: String): Int {
        return noteDao.deleteNote(primaryKey)
    }

    override suspend fun deleteNotes(notes: List<Note>): Int {
        val ids=notes.mapIndexed{index, value ->  value.id}
        return noteDao.deleteNotes(ids)
    }

    override suspend fun searchNotes(): List<Note> {
         return noteMapper.entityListToNoteList(
             noteDao.searchNotes()
         )
    }

    override suspend fun getAllNotes(): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotes()
        )
    }

    override suspend fun searchNotesOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateDESC(
                query=query,
                page=page,
                pageSize=pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByDateASC(
                query=query,
                page=page,
                pageSize=pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByTitleDESC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleDESC(
                query=query,
                page=page,
                pageSize=pageSize
            )
        )
    }

    override suspend fun searchNotesOrderByTitleASC(
        query: String,
        page: Int,
        pageSize: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.searchNotesOrderByTitleASC(
                query=query,
                page=page,
                pageSize=pageSize
            )
        )
    }

    override suspend fun getNumNotes(): Int {
       return noteDao.getNumNotes()
    }

    override suspend fun returnOrderedQuery(
        query: String,
        filterAndOrder: String,
        page: Int
    ): List<Note> {
        return noteMapper.entityListToNoteList(
            noteDao.returnOrderedQuery(
                query=query,
                page=page,
                filterAndOrder = filterAndOrder
            )
        )
    }


}