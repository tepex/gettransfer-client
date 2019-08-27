package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.os.Bundle

import androidx.annotation.CallSuper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.core.content.ContextCompat

import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.adapter.LanguagesListAdapter
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.presenter.SelectLanguagePresenter
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.SelectLanguageView
import kotlinx.android.synthetic.main.fragment_select_language.*
import kotlinx.android.synthetic.main.layout_select_language.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

open class SelectLanguageFragment : BaseBottomSheetFragment(), SelectLanguageView {

    private lateinit var adapterAll: LanguagesListAdapter
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

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)

        @Suppress("UnsafeCallOnNullableType")
        val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(), R.drawable.sh_divider_light_gray)?.let { drawable ->
            itemDecorator.setDrawable(drawable)
        }

        adapterAll = LanguagesListAdapter(presenter::changeLanguage)
        rvAllLanguages.adapter = adapterAll
        rvAllLanguages.itemAnimator = DefaultItemAnimator()
        rvAllLanguages.addItemDecoration(itemDecorator)
        setupToolbar()
    }

    private fun setupToolbar() {
        toolBar.toolbar.ivBack.setThrottledClickListener { findNavController().navigateUp() }
        toolBar.toolbar.toolbar_title.text = getString(R.string.LNG_LANGUAGES)
    }

    /**
     * Update UI after finished start fragment
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {
            adapterAll.notifyDataSetChanged()
        }
    }

    override fun setLanguages(all: List<LocaleModel>, selected: LocaleModel) {
        adapterAll.setNewSelectedLanguage(selected)
        adapterAll.update(all)
    }

    override fun recreateActivity() = requireActivity().recreate()

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
