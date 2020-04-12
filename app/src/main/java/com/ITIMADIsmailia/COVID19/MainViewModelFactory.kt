package com.ITIMADIsmailia.COVID19

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepository

class MainViewModelFactory (
    private val countryStateRepository:CountryStateRepository
): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(countryStateRepository) as T
    }
}