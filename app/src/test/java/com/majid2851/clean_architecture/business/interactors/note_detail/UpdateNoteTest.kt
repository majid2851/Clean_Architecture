package com.majid2851.clean_architecture.business.interactors.note_detail

import DataState
import com.majid2851.clean_architecture.business.data.cache.FORCE_UPDATE_NOTE_EXCEPTION
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.notedetail.UpdateNote
import com.example.clean_architecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_FAILED
import com.example.clean_architecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_SUCCESS
import com.example.clean_architecture.business.interactors.notelist.InsertNewNote
import com.example.clean_architecture.framework.presentation.notedetail.state.NoteDetailStateEvent
import com.example.clean_architecture.framework.presentation.notedetail.state.NoteDetailViewState
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.google.android.gms.common.data.DataBufferSafeParcelable
import com.majid2851.clean_architecture.business.data.cache.CacheErrors.CACHE_ERROR_UNKNOWN
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.UUID

class UpdateNoteTest
{
    /*
Test cases:
1. updateNote_success_confirmNetworkAndCacheUpdated()
    a) select a random note from the cache
    b) update that note
    c) confirm UPDATE_NOTE_SUCCESS msg is emitted from flow
    d) confirm note is updated in network
    e) confirm note is updated in cache
2. updateNote_fail_confirmNetworkAndCacheUnchanged()
    a) attempt to update a note, fail since does not exist
    b) check for failure message from flow emission
    c) confirm nothing was updated in the cache
3. throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    a) attempt to update a note, force an exception to throw
    b) check for failure message from flow emission
    c) confirm nothing was updated in the cache
 */
    // system in test
    private val updateNote: UpdateNote

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
        updateNote = UpdateNote(
            noteCacheDataSource = noteCacheDataSource,
            noteNetworkDataSource = noteNetworkDataSource,
        )
    }

    @Test
    fun updateNoteWorkCorrectly()
    {
        runBlocking()
        {
            val note=noteCacheDataSource.searchNotes("","",1).get(3)
            val newNote=Note(note.id,"title2851","body2851","","")
            updateNote.updateNote(
                note= newNote,
                stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
            ).collect(object :FlowCollector<DataState<NoteDetailViewState>?>
            {
                override suspend fun emit(value: DataState<NoteDetailViewState>?)
                {
                    Assert.assertEquals(
                        value?.stateMessage?.response?.message,
                        UPDATE_NOTE_SUCCESS
                    )
                }
            })


            val newNetworkNote=noteNetworkDataSource.searchNote(newNote)
            assertTrue()
            {
                newNetworkNote?.title.equals("title2851")
                newNetworkNote?.id.equals(note.id)
            }

            val newCacheNote=noteCacheDataSource.searchNotes("","",1).get(3)
            assertTrue()
            {
               newCacheNote.title.equals("title2851")
               newCacheNote.id.equals(note.id)
            }
        }
    }

    @Test
    fun updateNote_fail_confirmNetworkAndCacheUnchanged()
    {
        runBlocking()
        {
            val note=noteFactory.createSingleNote(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
                )
            val newNote=Note(note.id,"title2851","body2851","","")
            updateNote.updateNote(
                note= newNote,
                stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
            ).collect(object :FlowCollector<DataState<NoteDetailViewState>?>
            {
                override suspend fun emit(value: DataState<NoteDetailViewState>?)
                {
                    Assert.assertEquals(
                        value?.stateMessage?.response?.message,
                        UPDATE_NOTE_FAILED
                    )
                }
            })


            val allNetworkNote=noteNetworkDataSource.getAllNotes()
            assertFalse()
            {
                allNetworkNote.contains(newNote)
            }

            val allCacheNote=noteCacheDataSource.searchNotes("","",1)
            assertFalse()
            {
                allCacheNote.contains(newNote)
            }
        }



    }

    @Test
    fun throwException_checkGenericError_confirmNetworkAndCacheUnchanged()
    {
        runBlocking()
        {
            val note=noteFactory.createSingleNote(
                id= FORCE_UPDATE_NOTE_EXCEPTION,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
            )
            val newNote=Note(note.id,"title2851","body2851","","")
            updateNote.updateNote(
                note= newNote,
                stateEvent = NoteDetailStateEvent.UpdateNoteEvent()
            ).collect(object :FlowCollector<DataState<NoteDetailViewState>?>
            {
                override suspend fun emit(value: DataState<NoteDetailViewState>?)
                {

                    assert(
                        value?.stateMessage?.response?.message?.contains(CACHE_ERROR_UNKNOWN,)?:false
                    )
                }
            })


            val allNetworkNote=noteNetworkDataSource.getAllNotes()
            assertFalse()
            {
                allNetworkNote.contains(newNote)
            }

            val allCacheNote=noteCacheDataSource.searchNotes("","",1)
            assertFalse()
            {
                allCacheNote.contains(newNote)
            }
        }

    }


}