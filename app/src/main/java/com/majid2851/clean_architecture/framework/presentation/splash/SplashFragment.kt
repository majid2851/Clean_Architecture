package com.majid2851.clean_architecture.framework.presentation.splash

import DialogInputCaptureCallback
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.clean_architecture.R
import com.google.firebase.auth.FirebaseAuth
import com.majid2851.clean_architecture.framework.datasource.network.implementation.NoteFireStoreServieImpl.Companion.EMAIL
import com.majid2851.clean_architecture.framework.datasource.network.implementation.NoteFireStoreServieImpl.Companion.PASSWORD
import com.majid2851.clean_architecture.framework.presentation.BaseApplication
import com.majid2851.clean_architecture.framework.presentation.common.BaseNoteFragment
import com.majid2851.clean_architecture.framework.presentation.common.invisible
import com.majid2851.clean_architecture.framework.presentation.common.visible
import com.majid2851.clean_architecture.util.printLogD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.time.Duration
import javax.inject.Inject
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
class SplashFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseNoteFragment(R.layout.fragment_splash) {
    lateinit var progressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v=inflater.inflate(R.layout.fragment_splash, container, false)
        progressBar=v.findViewById(R.id.progressBar)
        return v;
    }


    val viewModel: SplashViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkFirebaseAuth()
    }

    private fun checkFirebaseAuth(){
        if(FirebaseAuth.getInstance().currentUser == null){
            displayCapturePassword()
        }
        else{
            subscribeObservers()
        }
    }

    // add password input b/c someone used my firestore and deleted the data
    private fun displayCapturePassword()
    {
//        uiController.displayInputCaptureDialog(
//            getString(R.string.text_enter_password),
//            object: DialogInputCaptureCallback {
//                override fun onTextCaptured(text: String)
//                {
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(EMAIL, PASSWORD)
                        .addOnCompleteListener {
                            if(it.isSuccessful){

                                printLogD("MainActivity",
                                    "Signing in to Firebase: ${it.result}")
                                subscribeObservers()
                                progressBar.invisible()
                                Toast.makeText(requireActivity(),"Login is Successful"
                                    ,Toast.LENGTH_LONG).show()
                            }
                            else{
                                Toast.makeText(requireActivity(),"cannot log in",Toast.LENGTH_LONG).show()
                                printLogD("MainActivity","cannot log in")
                                progressBar.invisible()
                            }
                        }
//                }
//            }
//        )
    }

    private fun subscribeObservers(){

        viewModel.hasSyncBeenExecuted().observe(viewLifecycleOwner, Observer { hasSyncBeenExecuted ->

            if(hasSyncBeenExecuted){
                Toast.makeText(requireActivity(),"sync is excuted"
                    ,Toast.LENGTH_LONG).show()
                navNoteListFragment()
            }
        })
    }

    private fun navNoteListFragment(){
        findNavController().navigate(R.id.action_splashFragment_to_noteListFragment)
    }

    override fun inject() {
        getAppComponent().inject(this)
    }
}
















