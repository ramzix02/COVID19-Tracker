package com.ITIMADIsmailia.COVID19.workmanagertask

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ITIMADIsmailia.COVID19.MainActivity
import com.ITIMADIsmailia.COVID19.R

class NotificationHelper(base: Context?) : ContextWrapper(base) {

   /* val notificationHelper = NotificationHelper(applicationContext)
    val nb = notificationHelper.channelNotification
    notificationHelper.manager!!.notify(counter, nb.build())*/

    var collapseView: RemoteViews
    var expandedView: RemoteViews
    var clickPendingIntent: PendingIntent
    private var mManager: NotificationManager? = null
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }

    val channelNotification: NotificationCompat.Builder
        get() = NotificationCompat.Builder(
            applicationContext,
            channelID
        )
            .setSmallIcon(R.drawable.coronavirus)
            .setCustomContentView(collapseView)
            //.setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(clickPendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))


    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
        var counter: Int  = 0
        var title : String = ""
    }

    init {
        collapseView = RemoteViews(packageName,R.layout.notification_collapsed)
        expandedView = RemoteViews(packageName,R.layout.notification_expanded)
        val clickedIntent = Intent(this, MainActivity::class.java)
        clickPendingIntent = PendingIntent.getActivity(applicationContext,0,
            clickedIntent,0)
        collapseView.setOnClickPendingIntent(R.layout.notification_collapsed,clickPendingIntent)
        expandedView.setOnClickPendingIntent(R.layout.notification_expanded,clickPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}