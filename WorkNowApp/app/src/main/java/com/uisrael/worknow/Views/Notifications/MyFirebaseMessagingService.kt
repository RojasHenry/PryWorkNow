package com.uisrael.worknow.Views.Notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uisrael.worknow.R
import com.uisrael.worknow.Views.MainActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TIPO_OFERTA_ACEPTADA = "OfferAcept"
        const val TIPO_OFERTA_COMENTARIO = "OfferComment"
        val CHANNEL_ID = "worknowchnnl01"
        val CHANNEL_NAME = "worknowchnnl"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("Notificaciones", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("Notificaciones", "Message data payload: ${remoteMessage.data}")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d("Notificaciones", "Message Notification Body: ${it.body}")
        }

        sendNotification(remoteMessage)
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {
        val notification_id = System.currentTimeMillis().toInt()
        var notificationManager: NotificationManager? = null
        var mBuilder: NotificationCompat.Builder? = null

        val tipo = remoteMessage.data["type"]

        //Set pending intent to builder
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        //Notification builder
        if (notificationManager == null) {
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            var mChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (mChannel == null) {
                mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
                mChannel.enableVibration(true)
                mChannel.lightColor = Color.GREEN
                mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                notificationManager.createNotificationChannel(mChannel)
            }
        }

        var title = ""
        var contentText:Any? = null
        var mensajeBig:Any? = null

        when(tipo){
            TIPO_OFERTA_ACEPTADA ->{
                title = "Solicitud aceptada !!"
                contentText = remoteMessage.data["offer"]!!
                mensajeBig = "Tu oferta de trabajo fue aceptada por: "+ remoteMessage.data["profAccept"] +"\nDescripción de oferta:\n"+ remoteMessage.data["offer"] + "\n\n!Comienza la negociación de tu oferta.¡"
            }
            TIPO_OFERTA_COMENTARIO->{
                title = "Nuevo comentario de: "+remoteMessage.data["userEmisor"]
                contentText = remoteMessage.data["comment"]!!
                mensajeBig = remoteMessage.data["comment"]!!
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            mBuilder.setContentTitle(title)
                .setContentText(contentText.toString()) //show icon on status bar
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_feed_24))
                .setSmallIcon(R.drawable.icon_app)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(mensajeBig.toString()))
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setDefaults(Notification.DEFAULT_ALL)
        } else {
            mBuilder = NotificationCompat.Builder(this)
            mBuilder.setContentTitle(title)
                .setContentText(contentText.toString())
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.icon_app)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_feed_24))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(mensajeBig.toString()))
                .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
                .setDefaults(Notification.DEFAULT_VIBRATE)
        }
        notificationManager.notify(notification_id, mBuilder.build())
    }
}