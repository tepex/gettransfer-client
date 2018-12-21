package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatDelegate

import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.presentation.presenter.AboutPresenter
import com.kg.gettransfer.presentation.view.AboutView
import com.kg.gettransfer.utilities.Analytics

import kotlinx.android.synthetic.main.activity_about.*

import timber.log.Timber

class AboutActivity : BaseActivity(), AboutView {

    @InjectPresenter
    internal lateinit var presenter: AboutPresenter

    @ProvidePresenter
    fun createMainPresenter() = AboutPresenter()

    override fun getPresenter(): AboutPresenter = presenter

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = it.get(key)
                Timber.d("Key: $key Value: $value")
            }
        }
        // [END handle_data_extras]

        presenter.openMain = intent.getBooleanExtra(AboutView.EXTRA_OPEN_MAIN, true)

        val adapter = AboutAdapter()
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = adapter.count - 1

        pageIndicator.count = 4
        pageIndicator.setSelected(1)

        btnClose.setOnClickListener {
            presenter.closeAboutActivity()
            if (viewpager.currentItem == viewpager.childCount - 1) presenter.logEvent(0)
            else presenter.logEvent(viewpager.currentItem + 1)
        }
        btnNext.setOnClickListener {
            if (viewpager.currentItem == viewpager.childCount - 1) {
                presenter.closeAboutActivity()
                presenter.logEvent(0)
            } else viewpager.currentItem = viewpager.currentItem + 1
        }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                pageIndicator.setSelected(p0)
                if (p0 == viewpager.childCount - 1) btnNext.text = getString(R.string.LNG_PRESENTATION_FINAL_BUTTON)
                else btnNext.text = getString(R.string.LNG_NEXT)
            }
        })
    }

    override fun onBackPressed() {
        if (viewpager.currentItem == 0) presenter.closeAboutActivity() else viewpager.currentItem = viewpager.currentItem - 1
    }

    inner class AboutAdapter : PagerAdapter() {
        private val pages = arrayOf<AboutItem>(item_0, item_1, item_2, item_3)

        override fun getCount() = pages.size
        override fun isViewFromObject(v: View, o: Any) = v == o
        override fun instantiateItem(container: ViewGroup, pos: Int) = pages[pos]
        override fun destroyItem(container: ViewGroup, pos: Int, obj: Any) = container.removeView(obj as View)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setError(e: ApiException) {}
}
