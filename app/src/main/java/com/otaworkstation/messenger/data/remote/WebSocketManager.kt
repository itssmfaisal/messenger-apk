package com.otaworkstation.messenger.data.remote

import android.util.Log
import com.google.gson.Gson
import com.otaworkstation.messenger.data.model.Message
import com.otaworkstation.messenger.data.model.StatusUpdate
import com.otaworkstation.messenger.util.Constants
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader

class WebSocketManager(private val token: String) {
    private var mStompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private val gson = Gson()

    var onMessageReceived: ((Message) -> Unit)? = null
    var onStatusUpdateReceived: ((StatusUpdate) -> Unit)? = null
    var onErrorReceived: ((String) -> Unit)? = null

    fun connect() {
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Constants.WS_URL)
        
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))
        
        mStompClient?.withClientHeartbeat(10000)?.withServerHeartbeat(10000)

        compositeDisposable.add(mStompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.OPENED -> Log.d("WS", "Stomp connection opened")
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.ERROR -> Log.e("WS", "Error", lifecycleEvent.exception)
                    ua.naiksoftware.stomp.dto.LifecycleEvent.Type.CLOSED -> Log.d("WS", "Stomp connection closed")
                    else -> {}
                }
            })

        mStompClient?.connect(headers)

        subscribeToQueues()
    }

    private fun subscribeToQueues() {
        compositeDisposable.add(mStompClient!!.topic("/user/queue/messages")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stompMessage ->
                val message = gson.fromJson(stompMessage.payload, Message::class.java)
                onMessageReceived?.invoke(message)
            }, { throwable ->
                Log.e("WS", "Error on subscribe messages", throwable)
            }))

        compositeDisposable.add(mStompClient!!.topic("/user/queue/status-updates")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stompMessage ->
                val update = gson.fromJson(stompMessage.payload, StatusUpdate::class.java)
                onStatusUpdateReceived?.invoke(update)
            }, { throwable ->
                Log.e("WS", "Error on subscribe status", throwable)
            }))

        compositeDisposable.add(mStompClient!!.topic("/user/queue/errors")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stompMessage ->
                onErrorReceived?.invoke(stompMessage.payload)
            }, { throwable ->
                Log.e("WS", "Error on subscribe errors", throwable)
            }))
    }

    fun sendMessage(recipient: String, content: String, attachment: Map<String, Any>? = null) {
        val payload = mutableMapOf<String, Any>(
            "recipient" to recipient,
            "content" to content
        )
        attachment?.let { payload.putAll(it) }
        
        mStompClient?.send("/app/chat.send", gson.toJson(payload))?.subscribe()
    }

    fun markDelivered(messageId: Long) {
        val payload = mapOf("messageId" to messageId)
        mStompClient?.send("/app/chat.delivered", gson.toJson(payload))?.subscribe()
    }

    fun markSeen(messageId: Long) {
        val payload = mapOf("messageId" to messageId)
        mStompClient?.send("/app/chat.seen", gson.toJson(payload))?.subscribe()
    }

    fun join() {
        mStompClient?.send("/app/chat.join", "")?.subscribe()
    }

    fun disconnect() {
        mStompClient?.disconnect()
        compositeDisposable.dispose()
    }
}