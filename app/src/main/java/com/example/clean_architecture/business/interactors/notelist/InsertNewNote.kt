package com.example.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import StateEvent
import UIComponentType
import android.provider.ContactsContract.Data
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class InsertNewNote(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource: NoteNetworkDataSource,
    private val noteFactory: NoteFactory
)
{
    fun insertNewNote(
        id:String?=null,
        title:String,
        stateEvent: StateEvent
    ):Flow<DataState<NoteListViewState>>
    {
        return flow{
            val newNote=noteFactory.createSingleNote(
                id=id?:UUID.randomUUID().toString(),
                title=title,
                body = ""
            )
            val cacheResult=noteCacheDataSource.insertNote(newNote)
            var cacheResponse:DataState<NoteListViewState>?=null
            if (cacheResult > 0)
            {
                val viewState=NoteListViewState(
                    newNote = newNote
                )
                cacheResponse=DataState.data(
                    response = Response(
                        message=INSERT_NOTE_SUCCESS,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success(),
                    ), data = viewState,
                    stateEvent = stateEvent
                )

            }else
            {
                cacheResponse=DataState.data(
                    response = Response(
                        message= INSERT_NOTE_FAILED,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success(),
                    ), data = null,
                    stateEvent = stateEvent
                )
            }
            emit(cacheResponse)
            updateNetword(cacheResponse.stateMessage?.response?.message,newNote)

        }
    }

    private suspend fun updateNetword(cachResponse: String?, newNote: Note) {
        if (cachResponse.equals(INSERT_NOTE_SUCCESS))
        {
            noteNetworkDataSource.insertOrUpdateNote(newNote)
        }

    }

    companion object{
        const val INSERT_NOTE_SUCCESS="successfully inserted note"
        const val INSERT_NOTE_FAILED="Insertion of Note failed"


    }



}