package com.majid2851.clean_architecture.framework.presentation.notedetail

import com.majid2851.clean_architecture.business.domain.state.StateEvent
import com.majid2851.clean_architecture.business.interactors.notedetail.NoteDetailInteractors
import com.majid2851.clean_architecture.framework.presentation.common.BaseViewModel
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.NoteDetailViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class NoteDetailViewModel
@Inject
constructor(
    private val noteDetailInteractors: NoteDetailInteractors
): BaseViewModel<NoteDetailViewState>(){

    override fun handleNewData(data: NoteDetailViewState) {

    }

    override fun setStateEvent(stateEvent: StateEvent) {

    }

    override fun initNewViewState(): NoteDetailViewState {
        return NoteDetailViewState()
    }

}









