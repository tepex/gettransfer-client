package com.kg.gettransfer.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.map
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                VIEW_TYPE_LOADING -> ProgressHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
                else -> RequestsHolder(LayoutInflater.from(parent.context).inflate(R.layout.view_transfer_request_item, parent, false))
            }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) {
            if (position == transfers.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAl

        } else VIEW_TYPE_NORMAl
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_NORMAl) {
            val transfer = transfers[position]
            (holder as RequestsHolder).bind(
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

    class RequestsHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var firstInit = true

        fun bind(item: TransferModel,
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
            setThrottledClickListener { onItemClick(item) }
        }
    }

    class ProgressHolder(override val containerView: View) :
            RecyclerView.ViewHolder(containerView), LayoutContainer

    fun updateTransfers(tr: List<TransferModel>) {
        val start = transfers.size
        transfers.addAll(tr)
        notifyItemRangeInserted(start, transfers.size)
    }

    fun removeAll() {
        transfers.clear()
        notifyDataSetChanged()
    }

    fun updateEvents(eventsCount: Map<Long, Int>) {
        this.eventsCount = eventsCount
        notifyDataSetChanged()
    }

    fun updateDriverCoordinates(transferId: Long) {
        if (!transfersWithDriverCoordinates.contains(transferId)) {
            transfersWithDriverCoordinates.add(transferId)
            notifyDataSetChanged()
        }
    }

    fun addLoading() {
        isLoading = true
        transfers.add(Transfer.EMPTY.map(listOf(TransportType.DEFAULT_TRANSPORT_TYPE.map())))
        notifyItemInserted(transfers.size - 1)
    }

    fun removeLoading() {
        if (isLoading) {
            isLoading = false
            val position = transfers.size - 1
            transfers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAl = 1
    }
}

typealias ItemClickListener = (TransferModel) -> Unit
typealias BtnCallClickListener = (String) -> Unit
typealias BtnChatClickListener = (Long) -> Unit
