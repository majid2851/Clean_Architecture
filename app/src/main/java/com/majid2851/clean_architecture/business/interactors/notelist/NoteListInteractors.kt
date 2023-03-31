package com.majid2851.clean_architecture.business.interactors.notelist

import com.majid2851.clean_architecture.business.interactors.common.DeleteNote
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState

class NoteListInteractors(
    val insertNewNote: InsertNewNote,
    val deletedNote:DeleteNote<NoteListViewState>,
    val searchNote: SearchNote,
    val getNumNotes: GetNumNotes,
    val restoreDeletedNote: RestoreDeletedNote,
    val deleteMultipleNotes: DeleteMultipleNotes
)
{




}