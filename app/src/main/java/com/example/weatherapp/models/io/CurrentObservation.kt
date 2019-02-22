package com.example.weatherapp.models.io

data class CurrentObservation(val display_location: DisplayLocation, val observation_time_rfc822: String,
                              val temp_c: Int, val temp_f: Int, val icon_url: String)
