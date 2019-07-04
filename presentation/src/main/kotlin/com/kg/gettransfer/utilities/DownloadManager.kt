package com.kg.gettransfer.utilities

import android.content.Context
import android.os.Environment
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

class DownloadManager(val context: Context): KoinComponent {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get(), compositeDisposable)

    private val transferInteractor: TransferInteractor by inject()

    fun downloadVoucher(transferId: Long) {
        utils.launchSuspend {
            utils.asyncAwait { transferInteractor.downloadVoucher(transferId) }
                    .isSuccess().let {
                        saveVoucher(it, transferId)
                    }
        }
    }

    private fun saveVoucher(content: InputStream?, transferId: Long) {
        content?.let {
            val folderName = getVouchersFolderName()
            val root = getVouchersFolder(folderName)
            val voucher = File(root, "$VOUCHER_START_NAME$transferId$VOUCHER_EXTENSION")

            content.use { input ->
                voucher.outputStream().use {
                    input.copyTo(it)
                }
            }
        }
    }

    private fun getVouchersFolderName(): String =
            context.getString(R.string.app_name) + File.separator + VOUCHERS_FOLDER

    private fun getVouchersFolder(downloadFolder: String): File {
        val file = Environment.getExternalStoragePublicDirectory(downloadFolder)
        if (!file.mkdirs()) {
            Timber.e("Directory not created")
        }
        return file
    }

    companion object {
        private const val VOUCHERS_FOLDER = "Vouchers"
        private const val VOUCHER_EXTENSION = ".pdf"
        private const val VOUCHER_START_NAME = "voucher_"
    }
}