package com.majid2851.clean_architecture.framework.datasource.data

import android.app.Application
import com.majid2851.clean_architecture.business.data.util.fakeData
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteDataFactory @Inject constructor(
    private val application:Application,
    private val noteFactory:NoteFactory
    )
{
    fun produceListOfNotes():List<Note>
    {
        return fakeData()
    }
//    fun produceHashMapOfNotes(noteList:List<Note>):HashMap<String,Note>
//    {
//        val map=HashMap<String,Note>()
//        for (note in noteList)
//        {
//            map.put(note.id,note)
//        }
//        return map
//    }
    fun produceEmptyListOfNotes():List<Note>
    {
        return ArrayList()
    }

    fun createSingleNote(
        id:String?=null,
        title:String,
        body:String?=null
    ){
        noteFactory.createSingleNote(id,title,body)
    }
    fun createNoteList(numNotes:Int){
        noteFactory.createNoteList(numNotes)
    }








}