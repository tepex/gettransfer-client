package com.kg.gettransfer.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_request_item.view.*

class RequestsRVAdapter(
    private val requestType: Int,
    private val onItemClick: ItemClickListener,
    private val onCallClick: BtnCallClickListener,
    private val onChatClick: BtnChatClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val transfers = mutableListOf<TransferModel>()
    private var eventsCount = mapOf<Long, Int>()
    private var transfersWithDriverCoordinates = mutableSetOf<Long>()

    private var isLoading = false

    override fun getItemCount() = transfers.size

    fun isEmptyList() = transfers.size == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (ViewType.values()[viewType]) {
            ViewType.LOADING -> ProgressHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_loading, parent, false))
            else -> RequestsHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.view_transfer_request_item, parent, false))
        }

    override fun getItemViewType(position: Int): Int =
        if (isLoading && position == transfers.size - 1) ViewType.LOADING.ordinal else ViewType.NORMAL.ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ViewType.NORMAL.ordinal) {
            val transfer = transfers[position]
            if (holder is RequestsHolder) {
                holder.bind(
                    transfer,
                    requestType,
                    eventsCount[transfer.id] ?: 0,
                    transfersWithDriverCoordinates.contains(transfer.id),
                    onItemClick,
                    onCallClick,
                    onChatClick
                )
            }
        }
    }

    class RequestsHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var firstInit = true

        @Suppress("LongParameterList")
        fun bind(
            item: TransferModel,
            requestType: Int,
            eventsCount: Int,
            haveDriverCoordinates: Boolean,
            onItemClick: ItemClickListener,
            onCallClick: BtnCallClickListener,
            onChatClick: BtnChatClickListener
        ) = with(containerView) {
            with(requestInfo) {
                if (firstInit) {
                    setStyle(requestType)
                    firstInit = false
                }
                containerView.isEnabled = true
                setInfo(item, requestType)
                showOfferInfo(
                    item.matchedOffer,
                    haveDriverCoordinates,
                    onCallClick,
                    View.OnClickListener { onChatClick(item.id) },
                    View.OnClickListener { onItemClick(item) }
                )
                showEvents(item, item.matchedOffer != null, eventsCount)
            }
            setOnClickListener {
                containerView.isEnabled = false
                onItemClick(item)
            }
        }
    }

    class ProgressHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateTransfers(transfers: List<TransferModel>) {
        /*val start = transfers.size
        transfers.addAll(tr)
        notifyItemRangeInserted(start, transfers.size)*/
        val diffCallback = RequestsDiffCallback(this.transfers, transfers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.transfers.clear()
        this.transfers.addAll(transfers)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeAll() {
        val count = transfers.size
        transfers.clear()
        notifyItemRangeRemoved(0, count)
    }

    fun updateEvents(eventsCount: Map<Long, Int>) {
        this.eventsCount = eventsCount
        notifyItemRangeChanged(0, transfers.size)
    }

    fun updateDriverCoordinates(transferId: Long) {
        if (!transfersWithDriverCoordinates.contains(transferId)) {
            transfersWithDriverCoordinates.add(transferId)
            notifyItemRangeChanged(0, transfers.size)
        }
    }

    fun addLoading() {
        isLoading = true
    }

    fun removeLoading() {
        if (isLoading) {
            isLoading = false
        }
    }

    enum class ViewType {
        NORMAL, LOADING
    }
}

class RequestsDiffCallback(
    private val oldList: List<TransferModel>,
    private val newList: List<TransferModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}

typealias ItemClickListener = (TransferModel) -> Unit
typealias BtnCallClickListener = (String) -> Unit
typealias BtnChatClickListener = (Long) -> Unit
