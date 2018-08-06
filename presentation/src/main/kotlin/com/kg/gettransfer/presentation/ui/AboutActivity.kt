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
		viewpager.setAdapter(AboutAdapter(this))
	}
	
	/*
	@CallSuper
	override fun onBackPressed() {
		if(viewpager.getCurrentItem() == 0) super.onBackPressed()
		else viewpager.setCurrentItem(viewpager.currentItem - 1)
	}
	*/
	
	class AboutAdapter(val activity: AboutActivity): PagerAdapter() {
		override fun getCount(): Int = 3
		override fun isViewFromObject(v: View, o: Any): Boolean = (v == o)
		override fun instantiateItem(container: ViewGroup, position: Int): Any {
			Timber.d("position : $position")
			
			val inflater = LayoutInflater.from(activity)
			when(position) {
				0 -> {
					//return inflater.inflate(R.layout.about_item1, container, false).findViewById(R.id.about_item1)
					return activity.findViewById(R.id.about_item1)
				}
				1 -> return activity.findViewById(R.id.about_item2)
				2 -> return activity.findViewById(R.id.about_item3)
			}
			throw RuntimeException("View pager error index: $position")
		}
		/*
		override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
			container.removeView(obj as View)
		}
		*/
	}
}
