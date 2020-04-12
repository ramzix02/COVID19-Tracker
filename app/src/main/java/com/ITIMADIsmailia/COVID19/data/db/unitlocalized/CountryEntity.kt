package com.ITIMADIsmailia.COVID19.data.db.unitlocalized

import androidx.room.ColumnInfo

class CountryEntity(
    @ColumnInfo(name = "country_name")
    override val countryName: String,

    @ColumnInfo(name = "new_cases")
    override val newCases: String,

    @ColumnInfo(name = "cases")
    override val totalCases: String,

    @ColumnInfo(name = "total_recovered")
    override val totalRecovered: String,

    @ColumnInfo(name = "deaths")
    override val totalDeath: String
): UnitCountriesStat
