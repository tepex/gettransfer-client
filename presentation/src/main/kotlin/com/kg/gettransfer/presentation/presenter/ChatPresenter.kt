package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.eventListeners.SystemEventListener
import com.kg.gettransfer.domain.model.Message
import com.kg.gettransfer.presentation.mapper.ChatMapper
import com.kg.gettransfer.presentation.mapper.MessageMapper
import com.kg.gettransfer.presentation.mapper.TransferMapper
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.view.ChatView
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject
import java.util.Calendar
import org.slf4j.Logger

@InjectViewState
class ChatPresenter : BasePresenter<ChatView>(), ChatEventListener, SystemEventListener {
    private val log: Logger by inject { parametersOf("GTR-socket") }

    private val transferMapper: TransferMapper by inject()
    private val chatMapper: ChatMapper by inject()
    private val messageMapper: MessageMapper by inject()

    private var chatModel: ChatModel? = null
    private var transferModel: TransferModel? = null
    private var offerModel: OfferModel? = null

    internal var transferId = 0L

    internal var sendMessagesAfterReconnect = true

    @CallSuper
    override fun attachView(view: ChatView) {
        super.attachView(view)
        systemInteractor.addListener(this)
        utils.launchSuspend {
            val transferCachedResult = utils.asyncAwait { transferInteractor.getTransfer(transferId, true) }
            val offerCachedResult = utils.asyncAwait { offerInteractor.getOffers(transferId, true) }
            val chatCachedResult = utils.asyncAwait { chatInteractor.getChat(transferId, true) }

            transferModel = transferMapper.toView(transferCachedResult.model)
            offerModel = offerCachedResult.model.firstOrNull()?.let { offerMapper.toView(it) }
            if(chatCachedResult.fromCache) chatModel = chatMapper.toView(chatCachedResult.model)

            initToolbar()
            chatModel?.let { viewState.setChat(it, true) }
        }
        getChatFromRemote()
    }

    fun onJoinRoom(sendMessages: Boolean){
        sendMessagesAfterReconnect = false
        chatInteractor.eventChatReceiver = this
        chatInteractor.onJoinRoom(transferId)
        if(sendMessages) {
            utils.launchSuspend {
                val result = utils.asyncAwait { chatInteractor.sendAllNewMessagesSocket() }
                if (result.model > 0) doingSomethingAfterSendingNewMessagesCached()
            }
        }
    }

    fun onLeaveRoom(){
        chatInteractor.onLeaveRoom(transferId)
        systemInteractor.removeListener(this)
    }

    private fun initToolbar(){
        val accountIds = chatModel?.accounts?.keys?.filter { it != chatModel!!.currentAccountId }
        val name = chatModel?.accounts?.get(accountIds?.firstOrNull())?.fullName
        viewState.initToolbar(transferModel, offerModel, name?: "")
    }

    override fun doingSomethingAfterSendingNewMessagesCached() {
        getChatFromRemote()
        viewState.scrollToEnd()
    }

    private fun getChatFromRemote() {
        utils.launchSuspend {
            val chatRemoteResult = utils.asyncAwait { chatInteractor.getChat(transferId) }
            chatRemoteResult.error?.let { checkResultError(it) }
            if (chatRemoteResult.error != null) viewState.setError(chatRemoteResult.error!!)
            else {
                if(chatModel == null){
                    chatModel = chatMapper.toView(chatRemoteResult.model)
                    initToolbar()
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

    fun onSentClick(text: String){
        val time = Calendar.getInstance().time
        val newMessage = MessageModel(0, chatModel!!.currentAccountId, transferId, time, null, text, time.time)
        chatModel!!.messages = chatModel!!.messages.plus(newMessage)
        viewState.scrollToEnd()
        utils.launchSuspend { utils.asyncAwait { chatInteractor.newMessage(messageMapper.fromView(newMessage)) } }
    }

    fun readMessage(messageId: Long) = chatInteractor.readMessage(transferId, messageId)

    override fun onNewMessageEvent(message: Message) {
        log.error("new message: id = ${message.id}")
        utils.launchSuspend{
            val result = utils.asyncAwait{ chatInteractor.getChat(transferId, true) }
            if(result.fromCache) {
                chatModel!!.messages = result.model.messages.map { messageMapper.toView(it) }
                viewState.notifyData()
                if(message.accountId != chatModel!!.currentAccountId) viewState.scrollToEnd()
            }
        }
    }

    override fun onMessageReadEvent(message: Message) {
        chatModel?.messages?.find { it.id == message.id }?.readAt = message.readAt
        viewState.notifyData()
    }

    override fun onSocketConnected() {
        onJoinRoom(sendMessagesAfterReconnect)
    }

    override fun onSocketDisconnected() {
        sendMessagesAfterReconnect = true
    }
}