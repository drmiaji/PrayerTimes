package com.drmiaji.prayertimes.ui.addactivity

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.databinding.ActivityAddProgressBinding
import com.drmiaji.prayertimes.utils.TimeUtils
import com.drmiaji.prayertimes.utils.TimeUtils.hour
import com.drmiaji.prayertimes.utils.TimeUtils.minutes
import com.drmiaji.prayertimes.utils.TimeUtils.partialDate
import com.drmiaji.prayertimes.utils.TimeUtils.stringFormat
import com.drmiaji.prayertimes.utils.TimeUtils.timeStamp
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import kotlin.random.Random

@AndroidEntryPoint
class AddProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProgressBinding
    private val viewModel: AddProgressViewModel by viewModels()
    private val scope = lifecycleScope
    private var selectedTime = Calendar.getInstance()

    private var isEdit = false
    private var editingTaskId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddProgressBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 🟩 Check if we are editing a task
        val taskToEdit: ProgressTask? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("task_to_edit", ProgressTask::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("task_to_edit")
        }
        taskToEdit?.let { task ->
            binding.appBar.ivBack.setImageResource(R.drawable.ic_arrow_back) // ✅ works now
            binding.appBar.ivBack.visibility = View.VISIBLE

            binding.appBar.btnBack.setOnClickListener {
                finish()
            }
            binding.edtTitle.setText(task.title)
            viewModel.setRepeatingFromString(task.repeating)
            selectedTime.timeInMillis = task.date
            populateDate(Timestamp(Date(task.date)))
            populateTime(selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE))
            isEdit = true
            editingTaskId = task.id
        }

        populateTime(Timestamp.now().hour, Timestamp.now().minutes)
        populateDate(Timestamp.now())

        scope.launch { viewModel.repeating.collect { populateRepeating(it) } }
        scope.launch {
            viewModel.textRepeating.collect {
                if (it == "Not Repeating") binding.btnRepeating.isChecked = false
                binding.tvRepeating.text = it
            }
        }

        with(binding) {
            appBar.apply {
                tvTitle.text = if (isEdit) "Edit Activity" else "Add Activity"
                btnBack.setOnClickListener { finish() }
            }

            // ✅ Change button label text based on edit mode
            btnCreateActivityText.text = if (isEdit) "Update" else "Create Activity"

            btnRepeating.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setRepeating(isChecked)
                rvRepeating.visibility = if (isChecked) View.VISIBLE else View.GONE
            }

            edtTitle.doAfterTextChanged {
                setUpButtonCreate(it.toString().isNotBlank())
            }

            btnTime.setOnClickListener { showDatePicker() }

            btnCreateActivity.setOnClickListener {
                viewModel.saveTask(
                    this@AddProgressActivity,
                    ProgressTask(
                        id = if (isEdit) editingTaskId else Random.nextLong(123, 1234567),
                        title = edtTitle.text.toString(),
                        date = selectedTime.time.time,
                        repeating = "", // Will be updated inside ViewModel
                        isCheck = false
                    ),
                    isEdit
                )
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun ActivityAddProgressBinding.setUpButtonCreate(isNotBlank: Boolean) {
        this.btnCreateActivity.apply {
            setCardBackgroundColor(
                ContextCompat.getColorStateList(
                    context, if (isNotBlank) R.color.primary else R.color.gray
                )
            )
            isClickable = isNotBlank
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setTitleText("Select Reminder Date")
            .build()
        datePicker.show(supportFragmentManager, "Date Reminder")
        datePicker.addOnPositiveButtonClickListener {
            showTimePicker()
            val date = Date(it).stringFormat.timeStamp
            populateDate(date)
        }
    }

    private fun showTimePicker() {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(Timestamp.now().hour)
            .setMinute(Timestamp.now().minutes)
            .setTitleText("Set Reminder Time")
            .build()
        picker.show(supportFragmentManager, "Reminder")
        picker.addOnPositiveButtonClickListener {
            populateTime(picker.hour, picker.minute)
        }
    }

    private fun populateDate(date: Timestamp) {
        binding.tvDate.text = date.partialDate
        selectedTime.time = date.toDate()
    }

    private fun populateTime(hour: Int, minute: Int) {
        binding.tvTime.text = buildString {
            selectedTime.set(Calendar.HOUR_OF_DAY, hour)
            selectedTime.set(Calendar.MINUTE, minute)
            val data = DateFormat.format("hh:mm a", Date(selectedTime.timeInMillis)).toString()
            append(data.uppercase())
        }
    }

    private fun populateRepeating(it: MutableList<Int>) {
        binding.rvRepeating.apply {
            itemAnimator = DefaultItemAnimator()
            adapter = RepeatingAdapter(it) { position, isActive ->
                viewModel.setRepeating(position, isActive)
            }
        }
    }
}