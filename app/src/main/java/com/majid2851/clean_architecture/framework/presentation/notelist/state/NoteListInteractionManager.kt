package com.majid2851.clean_architecture.framework.presentation.notelist.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.majid2851.clean_architecture.business.domain.model.Note

class NoteListInteractionManager {

    private val _selectedNotes: MutableLiveData<ArrayList<Note>> = MutableLiveData()

    private val _toolbarState: MutableLiveData<NoteListToolbarState>
            = MutableLiveData(NoteListToolbarState.SearchViewState())

    val selectedNotes: LiveData<ArrayList<Note>>
        get() = _selectedNotes

    val toolbarState: LiveData<NoteListToolbarState>
        get() = _toolbarState

    fun setToolbarState(state: NoteListToolbarState){
        _toolbarState.value = state
    }

    fun getSelectedNotes():ArrayList<Note> = _selectedNotes.value?: ArrayList()

    fun isMultiSelectionStateActive(): Boolean{
        return _toolbarState.value.toString() == NoteListToolbarState.MultiSelectionState().toString()
    }

    fun addOrRemoveNoteFromSelectedList(note: Note){
        var list = _selectedNotes.value
        if(list == null){
            list = ArrayList()
        }
        if (list.contains(note)){
            list.remove(note)
        }
        else{
            list.add(note)
        }
        _selectedNotes.value = list
    }

    fun isNoteSelected(note: Note): Boolean{
        return _selectedNotes.value?.contains(note)?: false
    }

    fun clearSelectedNotes(){
        _selectedNotes.value = null
    }

}
