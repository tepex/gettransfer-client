package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.annotation.CallSuper
import android.text.InputType
import android.view.Gravity
import android.view.inputmethod.EditorInfo

import com.kg.gettransfer.R

import kotlinx.android.synthetic.main.activity_text_editor.editText

class TextEditorActivity : AppCompatActivity() {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_editor)
        window.setGravity(Gravity.BOTTOM)

        editText.imeOptions = EditorInfo.IME_ACTION_DONE
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT)

        intent.getStringExtra(TEXT_FOR_CORRECTING)?.let {
            editText.setText(it)
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Intent().apply {
                    putExtra(CORRECTED_TEXT, editText.text.toString())
                    setResult(Activity.RESULT_OK, this)
                    finish()
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    companion object {
        const val REQUEST_CODE = 1
        const val CORRECTED_TEXT = "corrected text"
        const val TEXT_FOR_CORRECTING = "text for correcting"
    }
}
