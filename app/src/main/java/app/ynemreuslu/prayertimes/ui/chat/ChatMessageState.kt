package app.ynemreuslu.prayertimes.ui.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.toMutableStateList

class ChatMessageState(
    messages: List<ChatMessage> = emptyList()
) {
    private val _messages: MutableList<ChatMessage> = messages.toMutableStateList()
    val messages: List<ChatMessage> = _messages

    fun addMessage(msg: ChatMessage) {
        _messages.add(msg)
    }

    // Add this new function to clear all messages
    fun clearMessages() {
        _messages.clear()
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun replaceLastPendingMessage() {
        val lastMessage = _messages.lastOrNull()
        lastMessage?.let {
            val newMessage = lastMessage.apply { isPending = false }
            _messages.removeAt(_messages.size - 1)
            _messages.add(newMessage)
        }
    }
}