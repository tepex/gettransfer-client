package com.kg.gettransfer.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.FileProvider
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.interactor.TransferInteractor
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import timber.log.Timber
import java.io.File
import java.io.InputStream

class GTDownloadManager(val context: Context): KoinComponent {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get(), compositeDisposable)

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManagerCompat

    private val transferInteractor: TransferInteractor by inject()

    fun downloadVoucher(transferId: Long) {
        createNotification()
        createNotificationChannel()

        notificationManager = NotificationManagerCompat.from(context).apply {
            utils.launchSuspend {
                builder.setProgress(0, 0, true)
                notify(transferId.toInt(), builder.build())

                val result = utils.asyncAwait { transferInteractor.downloadVoucher(transferId) }
                if (result.isSuccess() != null) {
                    saveVoucher(result.model, transferId)
                } else {
                    builder.setAutoCancel(true)
                    builder.setOngoing(false)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Voucher"
            val descriptionText = "Downloading voucher"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("download chanel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() {
        builder = NotificationCompat.Builder(context, "download chanel").apply {
            setContentTitle("Voucher Download")
            setContentText("Download in progress")
            setSmallIcon(R.drawable.ic_download)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setAutoCancel(false)
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_LOW
        }
    }

    private fun saveVoucher(content: InputStream?, transferId: Long) {
        content?.let {
            val folderName = getVouchersFolderName()
            val root = getVouchersFolder(folderName)
            val fileName = "$VOUCHER_START_NAME$transferId$VOUCHER_EXTENSION"
            val voucher = File(root, fileName)

            content.use { input ->
                voucher.outputStream().use {
                    input.copyTo(it)

                    showCompletedNotification(voucher, fileName, transferId)
                }
            }
        }
    }

    private fun showCompletedNotification(voucher: File, fileName: String, transferId: Long) {
        val pendingIntent = createPendingIntent(voucher)
        builder.setContentIntent(pendingIntent)
        builder.setContentTitle(fileName)
        builder.setContentText("Download complete").setProgress(0, 0, false)
        builder.setAutoCancel(true)
        builder.setOngoing(false)
        notificationManager.notify(transferId.toInt(), builder.build())
    }

    private fun createPendingIntent(voucher: File): PendingIntent {
        val data = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), voucher)
        val target = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
            setDataAndType(data, "application/pdf")
        }

        return PendingIntent.getActivity(context, 0, target, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    private fun getVouchersFolderName(): String =
            context.getString(R.string.app_name) + File.separator + VOUCHERS_FOLDER

    private fun getVouchersFolder(downloadFolder: String): File {
        val storage = Environment.getExternalStorageDirectory()
        val file = File(storage, downloadFolder)
        if (!file.mkdirs()) {
            Timber.e("Directory not created")
        }
        return file
    }

    companion object {
        const val VOUCHERS_FOLDER = "Vouchers"
        private const val VOUCHER_EXTENSION = ".pdf"
        private const val VOUCHER_START_NAME = "voucher_"
    }
}