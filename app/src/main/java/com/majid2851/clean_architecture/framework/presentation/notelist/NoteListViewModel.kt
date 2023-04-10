package com.majid2851.clean_architecture.framework.presentation.notelist

import DataState
import Response
import StateMessage
import android.content.SharedPreferences
import android.os.Parcelable
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.domain.model.NoteFactory
import com.majid2851.clean_architecture.business.domain.state.StateEvent
import com.majid2851.clean_architecture.business.interactors.notelist.NoteListInteractors
import com.majid2851.clean_architecture.framework.datasource.cache.database.NOTE_FILTER_DATE_CREATED
import com.majid2851.clean_architecture.framework.datasource.cache.database.NOTE_ORDER_DESC
import com.majid2851.clean_architecture.framework.datasource.prefrences.PrefrencesKeys.Companion.NOTE_FILTER
import com.majid2851.clean_architecture.framework.datasource.prefrences.PrefrencesKeys.Companion.NOTE_ORDER
import com.majid2851.clean_architecture.framework.presentation.common.BaseViewModel
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListStateEvent
import com.majid2851.clean_architecture.framework.presentation.notelist.state.NoteListViewState
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow


const val DELETE_PENDING_ERROR = "There is already a pending delete operation."
const val NOTE_PENDING_DELETE_BUNDLE_KEY = "pending_delete"

@ExperimentalCoroutinesApi
@FlowPreview
class NoteListViewModel
constructor(
    private val noteListInteractors: NoteListInteractors,
    private val noteFactory: NoteFactory,
    private val editor: SharedPreferences.Editor,
    private val sharedPreferences: SharedPreferences
): BaseViewModel<NoteListViewState>(){

    init {
        setNoteFilter(
            sharedPreferences.getString(
                NOTE_FILTER,
                NOTE_FILTER_DATE_CREATED
            )
        )
        setNoteOrder(
            sharedPreferences.getString(
                NOTE_ORDER,
                NOTE_ORDER_DESC
            )
        )
    }

    override fun handleNewData(data: NoteListViewState) {

        data.let { viewState ->
            viewState.noteList?.let { noteList ->
                setNoteListData(noteList)
            }

            viewState.numNotesInCache?.let { numNotes ->
                setNumNotesInCache(numNotes)
            }

            viewState.newNote?.let { note ->
                setNote(note)
            }

            viewState.notePendingDelete?.let { restoredNote ->
                restoredNote.note?.let { note ->
                    setRestoredNoteId(note)
                }
                setNotePendingDelete(null)
            }
        }

    }

    override fun setStateEvent(stateEvent: StateEvent) {

        val job: Flow<DataState<NoteListViewState>?> = when(stateEvent){

            is NoteListStateEvent.InsertNewNoteEvent -> {
                noteListInteractors.insertNewNote.insertNewNote(
                    title = stateEvent.title,
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.DeleteNoteEvent -> {
                noteListInteractors.deletedNote.deleteNote(
                    note = stateEvent.note,
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.DeleteMultipleNotesEvent -> {
                noteListInteractors.deleteMultipleNotes.deleteNotes(
                    notes = stateEvent.notes,
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.RestoreDeletedNoteEvent -> {
                noteListInteractors.restoreDeletedNote.restoreDeletedNote(
                    note = stateEvent.note,
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.SearchNotesEvent -> {
                if(stateEvent.clearLayoutManagerState){
                    clearLayoutManagerState()
                }
                noteListInteractors.searchNote.searchNote(
                    query = getSearchQuery(),
                    filterAndOrder = getOrder() + getFilter(),
                    page = getPage(),
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.GetNumNotesInCacheEvent -> {
                noteListInteractors.getNumNotes.getNumNotes(
                    stateEvent = stateEvent
                )
            }

            is NoteListStateEvent.CreateStateMessageEvent -> {
                emitStateMessageEvent(
                    stateMessage = stateEvent.stateMessage,
                    stateEvent = stateEvent
                )
            }

            else -> {
                emitInvalidStateEvent(stateEvent)
            }
        }
        launchJob(stateEvent, job)
    }

    /*
        Getters
     */
    fun getFilter(): String {
        return getCurrentViewStateOrNew().filter
            ?: NOTE_FILTER_DATE_CREATED
    }

    fun getOrder(): String {
        return getCurrentViewStateOrNew().order
            ?: NOTE_ORDER_DESC
    }

    fun getSearchQuery(): String {
        return getCurrentViewStateOrNew().searchQuery
            ?: return ""
    }

    private fun getPage(): Int{
        return getCurrentViewStateOrNew().page
            ?: return 1
    }

    fun getNoteListSize() = getCurrentViewStateOrNew().noteList?.size?: 0

    private fun getNumNotesInCache() = getCurrentViewStateOrNew().numNotesInCache?: 0

    // for debugging
    fun getActiveJobs() = dataChannelManager.getActiveJobs()

    fun getLayoutManagerState(): Parcelable? {
        return getCurrentViewStateOrNew().layoutManagerState
    }

    private fun findListPositionOfNote(note: Note?): Int {
        val viewState = getCurrentViewStateOrNew()
        viewState.noteList?.let { noteList ->
            for((index, item) in noteList.withIndex()){
                if(item.id == note?.id){
                    return index
                }
            }
        }
        return 0
    }

    fun isPaginationExhausted() = getNoteListSize() >= getNumNotesInCache()

    fun isQueryExhausted(): Boolean{
        printLogD("NoteListViewModel",
            "is query exhasuted? ${getCurrentViewStateOrNew().isQueryExhausted?: true}")
        return getCurrentViewStateOrNew().isQueryExhausted?: true
    }

    override fun initNewViewState(): NoteListViewState {
        return NoteListViewState()
    }

    /*
        Setters
     */
    private fun setNoteListData(notesList: ArrayList<Note>){
        val update = getCurrentViewStateOrNew()
        update.noteList = notesList
        setViewState(update)
    }

    fun setQueryExhausted(isExhausted: Boolean){
        val update = getCurrentViewStateOrNew()
        update.isQueryExhausted = isExhausted
        setViewState(update)
    }

    // can be selected from Recyclerview or created new from dialog
    fun setNote(note: Note?){
        val update = getCurrentViewStateOrNew()
        update.newNote = note
        setViewState(update)
    }

    fun setQuery(query: String?){
        val update =  getCurrentViewStateOrNew()
        update.searchQuery = query
        setViewState(update)
    }


    // if a note is deleted and then restored, the id will be incorrect.
    // So need to reset it here.
    private fun setRestoredNoteId(restoredNote: Note){
        val update = getCurrentViewStateOrNew()
        update.noteList?.let { noteList ->
            for((index, note) in noteList.withIndex()){
                if(note.title.equals(restoredNote.title)){
                    noteList.remove(note)
                    noteList.add(index, restoredNote)
                    update.noteList = noteList
                    break
                }
            }
        }
        setViewState(update)
    }

    private fun removePendingNoteFromList(note: Note?){
        val update = getCurrentViewStateOrNew()
        val list = update.noteList
        if(list?.contains(note) == true){
            list.remove(note)
            update.noteList = list
            setViewState(update)
        }
    }

    fun setNotePendingDelete(note: Note?){
        val update = getCurrentViewStateOrNew()
        if(note != null){
            update.notePendingDelete = NoteListViewState.NotePendingDelete(
                note = note,
                listPosition = findListPositionOfNote(note)
            )
        }
        else{
            update.notePendingDelete = null
        }
        setViewState(update)
    }

    private fun setNumNotesInCache(numNotes: Int){
        val update = getCurrentViewStateOrNew()
        update.numNotesInCache = numNotes
        setViewState(update)
    }

    fun createNewNote(
        id: String? = null,
        title: String,
        body: String? = null
    ) = noteFactory.createSingleNote(id, title, body)

    private fun resetPage(){
        val update = getCurrentViewStateOrNew()
        update.page = 1
        setViewState(update)
    }

    fun clearList(){
        printLogD("ListViewModel", "clearList")
        val update = getCurrentViewStateOrNew()
        update.noteList = ArrayList()
        setViewState(update)
    }

    // workaround for tests
    // can't submit an empty string because SearchViews SUCK
    fun clearSearchQuery(){
        setQuery("")
        clearList()
        loadFirstPage()
    }

    private fun incrementPageNumber(){
        val update = getCurrentViewStateOrNew()
        val page = update.copy().page ?: 1
        update.page = page.plus(1)
        setViewState(update)
    }

    fun setLayoutManagerState(layoutManagerState: Parcelable){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = layoutManagerState
        setViewState(update)
    }

    fun clearLayoutManagerState(){
        val update = getCurrentViewStateOrNew()
        update.layoutManagerState = null
        setViewState(update)
    }

    fun setNoteFilter(filter: String?){
        filter?.let{
            val update = getCurrentViewStateOrNew()
            update.filter = filter
            setViewState(update)
        }
    }

    fun setNoteOrder(order: String?){
        val update = getCurrentViewStateOrNew()
        update.order = order
        setViewState(update)
    }

    fun saveFilterOptions(filter: String, order: String){
        editor.putString(NOTE_FILTER, filter)
        editor.apply()

        editor.putString(NOTE_ORDER, order)
        editor.apply()
    }

    /*
        StateEvent Triggers
     */
    fun isDeletePending(): Boolean{
        val pendingNote = getCurrentViewStateOrNew().notePendingDelete
        if(pendingNote != null){
            setStateEvent(
                NoteListStateEvent.CreateStateMessageEvent(
                    stateMessage = StateMessage(
                        response = Response(
                            message = DELETE_PENDING_ERROR,
                            uiComponentType = UIComponentType.Toast(),
                            messageType = MessageType.Info()
                        )
                    )
                )
            )
            return true
        }
        else{
            return false
        }
    }


    fun undoDelete(){
        // replace note in viewstate
        val update = getCurrentViewStateOrNew()
        update.notePendingDelete?.let { note ->
            if(note.listPosition != null && note.note != null){
                update.noteList?.add(
                    note.listPosition as Int,
                    note.note as Note
                )
                setStateEvent(NoteListStateEvent.RestoreDeletedNoteEvent(note.note as Note))
            }
        }
        setViewState(update)
    }

    fun beginPendingDelete(note: Note){
        setNotePendingDelete(note)
        removePendingNoteFromList(note)
        setStateEvent(
            NoteListStateEvent.DeleteNoteEvent(
                note = note
            )
        )
    }

    fun loadFirstPage() {
        setQueryExhausted(false)
        resetPage()
        setStateEvent(NoteListStateEvent.SearchNotesEvent())
        printLogD("NoteListViewModel",
            "loadFirstPage: ${getCurrentViewStateOrNew().searchQuery}")
    }

    fun nextPage(){
        if(!isQueryExhausted()){
            printLogD("NoteListViewModel", "attempting to load next page...")
            clearLayoutManagerState()
            incrementPageNumber()
            setStateEvent(NoteListStateEvent.SearchNotesEvent())
        }
    }

    fun retrieveNumNotesInCache(){
        setStateEvent(NoteListStateEvent.GetNumNotesInCacheEvent())
    }

    fun refreshSearchQuery(){
        setQueryExhausted(false)
        setStateEvent(NoteListStateEvent.SearchNotesEvent(false))
    }

}



