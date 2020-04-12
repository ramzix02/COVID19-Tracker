package com.ITIMADIsmailia.COVID19.data.db.network

import androidx.lifecycle.LiveData
import com.ITIMADIsmailia.COVID19.data.db.network.response.CountryCaseResponse

interface CountryStatDataSource {
    val downloadedCountryStat: LiveData<CountryCaseResponse>

    suspend fun fetchCountryStat( )

}