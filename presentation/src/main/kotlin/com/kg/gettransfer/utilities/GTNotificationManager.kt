package com.kg.gettransfer.utilities

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.ui.OffersActivity

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.getEmptyImageRes
import com.kg.gettransfer.presentation.ui.ChatActivity
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.ChatView
import com.kg.gettransfer.presentation.view.OffersView
import org.koin.core.KoinComponent
import java.lang.UnsupportedOperationException

class GTNotificationManager(val context: Context) : ContextWrapper(context), KoinComponent {

    companion object {
        const val OFFER_CHANEL_ID = "offer_chanel"
        const val OFFER_GROUP     = "offer_group"
        const val OFFER_GROUP_ID  = -1

        const val MESSAGE_CHANEL_ID = "message_chanel"
        const val MESSAGE_GROUP     = "message_group"
        const val MESSAGE_GROUP_ID  = 1

        const val EMPTY_GROUP = 2
    }

    @SuppressLint("CheckResult")
    fun showOfferNotification(offer: OfferModel) {
        offer.vehicle.photos.firstOrNull()?.let {
            Glide.with(context)
                .asBitmap()
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .listener(object : RequestListener<Bitmap>{
                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        showOfferNotification(offer, resource)
                        return true
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        showOfferNotification(offer, null)
                        return false
                    }
                }).submit()
        } ?: showOfferNotification(offer, null)
    }

    private fun showOfferNotification(offer: OfferModel, photo: Bitmap?) {
        val img = photo
            ?: Utils.getRoundedBitmap(
                context,
                offer.vehicle.transportType.id.getEmptyImageRes(),
                R.color.color_gtr_light_grey
            )
        val notification = createNotification(offer.transferId, OFFER_CHANEL_ID, OFFER_GROUP)?.apply {
            setContentTitle(context.getString(R.string.LNG_NEW_OFFER_TITLE))
            offer.vehicle.let { setContentText("${it.name} ${it.registrationNumber} ${it.color ?: ""}") }
            setLargeIcon(img)
        }?.build()

        with(NotificationManagerCompat.from(context)) {
            notification?.let { notify(offer.id.toInt(), it) }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                val groupNotification = createGroupNotification(OFFER_CHANEL_ID, OFFER_GROUP)
                groupNotification?.let { notify(OFFER_GROUP_ID, it) }
            }
        }
    }

    fun showNewMessageNotification(transferId: Long, countNewMessages: Int) {
        val notification = createNotification(transferId, MESSAGE_CHANEL_ID, MESSAGE_GROUP)?.apply {
            setContentTitle(context.getString(R.string.LNG_NEW_DRIVER_CHAT_TITLE))
            if (countNewMessages > 0) setContentText(context.getString(R.string.LNG_UNREAD_MESSAGES_COUNT, countNewMessages.toString()))
            setLargeIcon(AppCompatResources.getDrawable(context, R.mipmap.launcher_icon)?.toBitmap())
        }?.build()

        with(NotificationManagerCompat.from(context)) {
            notification?.let { notify(transferId.toInt(), it) }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                val groupNotification = createGroupNotification(MESSAGE_CHANEL_ID, MESSAGE_GROUP)
                groupNotification?.let { notify(MESSAGE_GROUP_ID, it) }
            }
        }
    }

    private fun createNotification(transferId: Long, chanelId: String, group: String): NotificationCompat.Builder? {
        val pendingIntent: PendingIntent = when(chanelId){
            OFFER_CHANEL_ID -> createOfferPendingIntent(transferId)
            MESSAGE_CHANEL_ID -> createChatPendingIntent(transferId)
            else -> throw UnsupportedOperationException()
        }

        return NotificationCompat.Builder(context, chanelId)
            .setSmallIcon(R.drawable.ic_offer_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(group)
            .setPriority(NotificationManagerCompat.IMPORTANCE_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setVibrate(longArrayOf(0))
    }

    private fun createGroupNotification(chanelId: String, group: String): Notification? {

        return NotificationCompat.Builder(context, chanelId)
            .setSmallIcon(R.drawable.ic_offer_notification)
            .setContentText(context.getString(R.string.LNG_NEW_OFFER_TITLE))
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
            putExtra(OffersView.EXTRA_ORIGIN, OffersView.SOURCE_NOTIFICATION)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createChatPendingIntent(transferId: Long): PendingIntent {
        val intent = Intent(context, ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun clearNotifications(notificationsIds: List<Int>) = notificationsIds.forEach { clearNotification(it) }

    fun clearNotification(notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val statusBarNotifications = notificationManager.activeNotifications
            val groupKey = statusBarNotifications.find { it.id == notificationId }?.groupKey
            val groupNotifications = statusBarNotifications.filter { it.groupKey == groupKey }
            if (groupNotifications.size == EMPTY_GROUP) {
                groupNotifications.forEach {
                    notificationManager.cancel(it.id)
                }
                return
            }
        }

        notificationManager.cancel(notificationId)
    }
}