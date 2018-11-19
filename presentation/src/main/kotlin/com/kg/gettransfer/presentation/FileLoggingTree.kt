package com.kg.gettransfer.presentation

import android.content.Context
import android.util.Log

import com.kg.gettransfer.R

import timber.log.Timber

class FileLoggingTree(private val context: Context): Timber.DebugTree() {
    private val TAG = FileLoggingTree::class.java.simpleName

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        try {
            context.openFileOutput(context.getString(R.string.logs_file_name), Context.MODE_APPEND).use {
                it.write("\n\n".toByteArray())
                val foramtedString = formatJsonString(message)
                if(foramtedString.indexOf("\n") >= 0) it.write(foramtedString.toByteArray())
                else it.write(message.toByteArray())
            }

        } catch(e: Exception) {
            Log.e(TAG, "Error while logging into file!", e)
        }

    }
    
    private fun formatJsonString(text: String) = buildString {
        var indentString = ""
        text.forEach {
            when(it) {
                '{', '[' -> {
                    append("\n${indentString}${it}\n")
                    indentString += "\t"
                    append(indentString)
                }
                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    append("\n${indentString}${it}")
                }
                ','  -> append("${it}\n${indentString}")
                else -> append(it)
            }
        }
    }
}
