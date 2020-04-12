package com.ITIMADIsmailia.COVID19.data

import com.ITIMADIsmailia.COVID19.data.db.network.ConnectivityInterceptor
import com.ITIMADIsmailia.COVID19.data.db.network.response.CountryCaseResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET



const val API_KEY = "182fb5f465msh558f7d4242cc47ep1c6f67jsn5a82b6162d1a"
const val API_HOST = "coronavirus-monitor.p.rapidapi.com"
//http://api.apixu.com/v1/current.json?key=89e8bd89085b41b7a4b142029180210&q=London&lang=en
//https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php
interface COVID19TrackerApiService {

    @GET("cases_by_country.php")
    fun getCurrentWeather(): Deferred<CountryCaseResponse>


    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): COVID19TrackerApiService {

            val requestInterceptor = Interceptor { chain ->

                val url = chain.request()
                    .url()
                    .newBuilder()
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .addHeader("x-rapidapi-key", API_KEY)
                    .addHeader("x-rapidapi-host", API_HOST)
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://coronavirus-monitor.p.rapidapi.com/coronavirus/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(COVID19TrackerApiService::class.java)
        }
    }
}