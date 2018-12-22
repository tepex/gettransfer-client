package com.kg.gettransfer.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent

import android.media.RingtoneManager

import android.os.Build
import android.os.Handler
import android.os.Looper

import android.support.annotation.CallSuper
import android.support.v4.app.NotificationCompat

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.presentation.view.OffersView
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.coroutines.Job

import org.koin.standalone.get
import org.koin.standalone.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Router

import timber.log.Timber

class OffersPushService : KoinComponent, FirebaseMessagingService() {
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get<CoroutineContexts>(), compositeDisposable)
    private val offerInteractor: OfferInteractor by inject()
    private val systemInteractor: SystemInteractor by inject()
    private val router: Router by inject()

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        // https://stackoverflow.com/questions/37959588/no-notification-sound-when-sending-notification-from-firebase-in-android
        Timber.d("From: ${remoteMessage?.from}")
        Timber.d("collapse key: ${remoteMessage?.collapseKey}")
        Timber.d("message type: ${remoteMessage?.messageType}")

        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Timber.d("Message Notification Body: ${it.body}")
            Timber.d("URL: ${it.link}")
            Timber.d("Tag: ${it.tag}")
            Timber.d("Title: ${it.title}")
            Timber.d("Body key: ${it.bodyLocalizationKey}")

            //sendNotification(it.body!!, it.title!!)
            Handler(Looper.getMainLooper()).post { router.navigateTo(Screens.Offers(offerInteractor.lastTransferId)) }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String?) {
        Timber.d("Refreshed token: $token")
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                it.result?.token?.let {
                    Timber.d("[FCM token]: $it")
                    utils.runAlien { systemInteractor.registerPushToken(it) }
                }
            } else Timber.w("getInstanceId failed", it.exception)
        })
    }
    // [END on_new_token]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, title: String) {
        val intent = Intent(this, OffersActivity::class.java).apply {
            putExtra(OffersView.EXTRA_TRANSFER_ID, -555L)
        }
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.new_offer_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}
