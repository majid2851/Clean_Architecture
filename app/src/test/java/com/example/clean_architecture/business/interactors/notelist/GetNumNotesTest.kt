package com.example.clean_architecture.business.interactors.notelist

import DataState
import com.example.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.example.clean_architecture.business.di.DependencyContainer
import com.example.clean_architecture.business.domain.model.NoteFactory
import com.example.clean_architecture.business.interactors.notelist.GetNumNotes.Companion.GET_NUM_NOTES_SUCCESS
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.example.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

/*
*1)-getNumber_success_confirmCorrect
*a)-getNumber of notes in cache
* b)-listen for get_num_success from flow emission
* c)-compare with number of notes in the fake data set
*
*
*
* */
class GetNumNotesTest
{
    //system in test
    private val getNumNotes:GetNumNotes

    //dependencies
    private val noteCacheDataSource: NoteCacheDataSource
    private val dependencyContainer: DependencyContainer
    private val noteFactory:NoteFactory

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteFactory=dependencyContainer.noteFactory
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        getNumNotes= GetNumNotes(noteCacheDataSource)
    }

    @Test
    fun getNumber_success_confirmCorrect()
    {
        runBlocking()
        {
            var numNotes=0
            getNumNotes.getNumNotes(
                stateEvent = NoteListStateEvent.GetNumNotesInCacheEvent()
            ).collect(object :FlowCollector<DataState<NoteListViewState>?>{
                override suspend fun emit(value: DataState<NoteListViewState>?)
                {
                    Assert.assertEquals(
                        value?.stateMessage?.response?.message,
                        GET_NUM_NOTES_SUCCESS
                    )
                    numNotes=value?.data?.numNotesInCache?:0
                }
            })
            val actualNumNotesInCache=noteCacheDataSource.getNumNotes()
            assertTrue{actualNumNotesInCache==numNotes}
        }
    }









}