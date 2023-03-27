package com.example.clean_architecture.framework.presentation.notedetail.state

import android.os.Parcelable
import com.example.clean_architecture.business.domain.model.Note
import com.mag2851.clean_architecture.business.domain.state.ViewState
import kotlinx.parcelize.Parcelize


@Parcelize
data class NoteDetailViewState(

    var note: Note? = null,

    var isUpdatePending: Boolean? = null

) : Parcelable, ViewState