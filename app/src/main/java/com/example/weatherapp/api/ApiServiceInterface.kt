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

interface ApiServiceInterface {


    //http://api.wunderground.com/api/b896f62d3c17257f/conditions/q/48.8353687,2.2643688.json
    @GET("{keyApi}/conditions/q/{coordinates}.json")
    fun getConditionsByCoordinates(@Path("keyApi") keyApi: String,
                                   @Path("coordinates") coordinates: String): Observable<Conditions>




    companion object Factory {
        fun create(): ApiServiceInterface {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()

            return retrofit.create(ApiServiceInterface::class.java)
        }
    }
}