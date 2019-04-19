package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.navigation_view_menu_item.view.*

class NavigationViewMenuItem @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View =
            LayoutInflater.from(context)
                    .inflate(R.layout.navigation_view_menu_item, this, true)

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.NavigationViewMenuItem)
            val drawableResId = ta.getResourceId(R.styleable.NavigationViewMenuItem_menu_icon_img, -1)
            menu_item_image.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
            menu_item_title.text = ta.getString(R.styleable.NavigationViewMenuItem_menu_title_text)
            menu_item_subtitle.text = ta.getString(R.styleable.NavigationViewMenuItem_menu_subtitle_text)
            ta.recycle()
        }
    }
}