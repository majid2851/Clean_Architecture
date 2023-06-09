package com.majid2851.clean_architecture.business.di


import com.majid2851.clean_architecture.business.data.NoteDataFactory
import com.majid2851.clean_architecture.business.data.cache.FakeNoteCacheDataSourceImpl
import com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
import com.majid2851.clean_architecture.business.data.network.FakeNoteNetworkDataSourceImpl
import com.majid2851.clean_architecture.business.data.network.abstraction.NoteNetworkDataSource
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.util.isUnitTest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class DependencyContainer
{

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
    val dateUtil = DateUtil(dateFormat)
    lateinit var noteNetworkDataSource: NoteNetworkDataSource
    lateinit var noteCacheDataSource: com.majid2851.clean_architecture.business.data.cache.abstraction.NoteCacheDataSource
    lateinit var noteFactory: NoteFactory
    lateinit var noteDataFactory: NoteDataFactory
    private var notesData:HashMap<String,Note> =HashMap()

    init {
        isUnitTest = true // for Logger.kt
    }

    fun build()
    {
        this.javaClass.classLoader?.let {classLoader ->
            noteDataFactory= NoteDataFactory(classLoader)
            //fake dataSet
            notesData=noteDataFactory.produceHashMapOfNotes(
                noteDataFactory.produceListOfNotes()
            )

        }
        noteFactory = NoteFactory(dateUtil)
        noteNetworkDataSource = FakeNoteNetworkDataSourceImpl(
            notesData = notesData,
            deletedNotesData = HashMap()
        )
        noteCacheDataSource = FakeNoteCacheDataSourceImpl(
            notesData = notesData,
            dateUtil = dateUtil
        )
    }

}