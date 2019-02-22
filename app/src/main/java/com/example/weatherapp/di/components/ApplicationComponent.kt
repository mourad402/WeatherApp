package com.example.weatherapp.di.components

import com.example.weatherapp.BaseApp
import com.example.weatherapp.di.modules.ApplicationModule
import dagger.Component


@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: BaseApp)

}