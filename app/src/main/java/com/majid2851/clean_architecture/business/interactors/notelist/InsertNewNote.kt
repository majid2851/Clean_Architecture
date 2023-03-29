package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import UIComponentType
import android.provider.ContactsContract.Data
import com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class InsertNewNote(
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource,
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
            val cacheResult= safeCacheCall(IO)
            {
                noteCacheDataSource.insertNote(newNote)
            }
            val cacheResponse=object :
                com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler<NoteListViewState, Long>(
                response=cacheResult,
                stateEvent=stateEvent
            ){
                override suspend fun handleSuccess(resultObj: Long): DataState<NoteListViewState>
                {
                    return if (resultObj > 0)
                    {
                        val viewState=NoteListViewState(newNote = newNote)
                        DataState.data(
                            response = Response(
                                message=INSERT_NOTE_SUCCESS,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Success(),
                            ), data = viewState,
                            stateEvent = stateEvent
                        )

                    }else
                    {
                        DataState.data(
                            response = Response(
                                message= INSERT_NOTE_FAILED,
                                uiComponentType = UIComponentType.Toast(),
                                messageType = MessageType.Success(),
                            ), data = null,
                            stateEvent = stateEvent
                        )
                    }
                }

            }.getResult()


            emit(cacheResponse!!)
            updateNetword(cacheResponse.stateMessage?.response?.message,newNote)

        }
    }

    private suspend fun updateNetword(cachResponse: String?, newNote: Note) {
        if (cachResponse.equals(INSERT_NOTE_SUCCESS))
        {
            safeApiCall(IO)
            {
                noteNetworkDataSource.insertOrUpdateNote(newNote)
            }

        }

    }

    companion object{
        const val INSERT_NOTE_SUCCESS="successfully inserted note"
        const val INSERT_NOTE_FAILED="Insertion of Note failed"


    }



}