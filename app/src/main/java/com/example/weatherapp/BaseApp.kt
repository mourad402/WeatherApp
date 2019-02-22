package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.di.components.ApplicationComponent
import com.example.weatherapp.di.components.DaggerApplicationComponent
import com.example.weatherapp.di.modules.ApplicationModule

class BaseApp: Application(){

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        instance = this
        setup()

        if (BuildConfig.DEBUG) {
            // Maybe TimberPlant etc.
        }
    }

    fun setup() {
        component = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this)).build()
        component.inject(this)
    }

    fun getApplicationComponent(): ApplicationComponent {
        return component
    }

    companion object {
        lateinit var instance: BaseApp private set
    }
}