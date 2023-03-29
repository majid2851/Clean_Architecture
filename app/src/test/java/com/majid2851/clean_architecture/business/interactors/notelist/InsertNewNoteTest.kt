package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import com.majid2851.clean_architecture.business.data.cache.FORCE_GENERAL_FAILURE
import com.majid2851.clean_architecture.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_FAILED
import com.example.clean_architecture.business.interactors.notelist.InsertNewNote.Companion.INSERT_NOTE_SUCCESS
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import java.util.UUID


/*
Test cases:
1. insertNote_success_confirmNetworkAndCacheUpdated()
    a) insert a new note
    b) listen for INSERT_NOTE_SUCCESS emission from flow
    c) confirm cache was updated with new note
    d) confirm network was updated with new note
2. insertNote_fail_confirmNetworkAndCacheUnchanged()
    a) insert a new note
    b) force a failure (return -1 from db operation)
    c) listen for INSERT_NOTE_FAILED emission from flow
    e) confirm cache was not updated
    e) confirm network was not updated
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) insert a new note
    b) force an exception
    c) listen for CACHE_ERROR_UNKNOWN emission from flow
    e) confirm cache was not updated
    e) confirm network was not updated
 */
class InsertNewNoteTest
{
    // system in test
    private val insertNewNote: InsertNewNote

    // dependencies
    private val dependencyContainer: DependencyContainer
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
    private val noteNetworkDataSource: NoteNetworkDataSource
    private val noteFactory: NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        noteNetworkDataSource = dependencyContainer.noteNetworkDataSource
        noteFactory = dependencyContainer.noteFactory
        insertNewNote = InsertNewNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
            noteFactory = noteFactory
        )
    }

    @Test
    fun insertNote_success_confirmNetworkAndCacheUpdated()
    {
        runBlocking()
        {
            val newNote=noteFactory.createSingleNote(
                id = null,
                title = UUID.randomUUID().toString(),
            )
            insertNewNote.insertNewNote(
                id = newNote.id,
                title = newNote.title,
                stateEvent = NoteListStateEvent.InsertNewNoteEvent(title =newNote.title )
                ).collect(
                    object :FlowCollector<DataState<NoteListViewState>>{
                        override suspend fun emit(value: DataState<NoteListViewState>) {
                            Assert.assertEquals(
                                value.stateMessage?.response?.message,
                                INSERT_NOTE_SUCCESS
                            )
                        }

                    }
                )
            //confirm cache was updated
            val cacheNoteThatWasInserted=noteCacheDataSource.searchNoteById(
                newNote.id
            )
            Assert.assertTrue(cacheNoteThatWasInserted==newNote)

            val networkNoteThatWasInserted=noteNetworkDataSource.searchNote(newNote)
            Assert.assertTrue(networkNoteThatWasInserted==newNote)
        }
    }

    @Test
    fun insertNote_fail_confirmNetworkAndCacheUnchanged()
    {
        runBlocking()
        {
            val newNote=noteFactory.createSingleNote(
                id = FORCE_GENERAL_FAILURE,
                title = UUID.randomUUID().toString(),
            )
            insertNewNote.insertNewNote(
                id = newNote.id,
                title = newNote.title,
                stateEvent = NoteListStateEvent.InsertNewNoteEvent(title =newNote.title )
            ).collect(
                object :FlowCollector<DataState<NoteListViewState>>{
                    override suspend fun emit(value: DataState<NoteListViewState>) {
                        Assert.assertEquals(
                            value.stateMessage?.response?.message,
                            INSERT_NOTE_FAILED
                        )
                    }

                }
            )
            //confirm cache was updated
            val cacheNoteThatWasInserted=noteCacheDataSource.searchNoteById(
                newNote.id
            )
            Assert.assertTrue(cacheNoteThatWasInserted==null)

            val networkNoteThatWasInserted=noteNetworkDataSource.searchNote(newNote)
            Assert.assertTrue(networkNoteThatWasInserted==null)
        }


    }

    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    {
        runBlocking()
        {
            val newNote=noteFactory.createSingleNote(
                id = FORCE_NEW_NOTE_EXCEPTION,
                title = UUID.randomUUID().toString(),
            )
            insertNewNote.insertNewNote(
                id = newNote.id,
                title = newNote.title,
                stateEvent = NoteListStateEvent.InsertNewNoteEvent(title =newNote.title )
            ).collect(
                object :FlowCollector<DataState<NoteListViewState>>{
                    override suspend fun emit(value: DataState<NoteListViewState>) {
                        assert(
                            value.stateMessage?.response
                                ?.message?.contains(CACHE_ERROR_UNKNOWN)?:false
                        )
                    }

                }
            )
            //confirm cache was updated
            val cacheNoteThatWasInserted=noteCacheDataSource.searchNoteById(
                newNote.id
            )
            Assert.assertTrue(cacheNoteThatWasInserted==null)

            val networkNoteThatWasInserted=noteNetworkDataSource.searchNote(newNote)
            Assert.assertTrue(networkNoteThatWasInserted==null)
        }
    }



}