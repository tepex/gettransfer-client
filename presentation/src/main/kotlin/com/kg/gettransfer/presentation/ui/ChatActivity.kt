package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.adapter.ChatAdapter
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.ChatPresenter
import com.kg.gettransfer.presentation.view.ChatView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar.view.*

class ChatActivity : BaseActivity(), ChatView {
    @InjectPresenter
    internal lateinit var presenter: ChatPresenter

    @ProvidePresenter
    fun createChatPresenter() = ChatPresenter()

    override fun getPresenter(): ChatPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(ChatView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_chat)

        rvMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btnSend.setOnClickListener {
            if(messageText.text.isNotEmpty()) {
                presenter.onSentClick(messageText.text.toString())
                messageText.setText("")
            }
        }
        /*rvMessages.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                (rvMessages.layoutManager as LinearLayoutManager).let { presenter.readMessages(it.findFirstVisibleItemPosition(), it.findLastVisibleItemPosition()) }
            }
        })*/
    }

    override fun initToolbar(transfer: TransferModel?, offer: OfferModel?, name: String) {
        (toolbar as Toolbar).apply {
            layoutChatTitle.isVisible = true
            transfer?.let {
                if(transfer.from.isNotEmpty()){
                    transferFromOrNameText.text = it.from
                    transferStartDateText.text = SystemUtils.formatDateTimeWithShortMonth(it.dateTime)
                    transferStartDateText.isVisible = true
                } else {
                    transferFromOrNameText.text = name
                    transferFromOrNameText.textSize = 17f
                }
            }
            chatTitleButtonBack.setOnClickListener { presenter.onBackCommandClick() }
            offer?.phoneToCall?.let { phone ->
                titleBtnCall.isVisible = true
                titleBtnCall.setOnClickListener { presenter.callPhone(phone) }
            }
        }
    }

    override fun setChat(chat: ChatModel, withScrollDown: Boolean) {
        val oldMessagesSize = rvMessages.adapter?.itemCount
        rvMessages.apply {
            if(adapter == null){
                adapter = ChatAdapter(chat) { presenter.readMessage(it) }
            } else {
                (adapter as ChatAdapter).changeModel(chat)
                rvMessages.adapter?.notifyDataSetChanged()
            }
        }
        if (withScrollDown || (oldMessagesSize ?: 0 < chat.messages.size && chat.messages.lastOrNull()!!.accountId != chat.currentAccountId)) scrollToEnd()
    }

    override fun scrollToEnd() {
        runOnUiThread { rvMessages.adapter?.let { rvMessages.scrollToPosition(it.itemCount - 1)  } }
    }

    override fun notifyData() {
        runOnUiThread { rvMessages.adapter?.apply { notifyDataSetChanged() } }
    }
}