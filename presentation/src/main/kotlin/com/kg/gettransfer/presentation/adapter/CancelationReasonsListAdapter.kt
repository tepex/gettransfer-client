package com.kg.gettransfer.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setThrottledClickListener
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_cancelation_reason_item.view.*

class CancelationReasonsListAdapter(
    private val reasons: List<Int>,
    private val listener: SelectReasonListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount() = reasons.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        if (holder is ViewHolderCurrency) {
            holder.bind(reasons[pos], listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolderCurrency(
            LayoutInflater.from(parent.context).inflate(R.layout.view_cancelation_reason_item, parent, false)
        )

    class ViewHolderCurrency(override val containerView: View) :
        RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bind(reason: Int, listener: SelectReasonListener) = with(containerView) {
            context.getString(reason).let { reasonText ->
                reasonName.text = reasonText
                setThrottledClickListener { listener(reasonText) }
            }
        }
    }
}

typealias SelectReasonListener = (String) -> Unit
