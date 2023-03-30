package com.majid2851.clean_architecture.business.interactors.notedetail

import com.majid2851.clean_architecture.business.interactors.common.DeleteNote
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.NoteDetailViewState

class NoteDetailInteractors(
    val deleteNote:DeleteNote<NoteDetailViewState>,
    val updateNote:UpdateNote
)
{









}