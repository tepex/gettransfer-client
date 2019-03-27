package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.eventListeners.ChatEventListener
import com.kg.gettransfer.domain.eventListeners.SystemEventListener
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
import org.koin.core.parameter.parametersOf
import org.koin.standalone.inject
import java.util.Calendar
import org.slf4j.Logger

@InjectViewState
class ChatPresenter : BasePresenter<ChatView>(), ChatEventListener, SystemEventListener {
    private val log: Logger by inject { parametersOf("GTR-socket") }

    private val chatMapper: ChatMapper by inject()
    private val messageMapper: MessageMapper by inject()
    private val carrierTripMapper: CarrierTripMapper by inject()

    private lateinit var chatModel: ChatModel
    private var transferModel: TransferModel? = null
    private var offerModel: OfferModel? = null
    private var carrierTripModel: CarrierTripModel? = null

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
        systemInteractor.addListener(this)
        utils.launchSuspend {
            val transferCachedResult = utils.asyncAwait { transferInteractor.getTransfer(transferId, false, userRole) }
            val chatCachedResult = utils.asyncAwait { chatInteractor.getChat(transferId, true) }

            transferModel = transferMapper.toView(transferCachedResult.model)
            chatModel = chatMapper.toView(chatCachedResult.model)
            chatModel.let { viewState.setChat(it) }

            if (tripId != NO_ID) {
                val carrierTripCachedResult = utils.asyncAwait { carrierTripInteractor.getCarrierTrip(tripId) }
                carrierTripModel = carrierTripMapper.toView(carrierTripCachedResult.model)
                if (carrierTripModel != null && carrierTripModel!!.base.id != NO_ID) {
                    viewState.setToolbar(carrierTripModel!!)
                } else {
                    transferModel?.let { viewState.setToolbar(it, null, false) }
                }
            } else {
                val offerCachedResult = utils.asyncAwait { offerInteractor.getOffers(transferId) }
                offerModel = offerCachedResult.model.firstOrNull()?.let { offerMapper.toView(it) }
                transferModel?.let { viewState.setToolbar(it, offerModel, tripId == NO_ID && userRole == ROLE_PASSENGER) }
            }
        }
        getChatFromRemote()
        onJoinRoom()
    }

    private fun onJoinRoom(){
        chatInteractor.eventChatReceiver = this
        chatInteractor.onJoinRoom(transferId)
        utils.launchSuspend { utils.asyncAwait { chatInteractor.sendMessageFromQueue(transferId) } }
    }

    fun onLeaveRoom(){
        chatInteractor.onLeaveRoom(transferId)
        chatInteractor.eventChatReceiver = null
        systemInteractor.removeListener(this)
    }

    /*override fun doingSomethingAfterSendingNewMessagesCached() {
        getChatFromRemote()
    }*/

    private fun getChatFromRemote() {
        utils.launchSuspend {
            fetchData(withCacheCheck = NO_CACHE_CHECK) { chatInteractor.getChat(transferId) }
                    ?.let { initChatModel(it) }
        }
    }

    private fun initChatModel(chatResult: Chat){
        if(chatModel.currentAccountId == NO_ID) {
            chatModel = chatMapper.toView(chatResult)
            viewState.setChat(chatModel)
        } else {
            val oldMessagesSize = chatModel.messages.size
            chatModel.messages = chatResult.messages.map { messageMapper.toView(it) }
            viewState.notifyData()
            if(chatResult.messages.size > oldMessagesSize) viewState.scrollToEnd()
        }
    }

    fun onSentClick(text: String){
        val time = Calendar.getInstance().time
        val newMessage = MessageModel(0, chatModel.currentAccountId, transferId, time, null, text, time.time)
        chatModel.messages = chatModel.messages.plus(newMessage)
        viewState.scrollToEnd()
        utils.launchSuspend { fetchResult { chatInteractor.newMessage(messageMapper.fromView(newMessage)) } }
        sendAnalytics(MESSAGE_OUT)
    }

    fun onTransferInfoClick(){
        if (tripId != NO_ID) {
            router.navigateTo(Screens.TripDetails(tripId, transferId))
        } else if (userRole != ROLE_CARRIER) {
            router.navigateTo(Screens.Details(transferId))
        }
    }

    fun readMessage(messageId: Long) = chatInteractor.readMessage(transferId, messageId)

    override fun onNewMessageEvent(message: Message) {
        log.error("new message: id = ${message.id}")
        utils.launchSuspend{
            fetchResult { chatInteractor.getChat(transferId, true) }
                    .also { if (it.fromCache) initChatModel(it.model) }
            if (isIdValid(message)) sendAnalytics(MESSAGE_IN)
        }
    }

    override fun onMessageReadEvent(message: Message) {
        chatModel.messages.find { it.id == message.id }?.readAt = message.readAt
        viewState.notifyData()
    }

    override fun onSocketConnected() {
        onJoinRoom()
    }

    override fun onSocketDisconnected() {}

    private fun sendAnalytics(event: String) =
        analytics.logEvent(event, createEmptyBundle(), emptyMap())

    private fun isIdValid(message: Message) =
            message.accountId != chatModel.currentAccountId &&
                    chatModel.currentAccountId != NO_ID

    companion object {
        const val MESSAGE_IN  = "message_in"
        const val MESSAGE_OUT = "message_out"

        const val ROLE_CARRIER = "carrier"
        const val ROLE_PASSENGER = "passenger"

        const val NO_ID       = 0L
    }
}