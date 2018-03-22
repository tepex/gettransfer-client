package com.kg.gettransfer.views


import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.activity.transfer.TransferActivity
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransfersAdapter(
        realmResults: RealmResults<Transfer>,
        autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<Transfer, TransfersAdapter.ViewHolder>(realmResults, autoUpdate) {

    private val onItemClickListener: View.OnClickListener = View.OnClickListener {
        Log.i("TransfersAdapter", "Starting transfer activity")

        val context = it.context

        val intent = Intent(context, TransferActivity::class.java)
        intent.putExtra("id", it.getTag(R.id.key_id) as Int)

        startActivity(context, intent, null)
    }

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val from: TextView = container.findViewById(R.id.tvFrom)
        val to: TextView = container.findViewById(R.id.tvTo)
        val date: TextView = container.findViewById(R.id.tvDateTime)
        val state: TransferStatusView = container.findViewById(R.id.tvState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_transfer, parent, false)
        v.setOnClickListener(onItemClickListener)
        return ViewHolder(v as ConstraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transfer = getItem(position) ?: return

        holder.from.text = transfer.from?.name

        if (transfer.to == null) holder.to.text = transfer.hireDurationString
        else holder.to.text = transfer.to?.name

        holder.date.text = Utils.dateToShortString(transfer.dateTo)

        holder.state.update(transfer, true)

        holder.itemView.setTag(R.id.key_id, transfer.id)
    }
}
