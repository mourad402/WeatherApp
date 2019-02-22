package com.example.weatherapp.ui.main

import com.example.weatherapp.models.Adresse
import com.example.weatherapp.models.io.Results
import com.example.weatherapp.ui.base.BaseContract

class MainContract {

    interface View: BaseContract.View {
        fun showWeather(adresse: Adresse)
        fun showSearchFragment()
        fun getLastLocation()
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun updateAutoCompleteAdapter(results:Results)
    }

    interface Presenter: BaseContract.Presenter<MainContract.View> { //implements

        fun onFabClick()
        fun loadWeather(location: String)
        fun refreshWeather()
        fun searchAuoComplete(query: String)
    }

}