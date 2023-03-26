package com.example.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import UIComponentType
import com.example.clean_architecture.business.data.cache.CacheResponseHandler
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.util.safeApiCall
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteMultipleNotes(
    private val noteCacheDataSource: NoteCacheDataSource,
    private val noteNetworkDataSource:NoteNetworkDataSource
)
{
    private var onDeleteError:Boolean =false

    fun deleteNote(
        notes:List<Note>,
        stateEvent:StateEvent
    ):Flow<DataState<NoteListViewState>?> =flow()
    {
        val successFulDelete:ArrayList<Note> = ArrayList()
        for (note in notes)
        {
            val cacheResult= safeCacheCall(IO)
            {
                noteCacheDataSource.deleteNote(note.id)
            }
            val response= object :CacheResponseHandler<NoteListViewState,Int>(
                response=cacheResult,
                stateEvent=stateEvent
            ){
                override fun handleSuccess(resultObj: Int): DataState<NoteListViewState>?
                {
                    if (resultObj<0)
                    {
                        onDeleteError=true
                    }else{
                        successFulDelete.add(note)
                    }
                    return null

                }

            }.getResult()

            if (response.stateMessage?.response?.message?.
                    contains(stateEvent.errorInfo())==true)
            {
                onDeleteError=true
            }
        }
        if (onDeleteError==true){
            emit(
                DataState.data<NoteListViewState>(
                    response = Response(
                        message = DELETE_NOTES_ERRORS,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Success()
                    ),
                    data=null,
                    stateEvent=stateEvent
                )
            )
        }else{
            emit(
                DataState.data<NoteListViewState>(
                    response = Response(
                        message = DELETE_NOTES_SUCCESS,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Success()
                    ),
                    data=null,
                    stateEvent=stateEvent
                )
            )
        }
        updateNetwork(successFulDelete)

    }

    private suspend fun updateNetwork(successFulDelete: ArrayList<Note>)
    {
        for (note in successFulDelete)
        {
            //delete from notes node
            safeApiCall {
                noteNetworkDataSource.deleteNote(note.id)
            }
            safeApiCall {
                noteNetworkDataSource.insertDeletedNote(note)
            }
        }

    }

    companion object{
        val DELETE_NOTES_SUCCESS = "Successfully deleted notes."
        val DELETE_NOTES_ERRORS = "Not all the notes you selected were deleted. There was some errors."
        val DELETE_NOTES_YOU_MUST_SELECT = "You haven't selected any notes to delete."
        val DELETE_NOTES_ARE_YOU_SURE = "Are you sure you want to delete these?"
    }




}