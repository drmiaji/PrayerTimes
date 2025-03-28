package com.drmiaji.prayertimes.ui.addactivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.data.model.toProgressEntity
import com.drmiaji.prayertimes.repo.PrayerRepository
import com.drmiaji.prayertimes.service.PrayerAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProgressViewModel @Inject constructor(
    private val repository: PrayerRepository,
    private val alarm: PrayerAlarm
) : ViewModel() {

    private val listTextRepeating = mutableListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    private val listActive = mutableListOf(-1, -1, -1, -1, -1, -1, -1)

    // 🟢 These are observed in UI
    private val _repeating = MutableStateFlow(listActive.toMutableList())
    val repeating: StateFlow<MutableList<Int>> get() = _repeating

    private val _textRepeating = MutableStateFlow("Not Repeating")
    val textRepeating: StateFlow<String> get() = _textRepeating

    private val scope = viewModelScope

    // 🟩 Toggle one day at a time
    fun setRepeating(position: Int, isActive: Boolean) {
        listActive[position] = if (isActive) position else -1
        emitRepeating()
    }

    // 🟩 Toggle all days
    fun setRepeating(isChecked: Boolean) {
        repeat(7) { listActive[it] = if (isChecked) it else -1 }
        emitRepeating()
    }

    // 🟩 For editing: set repeating from stored string
    fun setRepeatingFromString(repeating: String) {
        listActive.indices.forEach { listActive[it] = -1 } // Clear first
        repeating.split(" ").mapNotNull { it.toIntOrNull() }.forEach { index ->
            if (index in 0..6) listActive[index] = index
        }
        emitRepeating()
    }

    // 🟩 Save task (add or update)
    fun saveTask(context: Context, task: ProgressTask, isEdit: Boolean) {
        task.repeating = getRepeatingTaskString()
        alarm.setActivityAlarm(context, task, true)
        scope.launch {
            if (isEdit) {
                repository.localDataSource.updateProgressTask(task.toProgressEntity())
            } else {
                repository.addProgressTask(task)
            }
        }
    }

    // Internal 🔽
    private fun emitRepeating() {
        val filtered = listActive.toMutableList()
        scope.launch {
            _repeating.emit(filtered)
            _textRepeating.emit(getTextRepeating(filtered))
        }
    }

    private fun getRepeatingTaskString(): String = buildString {
        val activeDays = listActive.filter { it != -1 }
        if (activeDays.size == 7) append("7")
        else if (activeDays.isEmpty()) append("-1")
        else append(activeDays.joinToString(" "))
    }

    private fun getTextRepeating(list: List<Int>) = buildString {
        val active = list.filter { it != -1 }
        if (active.isEmpty()) append("Not Repeating")
        else if (active.size == 7) append("Everyday")
        else active.forEachIndexed { index, i ->
            append(listTextRepeating.getOrNull(i) ?: "")
            if (index != active.size - 1) append(", ")
        }
    }
}