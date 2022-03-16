package com.iranmobiledev.moodino.ui.states

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.iranmobiledev.moodino.base.BaseFragment
import com.iranmobiledev.moodino.data.BottomNavState
import com.iranmobiledev.moodino.databinding.DaysInARowCardBinding
import com.iranmobiledev.moodino.databinding.FragmentStatsBinding
import com.iranmobiledev.moodino.ui.states.viewmodel.StatsFragmentViewModel
import com.iranmobiledev.moodino.utlis.BottomNavVisibility
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus


class StatsFragment : BaseFragment() {
    private lateinit var binding: FragmentStatsBinding

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(BottomNavState(true))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val model : StatsFragmentViewModel by viewModels()

        /**
         * with scope variable we can call launch and
         * do heavy tasks in a coroutine thread .
         */
        val scope = CoroutineScope(Dispatchers.IO)

        val daysInARowCardBinding = binding.daysInRowCardInclude
        scope.launch {
            model.daysInRowManager(requireContext(),daysInARowCardBinding)
        }

        val lineChart = binding.moodChartCardInclude.moodsLineChart
        scope.launch {
            model.initializeLineChart(lineChart,requireContext())
        }

        val pieChart = binding.moodCountCardInclude.moodCountPieChart
        scope.launch {
            model.initializePieChart(pieChart,requireContext())
        }
    }
}