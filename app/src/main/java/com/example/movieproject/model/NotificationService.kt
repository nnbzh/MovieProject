package com.example.movieproject.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.movieproject.R
import com.example.movieproject.view.Activities.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService : FirebaseMessagingService(){
    private val notificationId =1
    override fun onMessageReceived(p0: RemoteMessage) {
        if(p0.data.isNotEmpty()){
            sendNotification(p0)
        }
    }
     private fun sendNotification(remoteMessage: RemoteMessage){
         val notificationManager: NotificationManager =
             getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
             val notificationChannel = NotificationChannel(
                 CHANNEL, CHANNEL, NotificationManager.IMPORTANCE_HIGH
             )

             notificationManager.createNotificationChannel(notificationChannel)
         }


         var builder: NotificationCompat.Builder = setNotificationBuilder(remoteMessage)

         val notification: Notification = builder.build()
         notification.flags = Notification.FLAG_AUTO_CANCEL
         notificationManager.notify(notificationId, notification)
     }

    private fun setNotificationBuilder(remoteMessage: RemoteMessage): NotificationCompat.Builder{
        val data: Map<String, String> = remoteMessage.data
        val name: String? = data[NAME]
        val body: String? = data[BODY]
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val intent = Intent( this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this,0,intent , PendingIntent.FLAG_ONE_SHOT)
            return NotificationCompat.Builder(applicationContext, CHANNEL)
                .setSmallIcon(R.drawable.ic_live_tv_black_24dp)
                .setSound(uri)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentTitle(name)
                .setContentText(body)
                .setCustomContentView(fall(name,body))
                .setCustomBigContentView(expand())
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)

    }

    private fun fall(name: String?, content: String?): RemoteViews{
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.name , name)
        remoteViews.setTextViewText(R.id.content, content)
        return remoteViews
    }
    private fun expand(): RemoteViews{
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification_expanded)

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FILM_URL))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,browserIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        remoteViews.setOnClickPendingIntent(R.id.fall_button, pendingIntent)
        return remoteViews
    }
}