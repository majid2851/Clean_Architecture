package com.majid2851.clean_architecture.di

import com.majid2851.clean_architecture.cleannotes.di.ProductionModule
import com.majid2851.clean_architecture.di.AppModule
import com.majid2851.clean_architecture.framework.presentation.BaseApplication
import com.majid2851.clean_architecture.framework.presentation.MainActivity
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Singleton

@FlowPreview
@ExperimentalCoroutinesApi
@Singleton
@Component(
    modules = [
        AppModule::class,
        ProductionModule::class
    ]
)
interface AppComponent{

    @Component.Factory
    interface Factory{

        fun create(@BindsInstance app: BaseApplication): AppComponent
    }

    fun inject(mainActivity: MainActivity)
}