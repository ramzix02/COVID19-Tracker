package com.ITIMADIsmailia.COVID19.workmanagertask

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import java.lang.Exception
import kotlin.concurrent.thread

class MyJobService : JobService() {
    val Tag = "MyJobService"
    var jobCancelled = false
    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d(Tag, "job started")
        //doBackgroundWork(params)
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
        NotificationHelper.title = "${NotificationHelper.counter}"
        val notificationHelper = NotificationHelper(applicationContext)
        val nb = notificationHelper.channelNotification
        notificationHelper.manager!!.notify(NotificationHelper.counter, nb.build())
        jobFinished(params, false)
    }

}