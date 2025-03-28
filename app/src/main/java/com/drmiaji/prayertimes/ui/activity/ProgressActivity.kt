package com.drmiaji.prayertimes.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.drmiaji.prayertimes.R
import com.drmiaji.prayertimes.data.model.ProgressTask
import com.drmiaji.prayertimes.databinding.ActivityProgressBinding
import com.drmiaji.prayertimes.databinding.ItemActivityBinding
import com.drmiaji.prayertimes.ui.addactivity.AddProgressActivity
import com.drmiaji.prayertimes.utils.EasyAdapter
import com.drmiaji.prayertimes.utils.TimeUtils.fullDate
import com.drmiaji.prayertimes.utils.TimeUtils.hourMinutes
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Date
import androidx.activity.OnBackPressedCallback

@AndroidEntryPoint
class ProgressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProgressBinding
    private val viewModel: ProgressActivityViewModel by viewModels()
    private val scope = lifecycleScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgressBinding.inflate(layoutInflater)

        // ✅ Add callback here
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                setResult(RESULT_OK)
                finish()
            }
        })

        setContentView(binding.root)
        viewModel.getTodayActivity()
        setupAppBar()
        onSwipeDelete()
        scope.launch { viewModel.activitiesC.collect(this@ProgressActivity::populateActivities) }

        binding.nestedScrollView.setOnScrollChangeListener(scrollListener)
        binding.btnAddActivity.setOnClickListener {
            startForResult.launch(Intent(this, AddProgressActivity::class.java))
        }
        binding.tvDate.text = Timestamp.now().fullDate
    }

    private fun populateActivities(activities: List<ProgressTask>) {
        Log.d("TAG", "populateActivities: $activities")
        populateHeader(activities)
        binding.tvTextProgress.text = buildString {
            if (activities.isEmpty()) append("No Activity yet, go add it")
            else append("Progress: ")
        }

        binding.rvActivity.apply {
            itemAnimator = DefaultItemAnimator()
            adapter = EasyAdapter(activities, ItemActivityBinding::inflate) { binding, data ->
                with(binding) {
                    tvActivityLabel.text = data.title
                    tvAlarm.text = Timestamp(Date(data.date)).hourMinutes
                    ivCheck.apply {
                        setImageResource(if (data.isCheck) R.drawable.ic_check_fill else R.drawable.ic_check_outline)
                        imageTintList = ContextCompat.getColorStateList(
                            this.context,
                            if (data.isCheck) R.color.primary else R.color.black_60
                        )
                    }
                    ivCheck.setOnClickListener { viewModel.checkedTask(data.id, data.isCheck) }
                    btnCheck.setOnClickListener { viewModel.checkedTask(data.id, data.isCheck) }
                }
            }
        }
    }

    private fun populateHeader(activities: List<ProgressTask>) {
        binding.tvTotalTask.text = buildString { append("All(${activities.size})") }
        val progress = activities.filter { it.isCheck }.size.toDouble()
        val percentage = (progress / activities.size.toDouble()) * 100
        binding.tvProgress.text = buildString {
            if (activities.isNotEmpty()) {
                if (percentage % 2.0 == 0.0) append(percentage.toInt())
                else append(DecimalFormat("##.##").format(percentage))
                append("%")
            } else append("")
        }
        binding.linearProgressIndicator.apply {
            max = activities.size
            setProgress(progress.toInt())
        }
    }

    private val scrollListener = NestedScrollView.OnScrollChangeListener { _, _, y, _, oldY ->
        if (y > oldY) binding.btnAddActivity.shrink() else binding.btnAddActivity.extend()
    }

    private fun setupAppBar() = binding.appBar.apply {
        tvTitle.text = buildString { append("Activity") }
        btnBack.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) viewModel.getTodayActivity()
        }

    private fun onSwipeDelete() {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                v: RecyclerView,
                h: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(h: RecyclerView.ViewHolder, dir: Int) {
                viewModel.deleteTask(this@ProgressActivity, h.absoluteAdapterPosition)
            }
        }).attachToRecyclerView(binding.rvActivity)
    }
}