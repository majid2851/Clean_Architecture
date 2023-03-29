package com.majid2851.clean_architecture.business.interactors.common

import DataState
import com.majid2851.clean_architecture.business.data.cache.FORCE_DELETE_NOTE_EXCEPTION
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_FAILED
import com.example.clean_architecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_SUCCESS
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import java.util.UUID


/*
Test cases:
1. deleteNote_success_confirmNetworkUpdated()
    a) delete a note
    b) check for success message from flow emission
    c) confirm note was deleted from "notes" node in network
    d) confirm note was added to "deletes" node in network
2. deleteNote_fail_confirmNetworkUnchanged()
    a) attempt to delete a note, fail since does not exist
    b) check for failure message from flow emission
    c) confirm network was not changed
3. throwException_checkGenericError_confirmNetworkUnchanged()
    a) attempt to delete a note, force an exception to throw
    b) check for failure message from flow emission
    c) confirm network was not changed
 */

@InternalCoroutinesApi
class DeleteNoteTest {
    // system in test
    private val deleteNote:DeleteNote<NoteListViewState>
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
        deleteNote= DeleteNote(noteCacheDataSource,noteNetworkDataSource)
    }




    @Test
    fun deleteNote_success_confirmNetworkUpdated()
    {
        runBlocking()
        {
            val noteToDelete=noteCacheDataSource.searchNotes("","",1).get(0)
            deleteNote.deleteNote(noteToDelete, stateEvent
            = NoteListStateEvent.DeleteNoteEvent(noteToDelete))
                .collect(
                    object : FlowCollector<DataState<NoteListViewState>?> {
                        override suspend fun emit(value: DataState<NoteListViewState>?) {
                            Assert.assertEquals(
                                value?.stateMessage?.response?.message,
                                DELETE_NOTE_SUCCESS
                            )
                        }
                    }
                )
            // confirm was deleted from 'notes' network
            val searchInNetwork=noteNetworkDataSource.searchNote(noteToDelete)
            assertTrue{
                searchInNetwork!=noteToDelete
            }
            //confirm was inserted into 'deleted notes' node
            val deletedNotes=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                deletedNotes.contains(noteToDelete)==true
            }
        }
    }
    @Test
    fun deleteNote_fail_confirmNetworkUnchanged()
    {
        runBlocking ()
        {
            val noteToDelete=Note(
                id=UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString(),
                updated_at = UUID.randomUUID().toString(),
                created_at = UUID.randomUUID().toString()
            )
            deleteNote.deleteNote(noteToDelete, stateEvent
            = NoteListStateEvent.DeleteNoteEvent(noteToDelete))
                .collect(
                    object : FlowCollector<DataState<NoteListViewState>?> {
                        override suspend fun emit(value: DataState<NoteListViewState>?) {
                            Assert.assertEquals(
                                value?.stateMessage?.response?.message,
                                DELETE_NOTE_FAILED
                            )
                        }
                    }
                )
            val deletedNotes=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                deletedNotes.contains(noteToDelete)==false
            }
        }
    }

    @Test
    fun throwException_checkGenericError_confirmNetworkUnchanged()
    {
        runBlocking ()
        {
            val noteToDelete=Note(
                id=UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString(),
                updated_at = UUID.randomUUID().toString(),
                created_at = UUID.randomUUID().toString()
            )
            deleteNote.deleteNote(noteToDelete, stateEvent
            = NoteListStateEvent.DeleteNoteEvent(noteToDelete))
                .collect(
                    object : FlowCollector<DataState<NoteListViewState>?> {
                        override suspend fun emit(value: DataState<NoteListViewState>?) {
                            Assert.assertEquals(
                                value?.stateMessage?.response?.message,
                                DELETE_NOTE_FAILED
                            )
                        }
                    }
                )
            val deletedNotes=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                deletedNotes.contains(noteToDelete)==false
            }
        }
    }




}