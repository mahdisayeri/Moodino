package com.iranmobiledev.moodino.ui.entry

import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.iranmobiledev.moodino.base.BaseFragment
import com.iranmobiledev.moodino.data.Entry
import com.iranmobiledev.moodino.data.EntryDate
import com.iranmobiledev.moodino.databinding.AddEntryFragmentBinding
import com.iranmobiledev.moodino.callback.DatePickerDialogEventListener
import com.iranmobiledev.moodino.callback.EmojiClickListener
import com.iranmobiledev.moodino.callback.TimePickerCallback
import com.iranmobiledev.moodino.data.EntryTime
import com.iranmobiledev.moodino.ui.MainActivityViewModel
import com.iranmobiledev.moodino.utlis.*
import com.iranmobiledev.moodino.utlis.dialog.TimePickerDialog
import com.iranmobiledev.moodino.utlis.dialog.getPersianDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import org.koin.android.ext.android.inject
import saman.zamani.persiandate.PersianDate


class AddEntryFragment : BaseFragment(), EmojiClickListener, DatePickerDialogEventListener,
    TimePickerCallback {

    private val entry = Entry()
    private lateinit var binding: AddEntryFragmentBinding
    private var persianDate: PersianDate = PersianDate()
    private val args: AddEntryFragmentArgs by navArgs()
    private val sharedPref: SharedPreferences by inject()
    private lateinit var mainViewModel: MainActivityViewModel
    private var nightMode: Boolean = false
    private var timeDialog = TimePickerDialog(PersianDate(), null)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEntryFragmentBinding.inflate(inflater, container, false)
        mainViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        setupUtil()
        setupClicks()
        setupUi()

        return binding.root
    }

    private fun setupUtil() {
        args.date.let { entry.date = it }
        args.time.let { entry.time = it }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.emojiViewAddEntry.setEmojiClickListener(this)


    }

    private fun setupUi() {
        binding.continueButton.visibility =
            if (mainViewModel.initialFromBackPressEntryDetailAddEntry) View.VISIBLE else View.GONE
        binding.dateTv.text = args.date.let { getDate(it) }
        binding.timeTv.text = getTime()
        if (mainViewModel.selectedAddEntryEmoji != -1)
            binding.emojiViewAddEntry.setSelectedEmojiView(mainViewModel.selectedAddEntryEmoji)
    }

    private fun navigateToEntryDetailFragment(entry: Entry) {
        val action =
            AddEntryFragmentDirections.actionAddEntryFragmentToEntryDetailFragment(entry = entry)
        findNavController().navigate(action)
    }

    private fun setupClicks() {
        binding.date.implementSpringAnimationTrait()
        binding.time.implementSpringAnimationTrait()
        binding.closeFragment.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.date.setOnClickListener {
            val persianDate = PersianDate()
            entry.date.let {
                persianDate.shYear = it.year
                persianDate.shMonth = it.month
                persianDate.shDay = it.day
            }
            getPersianDialog(requireContext(), this, persianDate, nightMode()).show()
        }
        binding.time.setOnClickListener {
            timeDialog = TimePickerDialog(persianDate, entry.time)
            timeDialog.setListener(this)
            timeDialog.show(parentFragmentManager, null)
        }
        binding.continueButton.setOnClickListener { navigateToEntryDetailFragment(entry) }
    }

    override fun onEmojiItemClicked(emojiValue: Int) {
        mainViewModel.selectedAddEntryEmoji = emojiValue
        entry.emojiValue = emojiValue
        if (!mainViewModel.initialFromBackPressEntryDetailAddEntry)
            navigateToEntryDetailFragment(entry)
    }

    override fun onDateSelected(persianPickerDate: PersianPickerDate) {
        val persianDate = PersianDate()
        entry.date = EntryDate(
            persianPickerDate.persianYear,
            persianPickerDate.persianMonth,
            persianPickerDate.persianDay
        )
        if(checkDateAndTimeState(dateDialogData = persianPickerDate, timeDialogData = timeDialog, persianDate = persianDate)) {
            onTimePickerDataReceived(persianDate.hour,persianDate.minute)
            entry.date = EntryDate(
                persianDate.shYear,
                persianDate.shMonth,
                persianDate.shDay
            )
            entry.time = EntryTime(persianDate.hour.toString(),persianDate.minute.toString())
        }
        else
            onTimePickerDataReceived(timeDialog.currentTime.hour.toInt(),timeDialog.currentTime.minutes.toInt())
        setupDate()
    }

    private fun setupDate() {
        entry.date.let {
            persianDate.shDay = it.day
            persianDate.shMonth = it.month
            persianDate.shYear = it.year
        }
        entry.time.let {
            persianDate.hour = Integer.parseInt(it.hour)
            persianDate.minute = Integer.parseInt(it.minutes)
        }
        binding.dateTv.text =
            getDate(pattern = "j F Y", date = persianDate)
    }

    override fun onTimePickerDataReceived(hour: Int, minute: Int) {
        val time = EntryTime("", "")
        if (hour < 10)
            time.hour = "0$hour"
        else
            time.hour = hour.toString()
        if (minute < 10)
            time.minutes = "0$minute"
        else
            time.minutes = minute.toString()
        entry.time = time
        setupTime(time)
    }

    private fun setupTime(time: EntryTime) {
        val hour = time.hour
        val minute = time.minutes
        binding.timeTv.text = "$hour:$minute"
    }
}