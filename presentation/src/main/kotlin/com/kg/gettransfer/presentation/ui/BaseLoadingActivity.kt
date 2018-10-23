package com.kg.gettransfer.presentation.ui

import android.support.v4.app.Fragment

import com.kg.gettransfer.presentation.presenter.BaseLoadingPresenter
import com.kg.gettransfer.presentation.view.BaseLoadingView

abstract class BaseLoadingActivity: BaseActivity(), BaseLoadingView {

    private var loadingContainerId = android.R.id.content
    private var loadingFragment: Fragment = LoadingFragment()

    abstract override fun getPresenter(): BaseLoadingPresenter<*>

    override fun showLoading() {
        if (!loadingFragment.isAdded) {
            supportFragmentManager
                    .beginTransaction()
                    .add(loadingContainerId, loadingFragment)
                    .commit()
        }
    }

    override fun hideLoading() {
        supportFragmentManager
                .beginTransaction()
                .remove(loadingFragment)
                .commit()
    }
}
