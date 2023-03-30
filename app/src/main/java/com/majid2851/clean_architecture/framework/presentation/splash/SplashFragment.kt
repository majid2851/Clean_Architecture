package com.majid2851.clean_architecture.framework.presentation.splash

import android.os.Bundle
import android.view.View
import com.example.clean_architecture.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.majid2851.clean_architecture.framework.presentation.common.BaseNoteFragment
import com.majid2851.clean_architecture.util.cLog

class SplashFragment : BaseNoteFragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cLog("Majid2851")
        FirebaseCrashlytics.getInstance().log("Majid28512")
    }


    override fun inject() {

    }

}




























