package com.kg.gettransfer.presentation.adapter

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_language_item.view.*

class LanguagesListAdapter(
    private val listener: SelectLanguageListener,
    private val languages: List<LocaleModel>,
    private val selectedLanguage: LocaleModel
) : RecyclerView.Adapter<LanguagesListAdapter.ViewHolderLanguage>() {

    override fun getItemCount() = languages.size

    override fun onBindViewHolder(holder: ViewHolderLanguage, pos: Int) =
        holder.bind(languages[pos], languages[pos].locale == selectedLanguage.locale, listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderLanguage(LayoutInflater.from(parent.context).inflate(R.layout.view_language_item, parent, false))

    class ViewHolderLanguage(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(language: LocaleModel, isSelected: Boolean, listener: SelectLanguageListener) = with(containerView) {
            languageFlag.setImageResource(Utils.getLanguageImage(language.locale.toLowerCase()))
            languageName.text = language.name
            imgSelected.isVisible = isSelected
            setThrottledClickListener { listener(language) }
        }
    }
}

typealias SelectLanguageListener = (LocaleModel) -> Unit
