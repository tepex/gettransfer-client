package com.kg.gettransfer.presentation.androidAutoTest

import android.graphics.Bitmap
import android.os.Environment.DIRECTORY_PICTURES

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot

import java.io.IOException

import org.junit.rules.TestWatcher
import org.junit.runner.Description

class IDTScreenCaptureProcessor : BasicScreenCaptureProcessor() {
    init {
        mTag = "IDTScreenCaptureProcessor"
        mFileNameDelimiter = "-"
        mDefaultFilenamePrefix = "Giorgos"
        mDefaultScreenshotPath = getNewFilename()
    }

    private fun getNewFilename() =
        getInstrumentation().targetContext.applicationContext.getExternalFilesDir(DIRECTORY_PICTURES)
}

class ScreenshotTestRule : TestWatcher() {
    override fun finished(description: Description?) {
        super.finished(description)

        val className = description?.testClass?.simpleName ?: "NullClassname"
        val methodName = description?.methodName ?: "NullMethodName"
        val filename = "$className - $methodName"

        val capture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG

        val processors = HashSet<ScreenCaptureProcessor>()
        processors.add(IDTScreenCaptureProcessor())

        try {
            capture.process(processors)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}
