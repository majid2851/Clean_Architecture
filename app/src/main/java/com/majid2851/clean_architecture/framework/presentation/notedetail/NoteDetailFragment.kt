@file:OptIn(FlowPreview::class)

package com.majid2851.clean_architecture.framework.presentation.notedetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.clean_architecture.R
import com.majid2851.clean_architecture.framework.presentation.common.BaseNoteFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

const val NOTE_DETAIL_STATE_BUNDLE_KEY = "com.codingwithmitch.cleannotes.notes.framework.presentation.notedetail.state"

@OptIn(ExperimentalCoroutinesApi::class)
class NoteDetailFragment
    @Inject constructor(
        private val viewModelFactory:ViewModelProvider.Factory
    ): BaseNoteFragment(R.layout.fragment_note_detail) {



    val viewModel:NoteDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun inject() {
        TODO("prepare dagger")
    }


}














