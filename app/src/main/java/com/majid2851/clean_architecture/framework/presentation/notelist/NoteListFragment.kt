

package com.majid2851.clean_architecture.framework.presentation.notelist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_architecture.R
import com.majid2851.clean_architecture.business.domain.util.DateUtil
import com.majid2851.clean_architecture.framework.presentation.common.BaseNoteFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


const val NOTE_LIST_STATE_BUNDLE_KEY = "com.codingwithmitch.cleannotes.notes.framework.presentation.notelist.state"

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class NoteListFragment
    constructor(
        private val viewModelFactory:ViewModelProvider.Factory,
        private val dateUtil:DateUtil
        ): BaseNoteFragment(R.layout.fragment_note_list)
{

    val viewModel:NoteListViewModel by viewModels{
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun inject() {
        TODO("prepare dagger")
    }

}










































