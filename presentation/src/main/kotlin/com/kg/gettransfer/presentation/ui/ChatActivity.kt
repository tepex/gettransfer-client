package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import androidx.core.view.isVisible

import com.kg.gettransfer.presentation.adapter.ChatAdapter
import com.kg.gettransfer.presentation.adapter.CopyMessageListener
import com.kg.gettransfer.presentation.adapter.MessageReadListener
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TitleModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.ChatPresenter
import com.kg.gettransfer.presentation.view.ChatView

import java.util.Date

import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.toolbar.*

import org.jetbrains.anko.toast

@Suppress("TooManyFunctions")
class ChatActivity : BaseActivity(), ChatView {
    @InjectPresenter
    internal lateinit var presenter: ChatPresenter

    @ProvidePresenter
    fun createChatPresenter() = ChatPresenter()

    override fun getPresenter(): ChatPresenter = presenter

    private val messageReadListener: MessageReadListener = { presenter.readMessage(it) }
    private val copyMessageListener: CopyMessageListener = { copyMessage(it) }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(ChatView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_chat)

        rvMessages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        btnSend.setOnClickListener {
            if (messageText.text.isNotEmpty()) {
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

    override fun setToolbar(transfer: TransferModel, offer: OfferModel?) {
        offer?.let { initToolbar(it.driver?.name ?: it.carrier.profile?.name, it.phoneToCall) }
        with(transfer) {
            if (id != ChatPresenter.NO_ID) {
                initTransferInfoLayout(from, dateTime, id)
            }
        }
    }

    private fun initToolbar(userName: String?, userPhone: String?) = with(toolbar) {
        val titleText = userName?.let { getString(R.string.LNG_CHAT_WITH).plus(" ").plus(it) }
        setToolbar(this, titleText?.let { TitleModel.Str(titleText) } ?: TitleModel.Id(R.string.LNG_PAYMENT_CHAT))
        userPhone?.let { setToolbarRightButton(this, R.drawable.ic_phone_title) { presenter.callPhone(it) } }
    }

    private fun initTransferInfoLayout(
        from: String,
        date: Date,
        transferId: Long
    ) {
        // imgChevron.isVisible = isShowChevron
        layoutTransferInfo.apply {
            isVisible = true
            textTransferInfoFrom.text = from
            textTransferInfoDate.text = getString(
                R.string.chat_transfer_date_transfer_id_format,
                SystemUtils.formatDateTime(date),
                getString(R.string.LNG_TRANSFER).plus(" №$transferId")
            )
            // setOnClickListener { presenter.onTransferInfoClick() }
        }
    }

    override fun setChat(chat: ChatModel) {
        val oldMessagesSize = rvMessages.adapter?.itemCount
        rvMessages.apply {
            if (adapter == null) {
                adapter = ChatAdapter(chat, messageReadListener, copyMessageListener)
            } else {
                val chatAdapter = adapter
                if (chatAdapter is ChatAdapter) {
                    chatAdapter.changeModel(chat)
                    chatAdapter.notifyDataSetChanged()
                }
            }
        }
        /* && chat.messages.lastOrNull()!!.accountId != chat.currentAccountId */
        if (oldMessagesSize ?: 0 < chat.messages.size) {
            scrollToEnd()
        }
    }

    private fun copyMessage(text: String) {
        copyText(text)
        toast(getString(R.string.LNG_MESSAGE_COPIED))
    }

    override fun scrollToEnd() {
        runOnUiThread { rvMessages.adapter?.let { rvMessages.scrollToPosition(it.itemCount - 1) } }
    }

    override fun notifyData() {
        runOnUiThread { rvMessages.adapter?.notifyDataSetChanged() }
    }
}
