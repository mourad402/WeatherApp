package com.example.weatherapp.ui.base


class BaseContract {


    interface View

    interface Presenter<in T> {
        fun subscribe()
        fun unsubscribe()
        fun attach(view: T)
    }



}