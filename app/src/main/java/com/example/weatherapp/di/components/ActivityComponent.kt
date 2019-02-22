package com.example.weatherapp.di.components

import com.example.weatherapp.ui.main.MainActivity
import com.example.weatherapp.di.modules.ActivityModule
import dagger.Component

@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)
}

