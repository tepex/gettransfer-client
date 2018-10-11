package com.kg.gettransfer.presentation.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TransportTypeModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_type.*
import kotlinx.android.synthetic.main.view_transfer_type.view.*

class TransferTypeAdapter(private var list: List<TransportTypeModel>,
                          private val listener: ChangeListener): RecyclerView.Adapter<TransferTypeAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) { holder.bind(list.get(pos), listener) }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: TransportTypeModel, listener: ChangeListener) = with(containerView) {
            tvTransferType.setText(item.nameId!!)
            tvNumberPersonsTransfer.text = context.getString(R.string.count_persons_and_baggage, item.paxMax)
            tvCountBaggage.text = context.getString(R.string.count_persons_and_baggage, item.luggageMax)
            if (item.price == null) tvPriceFrom.visibility = View.GONE
            else tvPriceFrom.text = context.getString(R.string.price_from, item.price)

            ivTransferType.setImageResource(item.imageId!!)
            cbTransferType.isChecked = item.checked
            setVisibilityIconInfo(item)
            setOnClickListener {
                item.checked = !item.checked
                cbTransferType.isChecked = item.checked
                setVisibilityIconInfo(item)
                item.showInfo = ivTransportInfo.visibility == View.VISIBLE
                if (item.checked) {
                    showItemShadowAndCorners(context)
                } else {
                    hideItemShadowAndCorners(context)
                }
            }
            layoutTransportInfo.setOnClickListener {
                if (item.checked) {
                    listener(item)
                }
            }
        }

        private fun setVisibilityIconInfo(item: TransportTypeModel) {
            if (item.checked) ivTransportInfo.visibility = View.VISIBLE else ivTransportInfo.visibility = View.INVISIBLE
        }

        private fun hideItemShadowAndCorners(context: Context) {
            cardTransferType.cardElevation = context.resources.getDimension(R.dimen.card_transfer_type_elevation_default)
            cardTransferType.radius = context.resources.getDimension(R.dimen.card_transfer_type_corner_radius_default)
        }

        private fun showItemShadowAndCorners(context: Context) {
            val elevation = TypedValue()
            val radius = TypedValue()
            context.resources.getValue(R.dimen.card_transfer_type_elevation, elevation, true)
            context.resources.getValue(R.dimen.card_transfer_type_corner_radius, radius, true)

            cardTransferType.cardElevation = elevation.float
            cardTransferType.radius = radius.float
        }
    }
}

typealias ChangeListener = (TransportTypeModel) -> Unit
