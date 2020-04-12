package com.ITIMADIsmailia.COVID19.data.repositry

import androidx.lifecycle.LiveData
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat

interface CountryStateRepository {
    suspend fun getCountryStatList(hoursToFetch: Long):LiveData <out List<UnitCountriesStat>>
}