package com.kg.gettransfer.presentation

import android.content.Context
import android.util.Log

import com.kg.gettransfer.R
import org.koin.core.KoinComponent
import org.koin.core.inject

import timber.log.Timber
import java.util.logging.Logger

class FileLoggingTree(): Timber.DebugTree(), KoinComponent {
    private val TAG = FileLoggingTree::class.java.simpleName
    private val javaLogger: Logger by inject()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            if (message.contains(PRIVATE_DATA.PASSWORD.keyWord)) return

            javaLogger.info(formatJsonString(message))

//            context.openFileOutput(context.getString(R.string.logs_file_name), Context.MODE_APPEND).use {
//                it.write("\n\n".toByteArray())
//                val foramtedString = formatJsonString(message)
//                if(foramtedString.indexOf("\n") >= 0) it.write(foramtedString.toByteArray())
//                else it.write(message.toByteArray())
//            }

        } catch(e: Exception) {
            Log.e(TAG, "Error while logging into file!", e)
        }

    }
    
    private fun formatJsonString(text: String) = buildString {
        var indentString = ""
        text.forEach {
            when(it) {
                '{', '[' -> {
                    append("\n$indentString$it\n")
                    indentString += "\t"
                    append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    append("\n$indentString$it")
                }
                ','  -> append("$it\n$indentString")
                else -> append(it)
            }
        }
    }

    companion object {
        const val LOGGER_NAME = "GTUserLog"
    }

    enum class PRIVATE_DATA(val keyWord: String){
        PASSWORD("password")
    }
}
