@file:OptIn(FlowPreview::class)

package com.majid2851.clean_architecture.framework.presentation.notedetail

import AreYouSureCallback
import Response
import StateMessage
import StateMessageCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_architecture.R
import com.google.android.material.appbar.AppBarLayout
import com.majid2851.clean_architecture.business.domain.model.Note
import com.majid2851.clean_architecture.business.interactors.common.DeleteNote.Companion.DELETE_NOTE_SUCCESS
import com.majid2851.clean_architecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_FAILED_PK
import com.majid2851.clean_architecture.business.interactors.notedetail.UpdateNote.Companion.UPDATE_NOTE_SUCCESS
import com.majid2851.clean_architecture.framework.presentation.common.BaseNoteFragment
import com.majid2851.clean_architecture.framework.presentation.common.COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD
import com.majid2851.clean_architecture.framework.presentation.common.disableContentInteraction
import com.majid2851.clean_architecture.framework.presentation.common.enableContentInteraction
import com.majid2851.clean_architecture.framework.presentation.common.fadeIn
import com.majid2851.clean_architecture.framework.presentation.common.fadeOut
import com.majid2851.clean_architecture.framework.presentation.common.hideKeyboard
import com.majid2851.clean_architecture.framework.presentation.common.showKeyboard
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.CollapsingToolbarState
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.NoteDetailStateEvent
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.NoteDetailViewState
import com.majid2851.clean_architecture.framework.presentation.notedetail.state.NoteInteractionState
import com.majid2851.clean_architecture.framework.presentation.notelist.NOTE_PENDING_DELETE_BUNDLE_KEY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


const val NOTE_DETAIL_STATE_BUNDLE_KEY = "com.codingwithmitch.cleannotes.notes.framework.presentation.notedetail.state"


@FlowPreview
@ExperimentalCoroutinesApi
class NoteDetailFragment
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseNoteFragment(R.layout.fragment_note_detail) {
    lateinit var container_due_date:LinearLayout
    lateinit var note_title:EditText
    lateinit var note_body:EditText
    lateinit var app_bar:AppBarLayout
    lateinit var toolbar_primary_icon:ImageView
    lateinit var toolbar_secondary_icon:ImageView
    lateinit var tool_bar_title:TextView


    val viewModel: NoteDetailViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v=inflater.inflate(R.layout.fragment_note_detail, container, false)
        container_due_date=v.findViewById(R.id.container_due_date)
        note_title=v.findViewById(R.id.note_title)
        note_body=v.findViewById(R.id.note_body)
        app_bar=v.findViewById(R.id.app_bar)
        toolbar_primary_icon=v.findViewById(R.id.toolbar_primary_icon)
        toolbar_secondary_icon=v.findViewById(R.id.toolbar_secondary_icon)
        tool_bar_title=v.findViewById(R.id.tool_bar_title)


        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupOnBackPressDispatcher()
        subscribeObservers()

        container_due_date.setOnClickListener {
            // TODO("handle click of due date")
        }

        note_title.setOnClickListener {
            onClick_noteTitle()
        }

        note_body.setOnClickListener {
            onClick_noteBody()
        }

        setupMarkdown()
        getSelectedNoteFromPreviousFragment()
        restoreInstanceState()
    }

    private fun onErrorRetrievingNoteFromPreviousFragment(){
        viewModel.setStateEvent(
            NoteDetailStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = NOTE_DETAIL_ERROR_RETRIEVEING_SELECTED_NOTE,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
        )
    }

    private fun setupMarkdown()
    {
        activity?.run {
//            val markdownProcessor = MarkdownProcessor(this)
//            markdownProcessor.factory(EditFactory.create())
//            markdownProcessor.live(note_body)
        }
    }

    private fun onClick_noteTitle(){
        if(!viewModel.isEditingTitle()){
            updateBodyInViewModel()
            updateNote()
            viewModel.setNoteInteractionTitleState(NoteInteractionState.EditState())
        }
    }

    private fun onClick_noteBody(){
        if(!viewModel.isEditingBody()){
            updateTitleInViewModel()
            updateNote()
            viewModel.setNoteInteractionBodyState(NoteInteractionState.EditState())
        }
    }

    private fun onBackPressed() {
        view?.hideKeyboard()
        if(viewModel.checkEditState()){
            updateBodyInViewModel()
            updateTitleInViewModel()
            updateNote()
            viewModel.exitEditState()
            displayDefaultToolbar()
        }
        else{
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
        updateTitleInViewModel()
        updateBodyInViewModel()
        updateNote()
    }

    private fun subscribeObservers(){

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            if(viewState != null){

                viewState.note?.let { note ->
                    setNoteTitle(note.title)
                    setNoteBody(note.body)
                }
            }
        })

        viewModel.shouldDisplayProgressBar.observe(viewLifecycleOwner, Observer {
            uiController.displayProgressBar(it)
        })

        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->

            stateMessage?.response?.let { response ->

                when(response.message){

                    UPDATE_NOTE_SUCCESS -> {
                        viewModel.setIsUpdatePending(false)
                        viewModel.clearStateMessage()
                    }

                    DELETE_NOTE_SUCCESS -> {
                        viewModel.clearStateMessage()
                        onDeleteSuccess()
                    }

                    else -> {
                        uiController.onResponseReceived(
                            response = stateMessage.response,
                            stateMessageCallback = object: StateMessageCallback {
                                override fun removeMessageFromStack() {
                                    viewModel.clearStateMessage()
                                }
                            }
                        )
                        when(response.message){

                            UPDATE_NOTE_FAILED_PK -> {
                                findNavController().popBackStack()
                            }

                            NOTE_DETAIL_ERROR_RETRIEVEING_SELECTED_NOTE -> {
                                findNavController().popBackStack()
                            }

                            else -> {
                                // do nothing
                            }
                        }
                    }
                }
            }

        })

        viewModel.collapsingToolbarState.observe(viewLifecycleOwner, Observer { state ->

            when(state){

                is CollapsingToolbarState.Expanded -> {
                    transitionToExpandedMode()
                }

                is CollapsingToolbarState.Collapsed -> {
                    transitionToCollapsedMode()
                }
            }
        })

        viewModel.noteTitleInteractionState.observe(viewLifecycleOwner, Observer { state ->

            when(state){

                is NoteInteractionState.EditState -> {
                    note_title.enableContentInteraction()
                    view?.showKeyboard()
                    displayEditStateToolbar()
                    viewModel.setIsUpdatePending(true)
                }

                is NoteInteractionState.DefaultState -> {
                    note_title.disableContentInteraction()
                }
            }
        })

        viewModel.noteBodyInteractionState.observe(viewLifecycleOwner, Observer { state ->

            when(state){

                is NoteInteractionState.EditState -> {
                    note_body.enableContentInteraction()
                    view?.showKeyboard()
                    displayEditStateToolbar()
                    viewModel.setIsUpdatePending(true)
                }

                is NoteInteractionState.DefaultState -> {
                    note_body.disableContentInteraction()
                }
            }
        })
    }

    private fun displayDefaultToolbar(){
        activity?.let { a ->
            toolbar_primary_icon.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_arrow_back_grey_24dp,
                    a.application.theme
                )
            )
            toolbar_secondary_icon.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_delete,
                    a.application.theme
                )
            )
        }
    }

    private fun displayEditStateToolbar(){
        activity?.let { a ->
            toolbar_primary_icon.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_close_grey_24dp,
                    a.application.theme
                )
            )
            toolbar_secondary_icon.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_done_grey_24dp,
                    a.application.theme
                )
            )
        }
    }

    private fun setNoteTitle(title: String) {
        note_title.setText(title)
    }

    private fun getNoteTitle(): String{
        return note_title.text.toString()
    }

    private fun getNoteBody(): String{
        return note_body.text.toString()
    }

    private fun setNoteBody(body: String?){
        note_body.setText(body)
    }

    private fun getSelectedNoteFromPreviousFragment(){
        arguments?.let { args ->
            (args.getParcelable(NOTE_DETAIL_SELECTED_NOTE_BUNDLE_KEY) as Note?)?.let { selectedNote ->
                viewModel.setNote(selectedNote)
            }?: onErrorRetrievingNoteFromPreviousFragment()
        }

    }

    private fun restoreInstanceState(){
        arguments?.let { args ->
            (args.getParcelable(NOTE_DETAIL_STATE_BUNDLE_KEY) as NoteDetailViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)

                // One-time check after rotation
                if(viewModel.isToolbarCollapsed()){
                    app_bar.setExpanded(false)
                    transitionToCollapsedMode()
                }
                else{
                    app_bar.setExpanded(true)
                    transitionToExpandedMode()
                }
            }
        }
    }

    private fun updateTitleInViewModel(){
        if(viewModel.isEditingTitle()){
            viewModel.updateNoteTitle(getNoteTitle())
        }
    }

    private fun updateBodyInViewModel(){
        if(viewModel.isEditingBody()){
            viewModel.updateNoteBody(getNoteBody())
        }
    }

    private fun setupUI(){
        note_title.disableContentInteraction()
        note_body.disableContentInteraction()
        displayDefaultToolbar()
        transitionToExpandedMode()

        app_bar.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener{ _, offset ->

                if(offset < COLLAPSING_TOOLBAR_VISIBILITY_THRESHOLD){
                    updateTitleInViewModel()
                    if(viewModel.isEditingTitle()){
                        viewModel.exitEditState()
                        displayDefaultToolbar()
                        updateNote()
                    }
                    viewModel.setCollapsingToolbarState(CollapsingToolbarState.Collapsed())
                }
                else{
                    viewModel.setCollapsingToolbarState(CollapsingToolbarState.Expanded())
                }
            })

        toolbar_primary_icon.setOnClickListener {
            if(viewModel.checkEditState()){
                view?.hideKeyboard()
                viewModel.triggerNoteObservers()
                viewModel.exitEditState()
                displayDefaultToolbar()
            }
            else{
                onBackPressed()
            }
        }

        toolbar_secondary_icon.setOnClickListener {
            if(viewModel.checkEditState()){
                view?.hideKeyboard()
                updateTitleInViewModel()
                updateBodyInViewModel()
                updateNote()
                viewModel.exitEditState()
                displayDefaultToolbar()
            }
            else{
                deleteNote()
            }
        }
    }

    private fun deleteNote(){
        viewModel.setStateEvent(
            NoteDetailStateEvent.CreateStateMessageEvent(
                stateMessage = StateMessage(
                    response = Response(
                        message = DELETE_ARE_YOU_SURE,
                        uiComponentType = UIComponentType.AreYouSureDialog(
                            object : AreYouSureCallback {
                                override fun proceed() {
                                    viewModel.getNote()?.let { note ->
                                        initiateDeleteTransaction(note)
                                    }
                                }

                                override fun cancel() {
                                    // do nothing
                                }
                            }
                        ),
                        messageType = MessageType.Info()
                    )
                )
            )
        )
    }

    private fun initiateDeleteTransaction(note: Note){
        viewModel.beginPendingDelete(note)
    }

    private fun onDeleteSuccess(){
        val bundle = bundleOf(NOTE_PENDING_DELETE_BUNDLE_KEY to viewModel.getNote())
        viewModel.setNote(null) // clear note from ViewState
        viewModel.setIsUpdatePending(false) // prevent update onPause
        findNavController().navigate(
            R.id.action_note_detail_fragment_to_noteListFragment,
            bundle
        )
    }

    private fun setupOnBackPressDispatcher() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun updateNote() {
        if(viewModel.getIsUpdatePending()){
            viewModel.setStateEvent(
                NoteDetailStateEvent.UpdateNoteEvent()
            )
        }
    }

    private fun transitionToCollapsedMode() {
        note_title.fadeOut()
        displayToolbarTitle(tool_bar_title, getNoteTitle(), true)
    }

    private fun transitionToExpandedMode() {
        note_title.fadeIn()
        displayToolbarTitle(tool_bar_title, null, true)
    }

    override fun inject() {
        getAppComponent().inject(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.getCurrentViewStateOrNew()
        outState.putParcelable(NOTE_DETAIL_STATE_BUNDLE_KEY, viewState)
        super.onSaveInstanceState(outState)
    }


}





