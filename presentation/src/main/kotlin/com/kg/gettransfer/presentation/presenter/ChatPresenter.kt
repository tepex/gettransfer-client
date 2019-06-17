package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.eventListeners.SocketEventListener
import com.kg.gettransfer.domain.model.Chat
import com.kg.gettransfer.domain.model.Message

import com.kg.gettransfer.presentation.mapper.CarrierTripMapper
import com.kg.gettransfer.presentation.mapper.ChatMapper
import com.kg.gettransfer.presentation.mapper.MessageMapper
import com.kg.gettransfer.presentation.model.ChatModel
import com.kg.gettransfer.presentation.model.MessageModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.CarrierTripModel

import com.kg.gettransfer.presentation.view.ChatView
import com.kg.gettransfer.presentation.view.Screens

import java.util.Calendar

import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject

import org.slf4j.Logger

@InjectViewState
class ChatPresenter : BasePresenter<ChatView>(), ChatEventListener, SocketEventListener {
    private val log: Logger by inject { parametersOf("GTR-chat") }

    private val chatMapper: ChatMapper by inject()
    private val messageMapper: MessageMapper by inject()
    private val carrierTripMapper: CarrierTripMapper by inject()

    private var chatModel: ChatModel? = null
    private var transferModel: TransferModel? = null
    private var offerModel: OfferModel? = null

    internal var transferId = 0L
    internal var tripId = 0L

    private val userRole = when (systemInteractor.lastMode) {
        Screens.PASSENGER_MODE -> ROLE_PASSENGER
        Screens.CARRIER_MODE -> ROLE_CARRIER
        else -> throw UnsupportedOperationException()
    }

    @CallSuper
    override fun attachView(view: ChatView) {
        super.attachView(view)
        socketInteractor.addSocketListener(this)
        utils.launchSuspend {
            val transferCachedResult = utils.asyncAwait { transferInteractor.getTransfer(transferId, false, userRole) }
            val chatCachedResult = utils.asyncAwait { chatInteractor.getChat(transferId, true) }

            transferModel = transferMapper.toView(transferCachedResult.model)
            chatModel = chatMapper.toView(chatCachedResult.model)
            chatModel?.let { viewState.setChat(it) }

            getChatFromRemote()

            if (tripId != NO_ID) {
                val carrierTripCachedModel = utils.asyncAwait { carrierTripInteractor.getCarrierTrip(tripId) }.model
                val carrierTripModel = carrierTripMapper.toView(carrierTripCachedModel)
                if (carrierTripModel.base.id != NO_ID) {
                    viewState.setToolbar(carrierTripModel)
                } else {
                    transferModel?.let { viewState.setToolbar(it, null, false) }
                }
            } else {
                offerModel = utils.asyncAwait { offerInteractor.getOffers(transferId, true) }
                    .model.firstOrNull()?.let { offerMapper.toView(it) }
                transferModel?.let { 
                    viewState.setToolbar(it, offerModel, tripId == NO_ID && userRole == ROLE_PASSENGER)
                }
            }
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
        sendAnalytics(MESSAGE_OUT)
    }

    fun onTransferInfoClick() {
        if (tripId != NO_ID) {
            router.navigateTo(Screens.TripDetails(tripId, transferId))
        } else if (userRole != ROLE_CARRIER) {
            router.navigateTo(Screens.Details(transferId))
        }
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
            chatModel?.accountId?.let { if (isIdValid(message, it)) sendAnalytics(MESSAGE_IN) }
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

    private fun sendAnalytics(event: String) =
        analytics.logEvent(event, createEmptyBundle(), emptyMap())

    private fun isIdValid(message: Message, accountId: Long) = message.accountId != accountId && accountId != NO_ID

    companion object {
        const val MESSAGE_IN  = "message_in"
        const val MESSAGE_OUT = "message_out"

        const val ROLE_CARRIER = "carrier"
        const val ROLE_PASSENGER = "passenger"

        const val NO_ID       = 0L
    }
}
