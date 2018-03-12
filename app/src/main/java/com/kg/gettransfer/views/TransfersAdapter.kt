package com.kg.gettransfer.views


import android.annotation.SuppressLint
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
import com.kg.gettransfer.realm.Transfer
import com.kg.gettransfer.realm.Utils
import com.kg.gettransfer.transfer.TransferActivity
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults


/**
 * Created by denisvakulenko on 29/01/2018.
 */


class TransfersAdapter(
        realmResults: RealmResults<Transfer>,
        autoUpdate: Boolean)
    : RealmRecyclerViewAdapter<Transfer, TransfersAdapter.ViewHolder>(realmResults, autoUpdate) {

    private val icl: View.OnClickListener = View.OnClickListener {
        Log.i("TransfersAdapter", "Starting activity")
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
        v.setOnClickListener(icl)
        return ViewHolder(v as ConstraintLayout)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return

        val context = holder.itemView.context

        holder.from.text = item.from?.name
        holder.to.text = item.to?.name
        holder.date.text = Utils.dateToString(context, item.dateTo)
        holder.state.update(item, true)

        holder.itemView.setTag(R.id.key_id, item.id)
    }
}
