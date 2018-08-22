package com.kg.gettransfer.presentation.ui

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.TransferType
import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.address_list_item.*
import kotlinx.android.synthetic.main.view_transfer_type.view.*

class TransferTypeAdapter(private val presenter: CreateOrderPresenter,
                     private var list: List<TransferType>): RecyclerView.Adapter<TransferTypeAdapter.ViewHolder>() {
    companion object {
        var selectedPos = RecyclerView.NO_POSITION
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_type, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        holder.bind(list.get(pos))
    }

    fun setSelectedItem(address: GTAddress) {

    }

    class ViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(item: TransferType) = with(containerView) {
            tvTransferType.text = item.transferName.substring(0, 1).toUpperCase() + item.transferName.substring(1)
            tvNumberPersonsTransfer.text = " X ${item.countPersons + 1}"
            tvCountBaggage.text = " X ${item.countBaggage + 1}"
            priceFrom.text = "from $${item.priceFrom + 1}"
            when(item.transferName){
                "economy" -> ivTransferType.setImageResource(R.drawable.economy)
                "business" -> ivTransferType.setImageResource(R.drawable.business)
                "premium" -> ivTransferType.setImageResource(R.drawable.premium)
                "van" -> ivTransferType.setImageResource(R.drawable.van)
                "minibus" -> ivTransferType.setImageResource(R.drawable.minibus)
                "bus" -> ivTransferType.setImageResource(R.drawable.bus)
                "limousine" -> ivTransferType.setImageResource(R.drawable.limousine)
                "helicopter" -> ivTransferType.setImageResource(R.drawable.helicopter)
            }
            //setSelected(selectedPos == adapterPosition)
            setOnClickListener {
                cbTransferType.isChecked = !cbTransferType.isChecked
                item.isChecked = cbTransferType.isChecked
            }
        }
    }
}
