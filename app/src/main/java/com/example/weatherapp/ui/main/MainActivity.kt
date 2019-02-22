package com.example.weatherapp.ui.main

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.di.components.DaggerActivityComponent
import com.example.weatherapp.di.modules.ActivityModule
import com.example.weatherapp.models.Adresse
import com.example.weatherapp.models.io.Result
import com.example.weatherapp.models.io.Results
import com.google.android.gms.location.FusedLocationProviderClient


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    private val TAG = "weatherMain"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private lateinit var latitude: String
    private lateinit var longitude: String

    private lateinit var searchView: SearchView
    private lateinit var searchAutoComplete: SearchView.SearchAutoComplete
    private lateinit var lastAutoCompleteResults: Results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        injectDependency()

        presenter.attach(this)

        fab.setOnClickListener { view ->
            presenter.onFabClick()
        }
    }




    override fun showWeather(adresse: Adresse) {

        cityNameTextView.text = adresse.nomVille
        temperatureTextView.text = adresse.tempC.toString() // test if setting C or F
        observationTimeTextView.text = adresse.heureRecup
        Glide.with(this)
            .asGif()
            .load(adresse.imageUrl)
            .into(iconImageView)
        Log.d(TAG,"showCurrentLocationWeather")
    }

    override fun showSearchFragment() {
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }


    override fun updateAutoCompleteAdapter(results: Results){

        Log.d(TAG,"updateAutoCompleteAdapter results "+ results.RESULTS.size)
        var dataArray = arrayOfNulls<String>(results.RESULTS.size)
        lastAutoCompleteResults = results

        for (i in results.RESULTS.indices) {
            Log.d(TAG,"RESULTS "+ results.RESULTS[i].name+" "+results.RESULTS[i].lon+"  "+results.RESULTS[i].lat)
            dataArray[i] = results.RESULTS[i].name
        }
        Log.d(TAG,"updateAutoCompleteAdapter dataArray "+ results.RESULTS.size)
        searchAutoComplete.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dataArray))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        searchView = menu?.findItem(R.id.searchMenu)?.actionView as SearchView


        // Get SearchView autocomplete object.
        searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white)


        // Listen to search view item on click event.
        searchAutoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, itemIndex, id ->
                val queryString = adapterView.getItemAtPosition(itemIndex) as String
                Log.d(TAG, "onItemClickListener : $queryString $itemIndex")
                searchAutoComplete.setText(queryString)
                val lon = lastAutoCompleteResults.RESULTS[itemIndex].lon
                val lat = lastAutoCompleteResults.RESULTS[itemIndex].lat
                presenter.loadWeather("$lat,$lon")

            }




        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (query.isNotEmpty()) {
                        Log.d(TAG, "Search keyword is : $query")
                        // when submit select first element of results list
                        searchAutoComplete.setText(lastAutoCompleteResults.RESULTS[0].name)
                        val lon = lastAutoCompleteResults.RESULTS[0].lon
                        val lat = lastAutoCompleteResults.RESULTS[0].lat
                        presenter.loadWeather("$lat,$lon")
                    }

                    if (query.isEmpty())
                         Log.d(TAG,"NOOO TEX")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.length > 1) {
                        Log.d(TAG, "onQueryTextChange : $newText")
                        presenter.searchAuoComplete(newText)
                    }

                    if (newText.isEmpty())
                        Log.d(TAG,"NOOOO onQueryTextChange")
                }
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId) {
            R.id.refreshButton -> {
                presenter.refreshWeather()
                return true
            }
            else -> {
                Toast.makeText(this, "${item.title}", Toast.LENGTH_LONG).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .build()

        activityComponent.inject(this)
    }

    /**
     * Return the current state of the permissions needed.
     */
    @SuppressLint("MissingPermission")
    override fun getLastLocation() {

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->

                if (task.isSuccessful && task.result != null) {
                    latitude = task.result!!.latitude.toString()
                    longitude = task.result!!.longitude.toString()

                    Log.d(TAG,"location $latitude $longitude")

                    presenter.loadWeather("$latitude,$longitude")
                } else {
                    Snackbar.make(parentLayout, R.string.no_location_detected, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()


    }

    private fun checkPermissions() =  ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(parentLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction(android.R.string.ok) {
                    // Request permission
                    startLocationPermissionRequest()
                }.show()


        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }


    override fun showProgress(show: Boolean) {

        if (show) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    override fun showErrorMessage(error: String) {
        Log.e(TAG, "ERRR $error")
        Toast.makeText(this, "Error $error", Toast.LENGTH_LONG).show()
    }

}
