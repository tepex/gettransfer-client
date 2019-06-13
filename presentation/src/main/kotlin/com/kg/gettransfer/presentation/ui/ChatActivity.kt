package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.adapter.ChatAdapter
import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.ChatPresenter
import com.kg.gettransfer.presentation.view.ChatView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar.view.*
import java.util.Date

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
        presenter.tripId = intent.getLongExtra(ChatView.EXTRA_TRIP_ID, 0)

        setContentView(R.layout.activity_chat)

        rvMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btnSend.setOnClickListener {
            if(messageText.text.isNotEmpty()) {
                presenter.onSentClick(messageText.text.toString())
                messageText.setText("")
            }
        }
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        presenter.onLeaveRoom()
    }

    override fun setToolbar(transfer: TransferModel, offer: OfferModel?, isShowChevron: Boolean) {
        transfer.let {
            initToolbar(offer?.driver?.name ?: it.nameSign ?: offer?.carrier?.profile?.name, offer?.phoneToCall)
            if (it.id != ChatPresenter.NO_ID) {
                initTransferInfoLayout(it.from, it.dateTime, it.id, isShowChevron)
            }
        }
    }

    override fun setToolbar(carrierTrip: CarrierTripModel) {
        val userProfile = carrierTrip.passenger?.profile
        initToolbar(userProfile?.name, userProfile?.phone)
        carrierTrip.base.let {
            if (it.id != ChatPresenter.NO_ID) {
                initTransferInfoLayout(it.from, it.dateLocal, it.transferId, true)
            }
        }
    }

    private fun initToolbar(userName: String?, userPhone: String?) {
        (toolbar as Toolbar).apply {
            layoutChatTitle.isVisible = true
            chatTitleButtonBack.setOnClickListener { presenter.onBackCommandClick() }
            userName?.let { textDriverName.text = getString(R.string.LNG_CHAT_WITH).plus(" ").plus(it) }
            userPhone?.let { phone ->
                titleBtnCall.isVisible = true
                titleBtnCall.setOnClickListener { presenter.callPhone(phone) }
            }
        }
    }

    private fun initTransferInfoLayout(from: String, date: Date, transferId: Long, isShowChevron: Boolean) {
        //imgChevron.isVisible = isShowChevron
        layoutTransferInfo.apply {
            isVisible = true
            textTransferInfoFrom.text = from
            textTransferInfoDate.text = getString(R.string.chat_transfer_date_transfer_id_format,
                    SystemUtils.formatDateTime(date),
                    getString(R.string.LNG_TRANSFER).plus(" №$transferId"))
            //setOnClickListener { presenter.onTransferInfoClick() }
        }
    }

    override fun setChat(chat: ChatModel) {
        val oldMessagesSize = rvMessages.adapter?.itemCount
        rvMessages.apply {
            if(adapter == null){
                adapter = ChatAdapter(chat) { presenter.readMessage(it) }
            } else {
                (adapter as ChatAdapter).changeModel(chat)
                rvMessages.adapter?.notifyDataSetChanged()
            }
        }
        if (oldMessagesSize ?: 0 < chat.messages.size /*&& chat.messages.lastOrNull()!!.accountId != chat.currentAccountId*/) scrollToEnd()
    }

    override fun scrollToEnd() {
        runOnUiThread { rvMessages.adapter?.let { rvMessages.scrollToPosition(it.itemCount - 1)  } }
    }

    override fun notifyData() {
        runOnUiThread { rvMessages.adapter?.apply { notifyDataSetChanged() } }
    }
}