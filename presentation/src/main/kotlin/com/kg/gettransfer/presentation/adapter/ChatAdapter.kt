package com.kg.gettransfer.presentation.adapter

import android.support.v4.content.ContextCompat
import com.kg.gettransfer.R

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.ChatModel.Type
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.ui.SystemUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.chat_item_my_message.*
import kotlinx.android.synthetic.main.chat_item_not_my_message.*
import java.lang.IllegalArgumentException


class ChatAdapter(
        private var chatItems: ChatModel,
        private val listener: MessageReadListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun changeModel(chatItems: ChatModel){
        this.chatItems = chatItems
    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemCount() = chatItems.messages.size

    override fun getItemViewType(position: Int) = chatItems.getMessageType(position).ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, pos: Int) {
        val message = chatItems.messages[pos]
        when(chatItems.getMessageType(pos)){
            Type.CURRENT_ACCOUNT_MESSAGE     -> (holder as ViewHolderMyMessage).bind(message)
            Type.NOT_CURRENT_ACCOUNT_MESSAGE -> (holder as ViewHolderNotMyMessage).bind(message, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when(viewType){
        Type.CURRENT_ACCOUNT_MESSAGE.ordinal -> ViewHolderMyMessage(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_my_message, parent, false))
        Type.NOT_CURRENT_ACCOUNT_MESSAGE.ordinal -> ViewHolderNotMyMessage(LayoutInflater.from(parent.context).inflate(R.layout.chat_item_not_my_message, parent, false))
        else -> throw IllegalArgumentException()
    }

    class ViewHolderMyMessage(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(message: MessageModel) = with(containerView) {
            myMessageText.text = message.text
            myMessageTimeText.text = SystemUtils.formatMessageDateTimePattern(message.createdAt).let {
                it.substring(0, 1).toUpperCase().plus(it.substring(1)) }
            indicatorMessageRead.isVisible = true
            indicatorMessageRead.setImageDrawable(ContextCompat.getDrawable(context,
                    when {
                        message.sendAt != null -> R.drawable.ic_message_sending
                        message.readAt == null -> R.drawable.ic_message_not_read
                        else -> R.drawable.ic_message_read
                    }
            ))
        }
    }

    class ViewHolderNotMyMessage(override val containerView: View):
            RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind(message: MessageModel, listener: MessageReadListener) = with(containerView) {
            notMyMessageText.text = message.text
            notMyMessageTimeText.text = SystemUtils.formatMessageDateTimePattern(message.createdAt).let {
                it.substring(0, 1).toUpperCase().plus(it.substring(1)) }
            if(message.readAt == null) listener(message.id)
        }
    }
}

typealias MessageReadListener = (Long) -> Unit