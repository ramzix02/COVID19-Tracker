package com.ITIMADIsmailia.COVID19.workmanagertask

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.ITIMADIsmailia.COVID19.MainActivity
import com.ITIMADIsmailia.COVID19.MainViewModel
import com.ITIMADIsmailia.COVID19.MainViewModelFactory
import com.ITIMADIsmailia.COVID19.ScopedActivity
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepository
import com.ITIMADIsmailia.COVID19.data.repositry.CountryStateRepositoryImpl
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.lang.Exception
import kotlin.concurrent.thread

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class MyJobService() : JobService() {
    val Tag = "MyJobService"
    var jobCancelled = false

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(Tag, "job started")

/* var obj= MainActivity()
        //var countryStateRepository: CountryStateRepositoryImpl = CountryStateRepositoryImpl()
        obj.makeViewModel().hoursCount = 2
        obj.makeViewModel().countryStat*/

        bulidNotification(params)
        return true
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d(Tag, "job cancelled before completion")
        jobCancelled = true
        return true
    }

    fun bulidNotification(params: JobParameters?) {
        NotificationHelper.counter += 1
       // NotificationHelper.title = "${NotificationHelper.counter}"
        val notificationHelper = NotificationHelper(applicationContext,"")
        val nb = notificationHelper.channelNotification
        notificationHelper.manager!!.notify(NotificationHelper.counter, nb.build())
        jobFinished(params, false)
    }

}