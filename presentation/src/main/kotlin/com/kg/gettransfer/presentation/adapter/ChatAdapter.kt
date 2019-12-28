package com.kg.gettransfer.presentation.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import androidx.core.view.isVisible
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.ChatModel.Type
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.ui.SystemUtils

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_item_my_message.*
import kotlinx.android.synthetic.main.chat_item_not_my_message.*

class ChatAdapter(
    private var chatItems: ChatModel,
    private val messageReadListener: MessageReadListener,
    private val copyMessageListener: CopyMessageListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun changeModel(chatItems: ChatModel) {
        this.chatItems = chatItems
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = chatItems.messages.size

    override fun getItemViewType(position: Int) = chatItems.getMessageType(position).ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val message = chatItems.messages[pos]
        when (chatItems.getMessageType(pos)) {
            Type.CURRENT_ACCOUNT_MESSAGE     -> if (holder is ViewHolderMyMessage) {
                holder.bind(message, copyMessageListener)
            }
            Type.NOT_CURRENT_ACCOUNT_MESSAGE -> if (holder is ViewHolderNotMyMessage) {
                holder.bind(message, messageReadListener, copyMessageListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        Type.CURRENT_ACCOUNT_MESSAGE.ordinal     ->
            ViewHolderMyMessage(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_my_message, parent, false)
            )
        Type.NOT_CURRENT_ACCOUNT_MESSAGE.ordinal ->
            ViewHolderNotMyMessage(
                LayoutInflater.from(parent.context).inflate(R.layout.chat_item_not_my_message, parent, false)
            )
        else                                     -> error("")
    }

    class ViewHolderMyMessage(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(message: MessageModel, copyMessageListener: CopyMessageListener) = with(containerView) {
            myMessageText.text = message.text
            myMessageTimeText.text = SystemUtils.formatMessageDateTimePattern(message.createdAt)
                .also { it.substring(0, 1).toUpperCase().plus(it.substring(1)) }
            indicatorMessageRead.isVisible = true
            indicatorMessageRead.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    when {
                        message.sendAt != null -> R.drawable.ic_message_sending
                        message.readAt == null -> R.drawable.ic_message_not_read
                        else                   -> R.drawable.ic_message_read
                    }
                )
            )
            setOnLongClickListener {
                copyMessageListener(message.text)
                return@setOnLongClickListener true
            }
            setOnClickListener { copyMessageListener(message.text) }
        }
    }

    class ViewHolderNotMyMessage(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(
            message: MessageModel,
            messageReadListener: MessageReadListener,
            copyMessageListener: CopyMessageListener
        ) = with(containerView) {

            notMyMessageText.text = message.text
            notMyMessageTimeText.text = SystemUtils.formatMessageDateTimePattern(message.createdAt)
                .also { it.substring(0, 1).toUpperCase().plus(it.substring(1)) }
            if (message.readAt == null) {
                messageReadListener(message.id)
            }
            setOnLongClickListener {
                copyMessageListener(message.text)
                return@setOnLongClickListener true
            }
        }
    }
}

typealias MessageReadListener = (Long) -> Unit
typealias CopyMessageListener = (String) -> Unit
