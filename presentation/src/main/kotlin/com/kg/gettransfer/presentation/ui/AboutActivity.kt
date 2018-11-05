package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.view.PagerAdapter
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.AboutPresenter
import com.kg.gettransfer.presentation.view.AboutView
import kotlinx.android.synthetic.main.about_item1.*
import kotlinx.android.synthetic.main.about_item2.*
import kotlinx.android.synthetic.main.about_item3.*
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity: MvpAppCompatActivity(), AboutView {
	@InjectPresenter
	internal lateinit var presenter: AboutPresenter
	
	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		setContentView(R.layout.activity_about)
		
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setDisplayShowHomeEnabled(true)
		(toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
		
		val adapter = AboutAdapter()
		viewpager.setAdapter(adapter)
		viewpager.setOffscreenPageLimit(adapter.count-1)
		indicator.setViewPager(viewpager)
	}
	
	override fun onBackPressed() {
		if(viewpager.getCurrentItem() == 0) presenter.onBackCommandClick()
		else viewpager.setCurrentItem(viewpager.currentItem - 1)
	}
	
	inner class AboutAdapter: PagerAdapter() {
		private val pages = arrayOf<View>(about_item1, about_item2, about_item3)
		
		override fun getCount(): Int = pages.size
		override fun isViewFromObject(v: View, o: Any): Boolean = (v == o)
		override fun instantiateItem(container: ViewGroup, pos: Int): Any = pages[pos]
		override fun destroyItem(container: ViewGroup, pos: Int, obj: Any) {
			container.removeView(obj as View)
		}
	}
}
