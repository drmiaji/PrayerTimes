package app.ynemreuslu.prayertimes.ui.chat

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.ynemreuslu.prayertimes.R
import app.ynemreuslu.prayertimes.ui.home.HomeContract
import app.ynemreuslu.prayertimes.ui.notificationpermission.NotificationPermissionContract
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val generativeModel: GenerativeModel,
) : ViewModel() {

    private val chat = generativeModel.startChat(
        history = listOf(
            content(role = "model") { text(applicationContext.getString(R.string.chatbot)) })
    )

    private val _uiState =
        MutableStateFlow(
            ChatContract.UiState(
                chatMessageState = ChatMessageState(
                    chat.history.map { content ->
                        ChatMessage(
                            text = content.parts.first().asTextOrNull().orEmpty(),
                            participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                            isPending = false
                        )
                    })
            )
        )
    val uiState = _uiState.asStateFlow()

    private val _uiEffect by lazy { Channel<ChatContract.UiEffect>() }
    val uiEffect: Flow<ChatContract.UiEffect> by lazy { _uiEffect.receiveAsFlow() }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun onAction(action: ChatContract.UiAction) {
        when (action) {
            is ChatContract.UiAction.EnterMessage -> {
                updateUiState {
                    copy(
                        userMessage = action.message, isExpanded = true
                    )
                }
            }

            is ChatContract.UiAction.SendMessage -> {
                _uiState.value.chatMessageState.addMessage(
                    ChatMessage(
                        text = action.chatMessage.text,
                        participant = Participant.USER,
                        isPending = true
                    )
                )
                viewModelScope.launch {
                    try {
                        val response = chat.sendMessage(action.chatMessage.text)
                        _uiState.value.chatMessageState.replaceLastPendingMessage()
                        response.text?.let { modelResponse ->
                            _uiState.value.chatMessageState.addMessage(
                                ChatMessage(
                                    text = modelResponse,
                                    participant = Participant.MODEL,
                                    isPending = false
                                )
                            )
                        }
                    } catch (e: Exception) {
                        _uiState.value.chatMessageState.replaceLastPendingMessage()
                        _uiState.value.chatMessageState.addMessage(
                            ChatMessage(
                                text = e.localizedMessage, participant = Participant.ERROR
                            )
                        )
                    }
                }
            }
        }
    }

    private fun updateUiState(block: ChatContract.UiState.() -> ChatContract.UiState) {
        _uiState.update(block)
    }

    private suspend fun emitUiEffect(effect: ChatContract.UiEffect) {
        _uiEffect.send(effect)
    }
}