package com.ITIMADIsmailia.COVID19.data.db.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ITIMADIsmailia.COVID19.data.COVID19TrackerApiService
import com.ITIMADIsmailia.COVID19.data.db.network.response.CountryCaseResponse
import com.ITIMADIsmailia.COVID19.internal.NoConnectivityException

class CountryStatDataSourceImpl(
    private val cOVID19TrackerApiService: COVID19TrackerApiService
) : CountryStatDataSource {


    private val _downloadedCountryStat = MutableLiveData<CountryCaseResponse>()
    override val downloadedCountryStat: LiveData<CountryCaseResponse>
        get() = _downloadedCountryStat

    override suspend fun fetchCountryStat() {
    try {
        val fetchedCountryStat = cOVID19TrackerApiService
            .getCurrentWeather()
            .await()
        _downloadedCountryStat.postValue(fetchedCountryStat)
    }
    catch (e: NoConnectivityException) {
        Log.e("Connectivity", "No internet connection.", e)
    }

}
}
