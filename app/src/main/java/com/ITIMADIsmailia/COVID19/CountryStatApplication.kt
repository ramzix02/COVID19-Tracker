package com.ITIMADIsmailia.COVID19

import android.app.Application
import com.ITIMADIsmailia.COVID19.data.COVID19TrackerApiService
import com.ITIMADIsmailia.COVID19.data.db.CountryDataBase
import com.ITIMADIsmailia.COVID19.data.db.network.ConnectivityInterceptor
import com.ITIMADIsmailia.COVID19.data.db.network.ConnectivityInterceptorImpl
import com.ITIMADIsmailia.COVID19.data.db.network.CountryStatDataSource
import com.ITIMADIsmailia.COVID19.data.db.network.CountryStatDataSourceImpl
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepository
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton


class CountryStatApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@CountryStatApplication))

        bind() from singleton { CountryDataBase(instance()) }
        bind() from singleton { instance<CountryDataBase>().countryStatDAO() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { COVID19TrackerApiService(instance()) }
        bind<CountryStatDataSource>() with singleton { CountryStatDataSourceImpl(instance()) }
        bind<CountryStateRepository>() with singleton { CountryStateRepositoryImpl(instance(), instance()) }
        bind() from provider { MainViewModelFactory(instance()) }
    }
}
