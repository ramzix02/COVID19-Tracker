package com.ITIMADIsmailia.COVID19.data.db.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

//Future Entry
@Entity(tableName = "cases_by_country")
data class CountriesStatEntity(

    @PrimaryKey(autoGenerate = true)
    val id:Int? = null,
    //@SerializedName("country_name")
    val country_name: String="",
//    @SerializedName("new_cases")
    val new_cases: String,
//    @SerializedName("total_recovered")
    val total_recovered: String,
    val cases: String,
    val deaths: String

)