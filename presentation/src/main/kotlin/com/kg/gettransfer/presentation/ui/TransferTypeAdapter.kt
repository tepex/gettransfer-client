package com.kg.gettransfer.presentation.ui

import android.support.v7.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.model.TransportTypeModel

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.view_transfer_type.view.*

class TransferTypeAdapter(private var list: List<TransportTypeModel>): 
    RecyclerView.Adapter<TransferTypeAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(list.get(pos))
    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: TransportTypeModel) = with(containerView) {
            tvTransferType.setText(item.nameId)
            tvNumberPersonsTransfer.text = " X ${item.delegate.paxMax + 1}"
            tvCountBaggage.text = " X ${item.delegate.luggageMax + 1}"
            priceFrom.text = "from UUU"
            ivTransferType.setImageResource(item.imageId)
            cbTransferType.isChecked = item.checked
            setOnClickListener {
                item.checked = !item.checked
                cbTransferType.isChecked = item.checked
            }
        }
    }
}
