package com.ITIMADIsmailia.COVID19

import androidx.lifecycle.ViewModel
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepository
import com.ITIMADIsmailia.COVID19.internal.lazyDeferred

class MainViewModel(
    private val countryStateRepository:CountryStateRepository): ViewModel(){
    var hoursCount:Long = 1
    val countryStat by lazyDeferred {
        countryStateRepository.getCountryStatList(hoursCount)
    }
}