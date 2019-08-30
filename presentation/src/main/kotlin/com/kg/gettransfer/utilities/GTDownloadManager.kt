package com.kg.gettransfer.utilities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
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
import android.app.Notification
import android.content.pm.PackageManager

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
                    showDownloadError(transferId)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.LNG_VOUCHER)
            val descriptionText =
                    context.getString(R.string.LNG_DOWNLOADING)
                    .plus(" " + context.getString(R.string.LNG_VOUCHER))
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() {
        builder = NotificationCompat.Builder(context, CHANEL_ID).apply {
            setContentTitle(context.getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER))
            setContentText(context.getString(R.string.LNG_DOWNLOADING))
            setSmallIcon(android.R.drawable.stat_sys_download)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setDefaults(Notification.DEFAULT_VIBRATE)
            setAutoCancel(false)
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_HIGH
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

    /**
     * Download complete with success
     */
    private fun showCompletedNotification(voucher: File, fileName: String, transferId: Long) {
        val pendingIntent = createPendingIntent(voucher)
        builder.apply {
            setContentIntent(pendingIntent)
            setContentTitle(fileName)
            setContentText(context.getString(R.string.LNG_DOWNLOAD_COMPLETED))
            setSmallIcon(R.drawable.ic_download)
            setProgress(0, 0, false)
            setAutoCancel( canDisplayPdf(context) )
            setOngoing(false)
            addAction(R.drawable.ic_menu_share, context.getString(R.string.share), createShareAction(voucher))
        }
        notificationManager.notify(transferId.toInt(), builder.build())
    }

    /**
     * Create action for share button
     */
    private fun createShareAction(voucher: File) :PendingIntent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), voucher)
        else Uri.fromFile(voucher)
        intent.putExtra(Intent.EXTRA_STREAM, data)
        intent.type = VOUCHER_MIME_TYPE
        intent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.share))
        // Grant read permission on the file to other apps without declared permission.
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    }

    /**
     * Download complete with error
     */
    private fun showDownloadError(transferId: Long) {
        builder.apply {
            setContentText(context.getString(R.string.LNG_ERROR))
            setSmallIcon(R.drawable.ic_download)
            setProgress(0, 0, false)
            setAutoCancel(true)
            setOngoing(false)
        }
        notificationManager.notify(transferId.toInt(), builder.build())
    }

    /**
     * Create pending intent for view pdf file
     */
    private fun createPendingIntent(voucher: File): PendingIntent {
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), voucher)
        else Uri.fromFile(voucher)
        val target = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NO_HISTORY
            setDataAndType(data, VOUCHER_MIME_TYPE)
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

    /**
     * Check if the supplied context can render PDF files via some installed application that reacts to a intent
     * with the pdf mime type and viewing action.
     *
     * @param context
     * @return
     */
    private fun canDisplayPdf(context: Context): Boolean {
        val packageManager = context.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.type = VOUCHER_MIME_TYPE
        return packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size > 0
    }

    companion object {
        private const val CHANEL_ID = "voucher chanel"
        const val VOUCHERS_FOLDER = "Vouchers"
        private const val VOUCHER_EXTENSION = ".pdf"
        private const val VOUCHER_START_NAME = "voucher_"
        private const val VOUCHER_MIME_TYPE = "application/pdf"
    }
}