package com.kg.gettransfer.presentation.ui.newtransfer

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.common.NavigationMenuListener
import com.kg.gettransfer.common.NewTransferSwitchListener
import com.kg.gettransfer.presentation.view.BaseNetworkWarning
import leakcanary.AppWatcher

import org.koin.core.KoinComponent
import timber.log.Timber

@Suppress("TooManyFunctions")
class NewTransferFragment : Fragment(), KoinComponent, BaseNetworkWarning, NewTransferSwitchListener {

    private val newTransferFragment by lazy { NewTransferMapFragment() }

    var listener: NavigationMenuListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_new_transfer, container, false)

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            listener = activity as NavigationMenuListener
        } catch (e: ClassCastException) {
            Timber.e("%s must implement NavigationMenuListener", activity.toString())
        }
    }

    override fun openMenu() {
        listener?.openMenu()
    }

    override fun switchToMap() {
        listener?.disablingNavigation()
        //Adding or showing new transfer fragment with map
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        if (childFragmentManager.findFragmentByTag(MAP_FRG_TAG) == null) {
            ft.replace(R.id.map, newTransferFragment, MAP_FRG_TAG)
        } else {
            ft.show(newTransferFragment)
            newTransferFragment.userVisibleHint = true
        }
        ft.commitAllowingStateLoss()
        //Don't update after show other fragment
        childFragmentManager.fragments.find { it is NewTransferMainFragment }?.let {
            (it as NewTransferMainFragment).userVisibleHint = false
        }
    }

    override fun switchToMain() {
        listener?.enablingNavigation()

        //Hiding new transfer fragment with map
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        childFragmentManager.findFragmentByTag(MAP_FRG_TAG).let {
            ft.hide(newTransferFragment)
            newTransferFragment.userVisibleHint = false
        }
        ft.commitAllowingStateLoss()
        //Can update after hide other fragment
        childFragmentManager.fragments.find { it is NewTransferMainFragment }?.let {
            (it as NewTransferMainFragment).userVisibleHint = true
        }
    }

    override fun onNetworkWarning(available: Boolean) {
        childFragmentManager.fragments.filter { it is BaseNetworkWarning }.let {
            it.forEach { fragment -> (fragment as BaseNetworkWarning).onNetworkWarning(available) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        const val MAP_FRG_TAG = "map"
    }
}
