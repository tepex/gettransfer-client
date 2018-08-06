package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager

import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.AboutPresenter
import com.kg.gettransfer.presentation.view.AboutView

import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.about_item1.*
import kotlinx.android.synthetic.main.about_item2.*
import kotlinx.android.synthetic.main.about_item3.*

import timber.log.Timber

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
		
		/*
		setSupportActionBar(toolbar_gray as Toolbar)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(false)
		supportActionBar?.setDisplayShowHomeEnabled(false)
		*/
		
		viewpager.setOffscreenPageLimit(2)
		viewpager.setAdapter(AboutAdapter())
	}
	
	/*
	@CallSuper
	override fun onBackPressed() {
		if(viewpager.getCurrentItem() == 0) super.onBackPressed()
		else viewpager.setCurrentItem(viewpager.currentItem - 1)
	}
	*/
	
	inner class AboutAdapter: PagerAdapter() {
		private val pages = arrayOf<View>(about_item1, about_item2, about_item3)
		
		override fun getCount(): Int = pages.size
		override fun isViewFromObject(v: View, o: Any): Boolean = (v == o)
		override fun instantiateItem(container: ViewGroup, pos: Int): Any = pages[pos]
	}
}
