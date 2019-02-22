package com.example.weatherapp.ui.main

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.weatherapp.api.ApiAutoCompleteServiceInterface
import com.example.weatherapp.api.ApiServiceInterface
import com.example.weatherapp.models.Adresse
import com.example.weatherapp.models.io.Conditions
import com.example.weatherapp.models.io.Results
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenter: MainContract.Presenter {


    private val TAG = "weatherMain"
    private val subscriptions = CompositeDisposable()
    private lateinit var view: MainContract.View
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private val apiAc: ApiAutoCompleteServiceInterface = ApiAutoCompleteServiceInterface.create()

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: MainContract.View) {
        this.view = view
        view.showProgress(true)
        view.getLastLocation() // as default
    }

    override fun searchAuoComplete(query: String) {

        Log.d(TAG,"searchAuoComplete $query")

        val subscribe = apiAc.searchAutoComplete(query)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ results: Results ->

                Log.d(TAG,"okkkkkkk "+ results.RESULTS.size)

                view.updateAutoCompleteAdapter(results)
            }, { error ->
                Log.d(TAG,"NOP !!!")
                view.showProgress(false)
                view.showErrorMessage(error.localizedMessage)
            })

        subscriptions.add(subscribe)
    }
    override fun loadWeather(location: String) {

        Log.d(TAG,"loadWeather $location")

        val subscribe = api.getConditionsByCoordinates("b896f62d3c17257f", location )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ conditions: Conditions ->
                Log.d(TAG,"okkkkkkk loadWeather")

                view.showProgress(false)
                view.showWeather( Adresse(conditions.current_observation.display_location.city,
                    conditions.current_observation.temp_f,
                    conditions.current_observation.temp_c,
                    conditions.current_observation.icon_url,
                    conditions.current_observation.observation_time_rfc822,
                    "adresse val")
                )
            }, { error ->
                Log.d(TAG,"NOP !!!")
                view.showProgress(false)
                view.showErrorMessage(error.localizedMessage)
            })

        subscriptions.add(subscribe)

    }

    override fun refreshWeather() {
        view.showProgress(true)
        view.getLastLocation()
    }



    override fun onFabClick() {
        view.showProgress(true)
        view.showSearchFragment()
    }

}