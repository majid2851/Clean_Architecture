package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import com.majid2851.clean_architecture.business.data.cache.FORCE_GENERAL_FAILURE
import com.majid2851.clean_architecture.business.data.cache.FORCE_NEW_NOTE_EXCEPTION
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.notelist.RestoreDeletedNote.Companion.RESTORE_NOTE_FAILURE
import com.example.clean_architecture.business.interactors.notelist.RestoreDeletedNote.Companion.RESTORE_NOTE_SUCCESS
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.UUID

class RestoreDeletedNoteTest
{
    /*
Test cases:
1. restoreNote_success_confirmCacheAndNetworkUpdated()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note
    c) Listen for success msg RESTORE_NOTE_SUCCESS from flow
    d) confirm note is in the cache
    e) confirm note is in the network "notes" node
    f) confirm note is not in the network "deletes" node
2. restoreNote_fail_confirmCacheAndNetworkUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force a failure)
    c) Listen for success msg RESTORE_NOTE_FAILED from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) create a new note and insert it into the "deleted" node of network
    b) restore that note (force an exception)
    c) Listen for success msg CACHE_ERROR_UNKNOWN from flow
    d) confirm note is not in the cache
    e) confirm note is not in the network "notes" node
    f) confirm note is in the network "deletes" node
 */

    // system in test
    private val restoreDeletedNote: RestoreDeletedNote

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
        restoreDeletedNote = RestoreDeletedNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
        )
    }

    @Test
    fun restoreNote_success_confirmCacheAndNetworkUpdated()
    {
        runBlocking()
        {
            //restore that note
            val note=noteFactory.createSingleNote(
                id = UUID.randomUUID().toString(),
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )
            noteNetworkDataSource.insertDeletedNote(note = note)
            val deletedNotes=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                deletedNotes.contains(note)==true
            }

            restoreDeletedNote.restoreDeletedNote(note, stateEvent =
                NoteListStateEvent.RestoreDeletedNoteEvent(note))
                .collect(
                    object :FlowCollector<DataState<NoteListViewState>?>
                    {
                        override suspend fun emit(value: DataState<NoteListViewState>?)
                        {
                            Assert.assertEquals(
                                value?.stateMessage?.response?.message,
                                RESTORE_NOTE_SUCCESS
                            )
                        }
                    }
                )
            val newDeletedNotes=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                newDeletedNotes.contains(note)==false
            }
        }
    }

    @Test
    fun restoreNote_fail_confirmCacheAndNetworkUnchanged()
    {
//        a) create a new note and insert it into the "deleted" node of network
//        b) restore that note (force a failure)
//        c) Listen for success msg RESTORE_NOTE_FAILED from flow
//        d) confirm note is not in the cache
//        e) confirm note is not in the network "notes" node
//        f) confirm note is in the network "deletes" node
        runBlocking()
        {
            val note=noteFactory.createSingleNote(
                id = FORCE_GENERAL_FAILURE,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )

            noteNetworkDataSource.insertDeletedNote(note)

            restoreDeletedNote.restoreDeletedNote(
                note = note,
                stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(note))
                .collect(object :FlowCollector<DataState<NoteListViewState>?>
                {
                    override suspend fun emit(value: DataState<NoteListViewState>?)
                    {
                        Assert.assertEquals(
                            value?.stateMessage?.response?.message,
                            RESTORE_NOTE_FAILURE
                        )
                    }
                })

            val allNotes=noteCacheDataSource.searchNotes("","",1)
            assertFalse {
                allNotes.contains(note)
            }
            val networkNotes=noteNetworkDataSource.getAllNotes()
            assertFalse {
                networkNotes.contains(note)
            }
            val addedToDeleted=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                addedToDeleted.contains(note)
            }

        }
    }

    @Test()
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    {
        runBlocking()
        {
            val note=noteFactory.createSingleNote(
                id = FORCE_NEW_NOTE_EXCEPTION,
                title = UUID.randomUUID().toString(),
                body = UUID.randomUUID().toString()
            )

            noteNetworkDataSource.insertDeletedNote(note)

            restoreDeletedNote.restoreDeletedNote(
                note = note,
                stateEvent = NoteListStateEvent.RestoreDeletedNoteEvent(note))
                .collect(object :FlowCollector<DataState<NoteListViewState>?>
                {
                    override suspend fun emit(value: DataState<NoteListViewState>?)
                    {
                        assert(
                            value?.stateMessage?.response?.message
                                ?.contains(CACHE_ERROR_UNKNOWN)?:false//???????
                        )
                    }
                })

            val allNotes=noteCacheDataSource.searchNotes("","",1)
            assertFalse {
                allNotes.contains(note)
            }
            val networkNotes=noteNetworkDataSource.getAllNotes()
            assertFalse {
                networkNotes.contains(note)
            }
            val addedToDeleted=noteNetworkDataSource.getDeletedNote()
            assertTrue{
                addedToDeleted.contains(note)
            }

        }
    }


}