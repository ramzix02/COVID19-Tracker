package com.ITIMADIsmailia.COVID19.data.repositry

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.ITIMADIsmailia.COVID19.data.db.CountryStatDAO
import com.ITIMADIsmailia.COVID19.data.db.network.CountryStatDataSource
import com.ITIMADIsmailia.COVID19.data.db.network.response.CountryCaseResponse
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.UnitCountriesStat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class CountryStateRepositoryImpl(
    private val countryStatDAO: CountryStatDAO,
    private val countryStatDataSource: CountryStatDataSource
): CountryStateRepository {

    init {
        countryStatDataSource.apply {
            downloadedCountryStat.observeForever { newCountryStat ->
                persistFetchedCountryStat(newCountryStat)
            }
        }
    }

    override suspend fun getCountryStatList(hoursToFetch: Long): LiveData<out List<UnitCountriesStat>> {
        initCountryStatData(hoursToFetch)
        return withContext(Dispatchers.IO) {
            return@withContext countryStatDAO.getCountryState()
        }
    }

    private fun persistFetchedCountryStat(fetchedCountry: CountryCaseResponse) {
        fun deleteOldData(){
            countryStatDAO.deleteOldCountryState()
        }
        GlobalScope.launch(Dispatchers.IO) {
            deleteOldData()
            countryStatDAO.upsert(fetchedCountry.countriesStat)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun initCountryStatData(myHours:Long){
        if(isFetchedCurrentNeeded(ZonedDateTime.now().minusHours(myHours)))
            fetchCountryStat()
    }

    private suspend fun fetchCountryStat(){
        countryStatDataSource.fetchCountryStat()
    }
            @RequiresApi(Build.VERSION_CODES.O)
    private fun isFetchedCurrentNeeded(lastFetchedTime: ZonedDateTime):Boolean{
        val valMunitesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchedTime.isBefore(valMunitesAgo)
    }
}