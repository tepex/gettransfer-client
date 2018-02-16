package com.kg.gettransfer.views


import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.kg.gettransfer.R
import com.kg.gettransfer.realm.Transfer
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

    inner class ViewHolder(container: ConstraintLayout) : RecyclerView.ViewHolder(container) {
        val from: TextView = container.findViewById(R.id.tvFrom)
        val to: TextView = container.findViewById(R.id.tvName)
        val date: TextView = container.findViewById(R.id.tvLuggage)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_transfer, parent, false)
        return ViewHolder(v as ConstraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = getItem(position) ?: return

        holder?.from?.text = item.from?.name
        holder?.to?.text = item.to?.name
        holder?.date?.text = item.dateTo

        holder?.itemView?.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TransferActivity::class.java)
            intent.putExtra("id", item.id)
            startActivity(context, intent, null)
        }
    }
}
