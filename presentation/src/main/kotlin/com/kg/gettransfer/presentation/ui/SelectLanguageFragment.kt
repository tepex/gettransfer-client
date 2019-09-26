package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper
import com.google.android.material.bottomsheet.BottomSheetBehavior

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.adapter.LanguagesListAdapter
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.presenter.SelectLanguagePresenter
import com.kg.gettransfer.presentation.view.SelectLanguageView
import kotlinx.android.synthetic.main.fragment_select_language.*
import kotlinx.android.synthetic.main.layout_select_language.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

open class SelectLanguageFragment : BaseBottomSheetFragment(), SelectLanguageView {

    override val layout = R.layout.fragment_select_language

    @InjectPresenter
    internal lateinit var presenter: SelectLanguagePresenter

    @ProvidePresenter
    fun createSelectLanguagePresenter() = SelectLanguagePresenter()

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolBar.toolbar.ivBack.setThrottledClickListener { findNavController().navigateUp() }
        toolBar.toolbar.toolbar_title.text = getString(R.string.LNG_LANGUAGES)
    }

    override fun setLanguages(all: List<LocaleModel>, selected: LocaleModel) {
        rvAllLanguages.adapter = LanguagesListAdapter(presenter::changeLanguage, all, selected)
    }

    override fun recreateActivity() {
        findNavController().navigateUp()
        requireActivity().recreate()
    }

    override fun showLanguageFragment() {
        view?.let { setBottomSheetState(it, BottomSheetBehavior.STATE_EXPANDED) }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {}

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {}

    override fun setError(e: ApiException) {}

    override fun setError(e: DatabaseException) {}

    override fun setTransferNotFoundError(transferId: Long) {}

    @CallSuper
    override fun onDestroyView() {
        rvAllLanguages.adapter = null
        super.onDestroyView()
    }
}
