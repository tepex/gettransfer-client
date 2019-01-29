package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.mapper.ChatMapper
import com.kg.gettransfer.presentation.mapper.MessageMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.view.ChatView
import org.koin.standalone.inject
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask


@InjectViewState
class ChatPresenter : BasePresenter<ChatView>() {
    private val transferMapper: TransferMapper by inject()
    private val chatMapper: ChatMapper by inject()
    private val messageMapper: MessageMapper by inject()

    private var chatModel: ChatModel? = null
    private lateinit var transferModel: TransferModel
    private lateinit var offerModel: OfferModel

    internal var transferId = 0L

    companion object {
        const val TIMEOUT_FOR_CHAT = 5000L
    }

    @CallSuper
    override fun attachView(view: ChatView) {
        super.attachView(view)
        utils.launchSuspend {
            val transferCachedResult = utils.asyncAwait { transferInteractor.getTransfer(transferId, true) }
            val offerCachedResult = utils.asyncAwait { offerInteractor.getOffers(transferId, true) }
            val chatCachedResult = utils.asyncAwait { chatInteractor.getChat(transferId, true) }

            transferModel = transferMapper.toView(transferCachedResult.model)
            offerModel = offerMapper.toView(offerCachedResult.model.first())
            chatModel = chatMapper.toView(chatCachedResult.model)

            viewState.initToolbar(transferModel, offerModel)
            chatModel?.let { viewState.setChat(it, true) }
        }
        checkNewMessagesCached()
        startTimerForUpdateChatHistory()
    }

    private fun getChatFromRemote() {
        utils.launchSuspend {
            val chatRemoteResult = utils.asyncAwait { chatInteractor.getChat(transferId) }
            if (chatRemoteResult.error != null) viewState.setError(chatRemoteResult.error!!)
            else {
                if(chatModel == null){
                    chatModel = chatMapper.toView(chatRemoteResult.model)
                    viewState.setChat(chatModel!!, true)
                } else {
                    val oldMessagesSize = chatModel!!.messages.size
                    chatModel!!.messages = chatRemoteResult.model.messages.map { messageMapper.toView(it) }
                    viewState.setChat(chatModel!!,
                            oldMessagesSize < chatModel!!.messages.size && chatModel!!.messages.lastOrNull()!!.accountId != chatModel!!.currentAccountId)
                }
            }
        }
    }

    private fun startTimerForUpdateChatHistory(){
        Timer().schedule(object : TimerTask() {
            override fun run() {
                getChatFromRemote()
            }
        }, 0, TIMEOUT_FOR_CHAT)
    }

    fun onSentClick(text: String){
        val time = Calendar.getInstance().time
        val newMessage = MessageModel(0, chatModel!!.currentAccountId, transferModel.id, time, null, text, time.time)
        chatModel!!.messages = chatModel!!.messages.plus(newMessage)
        viewState.scrollToEnd()
        utils.launchSuspend {
            val chatCachedResult = utils.asyncAwait { chatInteractor.newMessage(messageMapper.fromView(newMessage)) }
            chatModel = chatMapper.toView(chatCachedResult.model)
            viewState.setChat(chatModel!!, true)
        }
    }

    fun readMessages(firstVisibleElement: Int, lastVisibleElement: Int){
        if(chatModel != null){
            val elements = chatModel!!.messages.subList(firstVisibleElement, lastVisibleElement + 1)
            elements.forEach {
                if(it.accountId != chatModel!!.currentAccountId && it.readAt == null){
                    utils.launchSuspend {
                        utils.asyncAwait { chatInteractor.readMessage(it.id) }
                    }
                }
            }
        }
    }
}