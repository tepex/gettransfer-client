package com.kg.gettransfer.presentation.ui.newtransfer

import android.app.Activity
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.common.NavigationMenuClickListener
import com.kg.gettransfer.common.NewTransferSwitchListener

import org.koin.core.KoinComponent
import timber.log.Timber


@Suppress("TooManyFunctions")
class NewTransferFragment : Fragment(), KoinComponent, NewTransferSwitchListener {

    private val newTransferFragment by lazy { NewTransferMapFragment() }

    var listener: NavigationMenuClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_new_transfer, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            listener = activity as NavigationMenuClickListener
        } catch (e: ClassCastException) {
            Timber.e("%s must implement NavigationMenuClickListener", activity.toString())
        }
    }

    override fun openNewTransfer() {
        listener?.openNewTransfer()
    }

    override fun switchToMap() {
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        if (childFragmentManager.findFragmentByTag(MAP_FRG_TAG) == null) {
            ft.replace(R.id.map, newTransferFragment, MAP_FRG_TAG)
        } else {
            ft.show(newTransferFragment)
            newTransferFragment.userVisibleHint = true
        }
        ft.commitAllowingStateLoss()
        //Don't update after hide other fragment
        childFragmentManager.fragments.find { it is NewTransferMainFragment }?.let {
            (it as NewTransferMainFragment).userVisibleHint = false
        }
    }

    override fun switchToMain() {
        val ft = childFragmentManager.beginTransaction()
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        childFragmentManager.findFragmentByTag(MAP_FRG_TAG).let {
            ft.hide(newTransferFragment)
            newTransferFragment.userVisibleHint = false
        }
        ft.commitAllowingStateLoss()
        //Don't update after hide other fragment
        childFragmentManager.fragments.find { it is NewTransferMainFragment }?.let {
            (it as NewTransferMainFragment).userVisibleHint = true
        }
    }


    /*
    override fun onNetworkWarning(disconnected: Boolean) {
        tv_internet_warning.isVisible = disconnected
    }
*/

    companion object {
        const val MAP_FRG_TAG = "map"
    }
}
