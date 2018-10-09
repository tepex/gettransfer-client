package com.kg.gettransfer.presentation

import com.kg.gettransfer.R

import android.content.Context
import timber.log.Timber
import android.util.Log
import com.kg.gettransfer.presentation.ui.Utils


class FileLoggingTree(private val context: Context): Timber.DebugTree() {

    private val TAG = FileLoggingTree::class.java.simpleName

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        try {
            context.openFileOutput(context.getString(R.string.logs_file_name), Context.MODE_APPEND).use {
                it.write("\n\n".toByteArray())
                val foramtedString = Utils.formatJsonString(message)
                if(foramtedString.indexOf("\n") >= 0) it.write(foramtedString.toByteArray())
                else it.write(message.toByteArray())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error while logging into file : $e")
        }

    }

}