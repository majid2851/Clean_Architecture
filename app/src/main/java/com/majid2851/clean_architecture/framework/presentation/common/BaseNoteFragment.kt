package com.majid2851.clean_architecture.framework.presentation.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.majid2851.clean_architecture.di.AppComponent
import com.majid2851.clean_architecture.framework.presentation.BaseApplication
import com.majid2851.clean_architecture.framework.presentation.MainActivity
import com.majid2851.clean_architecture.framework.presentation.UIController
import com.majid2851.clean_architecture.util.TodoCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseNoteFragment
constructor(
    private @LayoutRes val layoutRes: Int
): Fragment() {

    open lateinit var uiController: UIController


    fun displayToolbarTitle(textView: TextView, title: String?, useAnimation: Boolean) {
        if(title != null){
            showToolbarTitle(textView, title, useAnimation)
        }
        else{
            hideToolbarTitle(textView, useAnimation)
        }
    }

    private fun hideToolbarTitle(textView: TextView, animation: Boolean){
        if(animation){
            textView.fadeOut(
                object: TodoCallback {
                    override fun execute() {
                        textView.text = ""
                    }
                }
            )
        }
        else{
            textView.text = ""
            textView.gone()
        }
    }

    private fun showToolbarTitle(
        textView: TextView,
        title: String,
        animation: Boolean
    ){
        textView.text = title
        if(animation){
            textView.fadeIn()
        }
        else{
            textView.visible()
        }
    }

    abstract fun inject()

    fun getAppComponent(): AppComponent {
        return activity?.run {
            (application as BaseApplication).appComponent
        }?: throw Exception("AppComponent is null.")
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
        setUIController(null) // null in production
    }

    fun setUIController(mockController: UIController?){

        // TEST: Set interface from mock
        if(mockController != null){
            this.uiController = mockController
        }
        else{ // PRODUCTION: if no mock, get from context
            activity?.let {
                if(it is MainActivity){
                    try{
                        uiController = context as UIController
                    }catch (e: ClassCastException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

}












