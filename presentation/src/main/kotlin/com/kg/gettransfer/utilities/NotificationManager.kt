package com.kg.gettransfer.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.presentation.ui.ChatActivity
import com.kg.gettransfer.presentation.view.ChatView
import com.kg.gettransfer.presentation.view.OffersView
import kotlinx.coroutines.Job
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import java.lang.UnsupportedOperationException

class NotificationManager(val context: Context) : KoinComponent {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get(), compositeDisposable)

    companion object {
        const val OFFER_CHANEL_ID = "offer_chanel"
        const val OFFER_GROUP = "offer_group"
        const val OFFER_GROUP_ID = -1

        const val MESSAGE_CHANEL_ID = "message_chanel"
        const val MESSAGE_GROUP = "message_group"
        const val MESSAGE_GROUP_ID = 1
    }

    fun showOfferNotification(offer: OfferModel) {
        val layout = RemoteViews(context.packageName, R.layout.notification_view)

        val notification = createNotification(offer.transferId, layout, OFFER_CHANEL_ID, OFFER_GROUP)
        val groupNotification = createGroupNotification(OFFER_CHANEL_ID, OFFER_GROUP)
        createNotificationChannel(OFFER_CHANEL_ID)

        val carName = offer.vehicle.name
        val carNumber = offer.vehicle.registrationNumber
        val carColor = offer.vehicle.color
        layout.setViewVisibility(R.id.tvCarInfo, View.VISIBLE)
        layout.setTextViewText(R.id.tvCarInfo, "$carName $carNumber $carColor")
        layout.setTextViewText(R.id.tvTitle, context.getString(R.string.LNG_NEW_OFFER_TITLE))
        layout.setTextViewText(R.id.tvContent, context.getString(R.string.LNG_NEW_OFFER_MESSAGE))

        if (!offer.vehicle.photos.isEmpty()) {
            utils.launchSuspend {
                val notificationTarget = NotificationTarget(context, R.id.ivCarPhoto, layout, notification, offer.id.toInt())
                Glide.with(context)
                        .asBitmap()
                        .load(offer.vehicle.photos.first())
                        .apply(RequestOptions.circleCropTransform())
                        .into(notificationTarget)
            }
        }

        with(NotificationManagerCompat.from(context)) {
            notification?.let { notify(offer.id.toInt(), it) }
            groupNotification?.let { notify(OFFER_GROUP_ID, it) }
        }
    }

    fun showNewMessageNotification(transferId: Long, countNewMessages: Int, isMessageByDriver: Boolean) {
        val layout = RemoteViews(context.packageName, R.layout.notification_view)

        val notification = createNotification(transferId, layout, MESSAGE_CHANEL_ID, MESSAGE_GROUP)
        val groupNotification = createGroupNotification(MESSAGE_CHANEL_ID, MESSAGE_GROUP)
        createNotificationChannel(MESSAGE_CHANEL_ID)

        layout.setViewVisibility(R.id.tvCarInfo, View.GONE)
        layout.setTextViewText(R.id.tvTitle, context.getString( if(isMessageByDriver) R.string.LNG_NEW_DRIVER_CHAT_TITLE else R.string.LNG_NEW_PASSENGER_CHAT_TITLE))
        layout.setTextViewText(R.id.tvContent, context.getString(R.string.LNG_UNREAD_MESSAGES_COUNT, countNewMessages.toString()))

        /*if (!offer.vehicle.photos.isEmpty()) {
            utils.launchSuspend {
                val notificationTarget = NotificationTarget(context, R.id.ivCarPhoto, layout, notification, transferId.toInt())
                Glide.with(context)
                        .asBitmap()
                        .load(offer.vehicle.photos.first())
                        .apply(RequestOptions.circleCropTransform())
                        .into(notificationTarget)
            }
        }*/
        layout.setImageViewResource(R.id.ivCarPhoto, R.mipmap.launcher_icon)

        with(NotificationManagerCompat.from(context)) {
            notification?.let { notify(transferId.toInt(), it) }
            groupNotification?.let { notify(MESSAGE_GROUP_ID, it) }
        }
    }

    private fun createNotification(transferId: Long, layout: RemoteViews, chanelId: String, group: String): Notification? {
        val pendingIntent: PendingIntent = when(chanelId){
            OFFER_CHANEL_ID -> createOfferPendingIntent(transferId)
            MESSAGE_CHANEL_ID -> createChatPendingIntent(transferId)
            else -> throw UnsupportedOperationException()
        }

        return NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.drawable.ic_offer_notification)
                .setCustomContentView(layout)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(group)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setVibrate(longArrayOf(0))
                .build()
    }

    private fun createGroupNotification(chanelId: String, group: String): Notification? {

        return NotificationCompat.Builder(context, chanelId)
                .setSmallIcon(R.drawable.ic_offer_notification)
                .setContentText(context.getString(com.kg.gettransfer.R.string.new_offers))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(group)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .build()
    }

    private fun createOfferPendingIntent(transferId: Long): PendingIntent {
        val intent = Intent(context, OffersActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createChatPendingIntent(transferId: Long): PendingIntent {
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
        }
        return PendingIntent.getActivity(context, transferId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel(chanelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.offer_channel_name)
            val descriptionText = context.getString(R.string.offer_channel_description)
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(chanelId, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            val notificationManager: android.app.NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE)
                            as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}