package com.kg.gettransfer.presentation.adapter

import android.content.Context

import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.ui.Utils

import kotlinx.android.extensions.LayoutContainer

import kotlinx.android.synthetic.main.view_transfer_type.*
import kotlinx.android.synthetic.main.view_transfer_type.view.*

class TransferTypeAdapter(
    private val listener: ChangeListener
) : RecyclerView.Adapter<TransferTypeAdapter.ViewHolder>() {

    var list: ArrayList<TransportTypeModel> = arrayListOf()

    override fun getItemCount() = list.size

    fun update(list: List<TransportTypeModel>){
        this.list.clear()
        this.list.addAll(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) { holder.bind(list.get(pos), listener) }

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: TransportTypeModel, listener: ChangeListener) = with(containerView) {
            tvTransferType.setText(item.nameId)
            tvNumberPersonsTransfer.text = Utils.formatPersons(context, item.paxMax)
            tvCountBaggage.text          = Utils.formatLuggage(context, item.luggageMax)

            if (item.price == null) rl_price.isVisible = false
            item.price?.let {
                from.textSize = getTextSize(it.min).toFloat()
                tvPriceFrom.text = item.price.min
            }

            ivTransferType.setImageResource(item.imageId)
            cbTransferType.isChecked = item.checked
            setVisibilityShadow(context, item)
            setOnClickListener {
                item.checked = !item.checked
                cbTransferType.isChecked = item.checked
                setVisibilityShadow(context, item)
                listener(item, false)
            }
            layoutTransportInfo.setOnClickListener { listener(item, true) }
        }

        private fun setVisibilityShadow(context: Context, item: TransportTypeModel) {
            if(item.checked) showItemShadow(context) else hideItemShadow(context)
        }

        private fun hideItemShadow(context: Context) {
            cardTransferType.cardElevation = context.resources.getDimension(R.dimen.card_transfer_type_elevation_default)
        }

        private fun showItemShadow(context: Context) {
            cardTransferType.cardElevation = context.resources.getDimension(R.dimen.card_transfer_type_elevation)
        }

        private fun getTextSize(text: String) =    //change title 'from:' text size depending on price characters count
                when (text.length) {
                    in 0..13 -> 12
                    in 14..15 -> 10
                    else -> 8
                }
    }

}

typealias ChangeListener = (TransportTypeModel, Boolean) -> Unit
