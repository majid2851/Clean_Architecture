package com.majid2851.clean_architecture.framework.datasource.cache

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.di.TestAppComponent
import com.majid2851.clean_architecture.framework.BaseTest
import com.majid2851.clean_architecture.framework.datasource.cache.abstraction.NoteDaoService
import com.majid2851.clean_architecture.framework.datasource.cache.database.NoteDao
import com.majid2851.clean_architecture.framework.datasource.cache.implementation.NoteDaoServiceImpl
import com.majid2851.clean_architecture.framework.datasource.cache.mapper.CacheMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@RunWith(AndroidJUnit4ClassRunner::class)
class NoteDaoServiceTest:BaseTest()
{
    private val noteDaoService:NoteDaoService

    @Inject
    lateinit var noteDao:NoteDao

    @Inject
    lateinit var noteFactory:NoteFactory

    @Inject
    lateinit var dateUtil:DateUtil

    @Inject
    lateinit var cacheMapper:CacheMapper

    init {
        injectTest()
        insertTestData()
        noteDaoService=NoteDaoServiceImpl(
            noteDao=noteDao,
            noteMapper = cacheMapper,
            dateUtil=dateUtil
        )
    }
    fun insertTestData() = runBlocking{
        val entityList = cacheMapper.noteListToEntityList(
            noteFactory.getFakeData()
        )
        noteDao.insertNotes(entityList)
    }
    override fun injectTest() {
        (application.appComponent as TestAppComponent).inject(this)
    }


// runBlockingTest doesn't work:
// https://github.com/Kotlin/kotlinx.coroutines/issues/1204

//        LEGEND:
//        1. CBS = "Confirm by searching"
//        Test cases:
//        1. confirm database note empty to start (should be test data inserted from CacheTest.kt)
    @Test
    fun a_searchNotes_confirmDbNotEmpty()
    {
        runBlocking()
        {
            val numNotes=noteDaoService.getNumNotes()
            assertTrue(numNotes > 0)
        }


    }

//        2. insert a new note, CBS
//        3. insert a list of notes, CBS
//        4. insert 1000 new notes, confirm filtered search query works correctly
//        5. insert 1000 new notes, confirm db size increased
//        6. delete new note, confirm deleted
//        7. delete list of notes, CBS
//        8. update a note, confirm updated
//        9. search notes, order by date (ASC), confirm order
//        10. search notes, order by date (DESC), confirm order
//        11. search notes, order by title (ASC), confirm order
//        12. search notes, order by title (DESC), confirm order


















}