package com.kg.gettransfer.presentation.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView

import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.ui.TransferRequestItem

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_info_enabled.*

class RequestsRVAdapter(
    private val transfers: List<TransferModel>,
    @LayoutRes private val layout: Int,
    private val listener: ItemClickListener
) : RecyclerView.Adapter<RequestsRVAdapter.ViewHolder>() {

    override fun getItemCount() = transfers.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RequestsRVAdapter.ViewHolder(
                    TransferRequestItem(parent.context, layout).containerView
            )

    override fun onBindViewHolder(holder: RequestsRVAdapter.ViewHolder, pos: Int) =
        holder.bind(transfers[pos], listener)

    class ViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(item: TransferModel, listener: ItemClickListener) = with(containerView) {
            (this as TransferRequestItem).setInfo(item)
            showEvents(item)
            setOnClickListener { listener(item) }
        }

        private fun showEvents(item: TransferModel) {
            if (item.eventsCount == 0 || !item.showOfferInfo) tvEventsCount.isVisible = false
            else {
                tvEventsCount.isVisible = true
                tvEventsCount.text = item.eventsCount.toString()
            }
        }
    }
}

typealias ItemClickListener = (TransferModel) -> Unit
