package com.majid2851.clean_architecture.business.data

import com.majid2851.clean_architecture.business.data.util.fakeData
import com.majid2851.clean_architecture.business.domain.model.Note


class NoteDataFactory(
    private val testClassLoader:ClassLoader
)
{
    fun produceListOfNotes():List<Note>
    {
        return fakeData()
    }
    fun produceHashMapOfNotes(noteList:List<Note>):HashMap<String,Note>
    {
        val map=HashMap<String,Note>()
        for (note in noteList)
        {
            map.put(note.id,note)
        }
        return map
    }
    fun produceEmptyListOfNotes():List<Note>
    {
        return ArrayList()
    }

    fun getNotesFromFile(fileName:String):String
    {

        return testClassLoader.getResource(fileName)
            .readText()
    }



}