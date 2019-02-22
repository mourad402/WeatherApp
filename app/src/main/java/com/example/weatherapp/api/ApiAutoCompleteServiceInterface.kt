package com.example.weatherapp.api

import com.example.weatherapp.models.io.Conditions
import com.example.weatherapp.models.io.Results
import com.example.weatherapp.utils.Constants
import io.reactivex.Observable
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiAutoCompleteServiceInterface {




    //http://autocomplete.wunderground.com/aq?query=issy
    @GET("aq")
    fun searchAutoComplete(@Query("query") query: String): Observable<Results>


    companion object Factory {
        fun create(): ApiAutoCompleteServiceInterface {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL_AC)
                .build()

            return retrofit.create(ApiAutoCompleteServiceInterface::class.java)
        }
    }
}