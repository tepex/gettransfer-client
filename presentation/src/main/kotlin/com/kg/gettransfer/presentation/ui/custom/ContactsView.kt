package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_contacts.view.*

class ContactsView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
            LayoutInflater.from(context).inflate(R.layout.view_contacts,this, true)
    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.ContactsView).apply {
                val drawableResId = getResourceId(R.styleable.ContactsView_icon_country, View.NO_ID)
                ivIcon.setImageResource(drawableResId)
                tvTitle.text = getString(R.styleable.ContactsView_title_country)
                tvPhone.text = getString(R.styleable.ContactsView_phone)
                recycle()
            }
        }
    }
}