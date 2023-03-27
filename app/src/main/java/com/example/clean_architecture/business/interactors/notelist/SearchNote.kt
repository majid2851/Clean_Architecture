package com.example.clean_architecture.business.interactors.notelist

import DataState
import MessageType
import Response
import UIComponentType
import com.example.clean_architecture.business.data.cache.CacheResponseHandler
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.domain.model.Note
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.util.safeCacheCall
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchNote(
    private val noteCacheDataSource: NoteCacheDataSource
)
{
    fun searchNote(
        query:String,
        filterAndOrder:String,
        page:Int,
        stateEvent:StateEvent
    ): Flow<DataState<NoteListViewState>?> = flow{
        var updatedPage=page
        if (page<=0)
        {
            updatedPage=1
        }
        val cacheResult= safeCacheCall(IO)
        {
            noteCacheDataSource.searchNotes(
                query=query,
                filterAndOrder=filterAndOrder,
                page=updatedPage,

            )
        }

        val response=object :CacheResponseHandler<NoteListViewState,List<Note>>(
            response=cacheResult,
            stateEvent=stateEvent
        ){
            override suspend fun handleSuccess(resultObj: List<Note>): DataState<NoteListViewState>
            {
                var message:String?= SEARCH_NOTES_SUCCESS
                var uiComponentType:UIComponentType=UIComponentType.None()
                if (resultObj.size==0)
                {
                    message= SEARCH_NOTES_NO_MATCHING_RESULTS
                    uiComponentType=UIComponentType.Toast()
                }
                return DataState.data(
                    response = Response(
                            message=message,
                            uiComponentType=uiComponentType,
                            messageType = MessageType.Success()
                        ),
                    data = NoteListViewState(
                        noteList = ArrayList(resultObj)
                    ),
                    stateEvent = stateEvent
                )

            }

        }.getResult()
        emit(response)

    }

    companion object{
        val SEARCH_NOTES_SUCCESS = "Successfully retrieved list of notes."
        val SEARCH_NOTES_NO_MATCHING_RESULTS = "There are no notes that match that query."
        val SEARCH_NOTES_FAILED = "Failed to retrieve the list of notes."

    }

}