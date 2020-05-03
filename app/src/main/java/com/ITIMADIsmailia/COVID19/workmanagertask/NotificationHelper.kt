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
import com.ITIMADIsmailia.COVID19.SettingActivity

class NotificationHelper(base: Context?, countryName: String) : ContextWrapper(base) {

   private var collapsedView: RemoteViews
   private var expandedView: RemoteViews
   private var clickPendingIntent: PendingIntent
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
            .setCustomContentView(collapsedView)
            //.setCustomBigContentView(expandedView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(clickPendingIntent)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))


    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
        var counter: Int  = 0


    }

    init {
        var title  = "There is new cases in $countryName"
        collapsedView = RemoteViews(packageName,R.layout.notification_collapsed)
        expandedView = RemoteViews(packageName,R.layout.notification_expanded)
        collapsedView.setTextViewText(R.id.notification_title, title);
        val clickedIntent = Intent(this, MainActivity::class.java)
        clickPendingIntent = PendingIntent.getActivity(applicationContext,0,
            clickedIntent,0)
        collapsedView.setOnClickPendingIntent(R.layout.notification_collapsed,clickPendingIntent)
        expandedView.setOnClickPendingIntent(R.layout.notification_expanded,clickPendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
    }
}