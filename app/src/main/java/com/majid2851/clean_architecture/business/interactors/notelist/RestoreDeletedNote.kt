package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import UIComponentType
import com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.mag2851.clean_architecture.business.domain.state.ViewState
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RestoreDeletedNote(
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource,
    private val noteNetworkDataSource:NoteNetworkDataSource
)
{
    fun restoreDeletedNote(
        note:Note,
        stateEvent: StateEvent
    ):Flow<DataState<NoteListViewState>?> =flow()
    {
        val cacheResult= safeCacheCall(IO)
        {
            noteCacheDataSource.insertNote(note)
        }
        val response=object : com.majid2851.clean_architecture.business.data.cache.CacheResponseHandler<NoteListViewState, Long>(
            response=cacheResult,
            stateEvent=stateEvent
        )
        {
            override suspend fun handleSuccess(resultObj: Long): DataState<NoteListViewState>
            {
                return if (resultObj > 0) {
                    val viewState = NoteListViewState(
                        notePendingDelete = NoteListViewState.NotePendingDelete(
                            note = note
                        )
                    )
                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_SUCCESS,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = viewState,
                        stateEvent = stateEvent
                    )

                } else {
                    DataState.data(
                        response = Response(
                            message = RESTORE_NOTE_FAILURE,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Success()
                        ),
                        data = null,
                        stateEvent = stateEvent
                    )
                }
            }

        }.getResult()
        emit(response)
        updateNetwork(response?.stateMessage?.response?.message,note)
    }
    private suspend fun updateNetwork(response:String?,note:Note)
    {
        if (response.equals(RESTORE_NOTE_SUCCESS))
        {
            //insert into the notes nodej
            safeApiCall {
                noteNetworkDataSource.insertOrUpdateNote(note)
            }
            //remove from deleted note
            safeApiCall {
                noteNetworkDataSource.deleteDeletedNote(note)
            }
        }
    }
    companion object{
        val RESTORE_NOTE_SUCCESS="Successfully restored the deleted note"
        val RESTORE_NOTE_FAILURE="Failed to restore the delted note"
    }







}