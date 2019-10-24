package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.presenter.AboutPresenter
import com.kg.gettransfer.presentation.view.AboutView

import kotlinx.android.synthetic.main.activity_about.*

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

        presenter.openMain = intent.getBooleanExtra(AboutView.EXTRA_OPEN_MAIN, true)

        val adapter = AboutAdapter()
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = adapter.count - 1

        pageIndicator.count = COUNT_PAGE
        pageIndicator.setSelected(DEFAULT_PAGE)

        btnClose.setThrottledClickListener {
            presenter.closeAboutActivity()
            if (viewpager.currentItem == viewpager.childCount - 1) {
                presenter.logExitStep(0)
            } else {
                presenter.logExitStep(viewpager.currentItem + 1)
            }
        }
        btnNext.setOnClickListener {
            if (viewpager.currentItem == viewpager.childCount - 1) {
                btnNext.isEnabled = false
                presenter.closeAboutActivity()
                presenter.logExitStep(0)
            } else {
                viewpager.currentItem = viewpager.currentItem + 1
            }
        }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageSelected(p0: Int) {
                pageIndicator.setSelected(p0)
                if (p0 == viewpager.childCount - 1) {
                    btnNext.text = getString(R.string.LNG_OK)
                } else {
                    btnNext.text = getString(R.string.LNG_NEXT)
                }
            }
        })
    }

    override fun onBackPressed() {
        if (viewpager.currentItem == 0) {
            presenter.closeAboutActivity()
        } else {
            viewpager.currentItem = viewpager.currentItem - 1
        }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}
    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}
    override fun setError(e: ApiException) {}
    override fun setError(e: DatabaseException) {}

    companion object {
        private const val COUNT_PAGE = 2
        private const val DEFAULT_PAGE = 1
    }

    inner class AboutAdapter : PagerAdapter() {
        private val pages = arrayOf<AboutItem>(item_0, item_1)

        override fun getCount() = pages.size
        override fun isViewFromObject(v: View, o: Any) = v == o
        override fun instantiateItem(container: ViewGroup, pos: Int) = pages[pos]
        override fun destroyItem(container: ViewGroup, pos: Int, obj: Any) {
            if (obj is View) {
                container.removeView(obj)
            }
        }
    }
}
