package com.ITIMADIsmailia.COVID19.workmanagertask

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParameters: WorkerParameters) :
Worker(appContext, workerParameters) {

    override fun doWork(): Result {
        return  Result.success()
    }
}