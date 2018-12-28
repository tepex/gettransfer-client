package com.kg.gettransfer.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.presentation.view.OffersView
import kotlinx.coroutines.Job
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class NotificationManager(val context: Context) : KoinComponent {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get(), compositeDisposable)

    companion object {
        const val OFFER_CHANEL_ID = "offer_chanel"
        const val OFFER_GROUP = "offer_group"
        const val OFFER_GROUP_ID = -1
    }

    fun showOfferNotification(offer: OfferModel) {
        val layout = RemoteViews(context.packageName, R.layout.notification_offer)

        val notification = createNotification(offer.transferId, layout)
        val groupNotification = createGroupNotification()
        createNotificationChannel()

        val carName = offer.vehicle.name
        val carNumber = offer.vehicle.registrationNumber
        val carColor = offer.vehicle.color
        layout.setTextViewText(R.id.tvCarInfo, "$carName $carNumber $carColor")

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

    private fun createNotification(transferId: Long, layout: RemoteViews): Notification? {
        val pendingIntent: PendingIntent = createPendingIntent(transferId)

        return NotificationCompat.Builder(context, OFFER_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_offer_notification)
                .setCustomContentView(layout)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(OFFER_GROUP)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setVibrate(longArrayOf(0))
                .build()
    }

    private fun createGroupNotification(): Notification? {

        return NotificationCompat.Builder(context, OFFER_CHANEL_ID)
                .setSmallIcon(R.drawable.ic_offer_notification)
                .setContentText(context.getString(com.kg.gettransfer.R.string.new_offers))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setGroup(OFFER_GROUP)
                .setGroupSummary(true)
                .setAutoCancel(true)
                .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setVibrate(longArrayOf(0))
                .build()
    }

    private fun createPendingIntent(transferId: Long): PendingIntent {
        val intent = Intent(context, OffersActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.offer_channel_name)
            val descriptionText = context.getString(R.string.offer_channel_description)
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(OFFER_CHANEL_ID, name, importance).apply {
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