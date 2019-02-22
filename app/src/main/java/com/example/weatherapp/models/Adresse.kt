package com.example.weatherapp.models

data class Adresse(val nomVille: String, val tempF: Int, val tempC: Int, val imageUrl:String, val heureRecup: String,
                   val adresseVal: String)

/*
heureRecup "observation_time_rfc822": "Thu, 21 Feb 2019 09:00:00 +0100",
nomVille response.display_location.city
tempC response.temp_c
tempF response.temp_f
imageUrl response.icon_url

 */
