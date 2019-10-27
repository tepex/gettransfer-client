package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.core.presentation.WorkerManager

import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message

import com.kg.gettransfer.presentation.mapper.ChatMapper
import com.kg.gettransfer.presentation.mapper.MessageMapper

import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.view.ChatView

import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.kg.gettransfer.utilities.Analytics

import java.util.Calendar

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import org.koin.core.inject
import org.koin.core.parameter.parametersOf

@InjectViewState
class ChatPresenter : BasePresenter<ChatView>(), ChatEventListener, SocketEventListener {

    private val chatMapper: ChatMapper by inject()
    private val messageMapper: MessageMapper by inject()
    private val worker: WorkerManager by inject { parametersOf("ChatPresenter") }
    private val configsManager: ConfigsManager by inject()

    private var chatModel: ChatModel? = null
    private var transferModel: TransferModel? = null
    private var offerModel: OfferModel? = null

    internal var transferId = 0L

    override fun attachView(view: ChatView) {
        super.attachView(view)
        socketInteractor.addSocketListener(this)
        worker.main.launch {
            val transferCachedResult = withContext(worker.bg) {
                transferInteractor.getTransfer(transferId, false)
            }
            val chatCachedResult = withContext(worker.bg) { chatInteractor.getChat(transferId, true) }

            transferModel = transferCachedResult.model.map(configsManager.configs.transportTypes.map { it.map() })
            chatModel = chatMapper.toView(chatCachedResult.model)
            chatModel?.let { viewState.setChat(it) }

            getChatFromRemote()

            offerModel = fetchResult(WITHOUT_ERROR, withCacheCheck = false, checkLoginError = false) {
                offerInteractor.getOffers(transferId)
            }.model.firstOrNull()?.let { offerMapper.toView(it) }

            transferModel?.let { viewState.setToolbar(it, offerModel) }
        }
        onJoinRoom()
    }

    private fun onJoinRoom() {
        chatInteractor.eventChatReceiver = this
        chatInteractor.onJoinRoom(transferId)
        utils.launchSuspend { utils.asyncAwait { chatInteractor.sendMessageFromQueue(transferId) } }
    }

    fun onLeaveRoom() {
        chatInteractor.onLeaveRoom(transferId)
        chatInteractor.eventChatReceiver = null
        socketInteractor.removeSocketListener(this)
    }

    /*override fun doingSomethingAfterSendingNewMessagesCached() {
        getChatFromRemote()
    }*/

    private fun getChatFromRemote() {
        utils.launchSuspend {
            fetchData(withCacheCheck = NO_CACHE_CHECK) { chatInteractor.getChat(transferId) }?.let { initChatModel(it) }
        }
    }

    private fun initChatModel(chatResult: Chat){
        if (chatModel == null || chatModel!!.accountId == NO_ID) {
            chatModel = chatMapper.toView(chatResult)
            chatModel?.let { viewState.setChat(it) }
        } else {
            chatModel?.apply {
                val oldMessagesSize = messages.size
                messages = chatResult.messages.map { messageMapper.toView(it) }
                viewState.notifyData()
                if(chatResult.messages.size > oldMessagesSize) viewState.scrollToEnd()
            }
        }
    }

    fun onSentClick(text: String) {
        val time = Calendar.getInstance().time
        val newMessage = MessageModel(0, chatModel?.accountId ?: NO_ID, transferId, time, null, text, time.time)
        chatModel?.apply { messages = messages.plus(newMessage) }
        viewState.scrollToEnd()
        utils.launchSuspend { fetchResult { chatInteractor.newMessage(messageMapper.fromView(newMessage)) } }
        analytics.logSingleEvent(Analytics.MESSAGE_OUT)
    }

    fun readMessage(messageId: Long) {
        chatInteractor.readMessage(transferId, messageId)
        decreaseEventsMessagesCounter(transferId)
    }

    override fun onNewMessageEvent(message: Message) {
        log.error("new message: id = ${message.id}")
        utils.launchSuspend {
            fetchResult { chatInteractor.getChat(transferId, true) }
                .also { if (it.fromCache) initChatModel(it.model) }
            chatModel?.accountId?.let { if (isIdValid(message, it)) analytics.logSingleEvent(Analytics.MESSAGE_IN) }
        }
    }

    override fun onMessageReadEvent(message: Message) {
        chatModel?.messages?.find { it.id == message.id }?.readAt = message.readAt
        viewState.notifyData()
    }

    override fun onSocketConnected() {
        onJoinRoom()
    }

    override fun onSocketDisconnected() {}

    private fun isIdValid(message: Message, accountId: Long) = message.accountId != accountId && accountId != NO_ID

    override fun onDestroy() {
        worker.cancel()
        super.onDestroy()
    }

    companion object {
        const val NO_ID = 0L
    }
}
