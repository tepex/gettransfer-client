package com.kg.gettransfer.utilities

import com.kg.gettransfer.R
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.FileProvider
import com.kg.gettransfer.presentation.ui.Utils
import java.io.File

internal class CommunicateMethods {
    companion object {
        const val EMAIL_DATA = "mailto:"
        const val EMAIL_TYPE = "text/*"
        const val DIAL_SCHEME = "tel"

        fun sendEmail(context: Context, emailCarrier: String?, logsFile: File?) {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.type = EMAIL_TYPE
            emailIntent.data = Uri.parse(EMAIL_DATA)
            if(emailCarrier != null) emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(emailCarrier))
            else{
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email_support)))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.LNG_EMAIL_SUBJECT))
                logsFile?.let {
                    val path = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), it)
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path)
                }
            }
            try {
                context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_email)))
            } catch (ex: android.content.ActivityNotFoundException) {
                Utils.showShortToast(context, context.getString(R.string.no_email_apps))
            }
        }

        fun callPhone(context: Context, phoneCarrier: String){
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts(DIAL_SCHEME, phoneCarrier, null))
            context.startActivity(callIntent)
        }
    }
}