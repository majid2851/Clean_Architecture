package com.majid2851.clean_architecture.business.interactors.notelist

import DataState
import com.majid2851.clean_architecture.framework.datasource.cache.database.ORDER_BY_ASC_DATE_UPDATED
import com.majid2851.clean_architecture.business.data.cache.FORCE_SEARCH_NOTES_EXCEPTION
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.di.DependencyContainer
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.interactors.notelist.SearchNote.Companion.SEARCH_NOTES_NO_MATCHING_RESULTS
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.business.data.cache.CacheErrors
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class SearchNotesTest
{
    /*
    Test cases:
    1. blankQuery_success_confirmNotesRetrieved()
        a) query with some default search options
        b) listen for SEARCH_NOTES_SUCCESS emitted from flow
        c) confirm notes were retrieved
        d) confirm notes in cache match with notes that were retrieved
    2. randomQuery_success_confirmNoResults()
        a) query with something that will yield no results
        b) listen for SEARCH_NOTES_NO_MATCHING_RESULTS emitted from flow
        c) confirm nothing was retrieved
        d) confirm there is notes in the cache
    3. searchNotes_fail_confirmNoResults()
        a) force an exception to be thrown
        b) listen for CACHE_ERROR_UNKNOWN emitted from flow
        c) confirm nothing was retrieved
        d) confirm there is notes in the cache
     */
    private val searchNotes:SearchNote
    private val noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
    private val dependencyContainer: DependencyContainer

    init {
        dependencyContainer = DependencyContainer()
        dependencyContainer.build()
        noteCacheDataSource = dependencyContainer.noteCacheDataSource
        searchNotes=SearchNote(noteCacheDataSource)
    }

    @Test
    fun blankQuery_success_confirmNotesRetrieved()
    {
        runBlocking()
        {
            val query=""
            var results:ArrayList<Note>? = null
            searchNotes.searchNote(
                query=query,
                filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
                page=1,
                stateEvent = NoteListStateEvent.SearchNotesEvent()
            ).collect(object :FlowCollector<DataState<NoteListViewState>?>
            {
                override suspend fun emit(value: DataState<NoteListViewState>?)
                {
                    Assert.assertEquals(
                        value?.stateMessage?.response?.message,
                        SearchNote.SEARCH_NOTES_SUCCESS
                    )
                    value?.data?.noteList?.let {list->
                        results= ArrayList(list)
                    }
                }
            })
            //confirm notes were retrieved
            Assert.assertTrue(
                results!=null
            )
            //confirm notes in cache match with notes that were retrieved
            val noteInCache=noteCacheDataSource.searchNotes(
                query=query,
                filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
                page = 1
            )
            Assert.assertTrue(results?.containsAll(noteInCache)?:false)
        }
    }
    @Test
    fun randomQuery_success_confirmNoResults() = runBlocking {
        val query = "hthrthrgrkgenrogn843nn4u34n934v53454hrth"
        var results: ArrayList<Note>? = null
        searchNotes.searchNote(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object: FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                Assert.assertEquals(
                    value?.stateMessage?.response?.message,
                    SEARCH_NOTES_NO_MATCHING_RESULTS
                )
                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
            }
        })
        // confirm nothing was retrieved
        assertTrue { results?.run { size == 0 }?: true }
        // confirm there is notes in the cache
        val notesInCache = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )
        assertTrue { notesInCache.size > 0}
    }

    @Test
    fun searchNotes_fail_confirmNoResults() = runBlocking {

        val query = FORCE_SEARCH_NOTES_EXCEPTION
        var results: ArrayList<Note>? = null
        searchNotes.searchNote(
            query = query,
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1,
            stateEvent = NoteListStateEvent.SearchNotesEvent()
        ).collect(object: FlowCollector<DataState<NoteListViewState>?>{
            override suspend fun emit(value: DataState<NoteListViewState>?) {
                assert(
                    value?.stateMessage?.response?.message
                        ?.contains(CacheErrors.CACHE_ERROR_UNKNOWN) ?: false
                )
                value?.data?.noteList?.let { list ->
                    results = ArrayList(list)
                }
                println("results: ${results}")
            }
        })

        // confirm nothing was retrieved
        assertTrue { results?.run { size == 0 }?: true }

        // confirm there is notes in the cache
        val notesInCache = noteCacheDataSource.searchNotes(
            query = "",
            filterAndOrder = ORDER_BY_ASC_DATE_UPDATED,
            page = 1
        )
        assertTrue { notesInCache.size > 0}
    }






}