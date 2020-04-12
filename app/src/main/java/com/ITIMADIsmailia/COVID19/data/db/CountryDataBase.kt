package com.ITIMADIsmailia.COVID19.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ITIMADIsmailia.COVID19.data.db.entity.CountriesStatEntity


@Database(
    entities = [CountriesStatEntity::class],
    version = 1
)

//@TypeConverters(LocalDateConverter::class)
abstract class CountryDataBase : RoomDatabase() {

    abstract fun countryStatDAO(): CountryStatDAO

    companion object {
        @Volatile private var instance: CountryDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                CountryDataBase::class.java, "CountryState.db")
                .build()
    }
}