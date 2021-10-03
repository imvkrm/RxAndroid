package com.vikram.rxandroidsampleapp.api

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory




class ServiceGenerator {
    private val BASE_URL = "https://jsonplaceholder.typicode.com"

    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//To convert Retrofit Call objects into Flowables/Observables.
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit:Retrofit = retrofitBuilder.build()


    private val requestApi: RequestApi = retrofit.create(RequestApi::class.java)

    fun getRequestApi(): RequestApi {
        return requestApi
    }
}