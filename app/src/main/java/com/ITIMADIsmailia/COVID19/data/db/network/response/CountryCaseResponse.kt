package com.ITIMADIsmailia.COVID19.data.db.network.response


import com.ITIMADIsmailia.COVID19.data.db.entity.CountriesStatEntity
import com.google.gson.annotations.SerializedName

data class CountryCaseResponse(
    @SerializedName("countries_stat")
    val countriesStat: List<CountriesStatEntity>,
    @SerializedName("statistic_taken_at")
    val statisticTakenAt: String
)