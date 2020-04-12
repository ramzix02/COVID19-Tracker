package com.ITIMADIsmailia.COVID19.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ITIMADIsmailia.COVID19.data.db.entity.CountriesStatEntity
import com.ITIMADIsmailia.COVID19.data.db.unitlocalized.CountryEntity

@Dao
interface CountryStatDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(countryEntry: List<CountriesStatEntity>)

    @Query("select * from cases_by_country")
    fun getCountryState(): LiveData<List<CountryEntity>>

    @Query("select count(id) from cases_by_country")
    fun countCountryState(): Int

    @Query("delete from cases_by_country")
    fun deleteOldCountryState()

/*
    @Query("delete from future_weather where date(date) < date(:firstDateToKeep)")
    fun deleteOldEntries(firstDateToKeep: LocalDate)
*/
}