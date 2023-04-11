package com.majid2851.clean_architecture.framework.presentation.notedetail.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Both can not be in 'EditState' at the same time.
class NoteInteractionManager
{

    private val _noteTitleState: MutableLiveData<NoteInteractionState>
            = MutableLiveData(NoteInteractionState.DefaultState())

    private val _noteBodyState: MutableLiveData<NoteInteractionState>
            = MutableLiveData(NoteInteractionState.DefaultState())

    private val _collapsingToolbarState: MutableLiveData<CollapsingToolbarState>
            = MutableLiveData(CollapsingToolbarState.Expanded())

    val noteTitleState: LiveData<NoteInteractionState>
        get() = _noteTitleState

    val noteBodyState: LiveData<NoteInteractionState>
        get() = _noteBodyState

    val collapsingToolbarState: LiveData<CollapsingToolbarState>
        get() = _collapsingToolbarState

    fun setCollapsingToolbarState(state: CollapsingToolbarState){
        if(!state.toString().equals(_collapsingToolbarState.value.toString())){
            _collapsingToolbarState.value = state
        }
    }

    fun setNewNoteTitleState(state: NoteInteractionState){
        if(!noteTitleState.toString().equals(state.toString())){
            _noteTitleState.value = state
            when(state){

                is NoteInteractionState.EditState -> {
                    _noteBodyState.value = NoteInteractionState.DefaultState()
                }

                else -> {}
            }
        }
    }

    fun setNewNoteBodyState(state: NoteInteractionState){
        if(!noteBodyState.toString().equals(state.toString())){
            _noteBodyState.value = state
            when(state){

                is NoteInteractionState.EditState -> {
                    _noteTitleState.value = NoteInteractionState.DefaultState()
                }

                else -> {

                }
            }
        }
    }

    fun isEditingTitle() = noteTitleState.value.toString().equals(NoteInteractionState.EditState().toString())

    fun isEditingBody() = noteBodyState.value.toString().equals(NoteInteractionState.EditState().toString())

    fun exitEditState(){
        _noteTitleState.value = NoteInteractionState.DefaultState()
        _noteBodyState.value = NoteInteractionState.DefaultState()
    }

    // return true if either title or body are in EditState
    fun checkEditState() = noteTitleState.value.toString().equals(NoteInteractionState.EditState().toString())
            || noteBodyState.value.toString().equals(NoteInteractionState.EditState().toString())



}
