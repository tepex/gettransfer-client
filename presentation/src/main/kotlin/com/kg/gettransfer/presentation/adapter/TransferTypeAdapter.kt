package com.kg.gettransfer.presentation.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TransportTypeModel
import kotlinx.android.extensions.LayoutContainer
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
            setOnClickListener {
                item.checked = !item.checked
                cbTransferType.isChecked = item.checked
                if (item.checked) ivTransportInfo.visibility = View.VISIBLE else ivTransportInfo.visibility = View.INVISIBLE
                item.showInfo = ivTransportInfo.visibility == View.VISIBLE
            }
            ivTransportInfo.setOnClickListener {
                if (item.checked) {
                    listener(item)
                }
            }
        }
    }
}

typealias ChangeListener = (TransportTypeModel) -> Unit
