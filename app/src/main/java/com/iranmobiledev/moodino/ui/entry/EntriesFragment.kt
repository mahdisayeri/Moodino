package com.iranmobiledev.moodino.ui.entry

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iranmobiledev.moodino.R
import com.iranmobiledev.moodino.base.BaseFragment
import com.iranmobiledev.moodino.data.*
import com.iranmobiledev.moodino.databinding.FragmentEntriesBinding
import com.iranmobiledev.moodino.listener.DialogEventListener
import com.iranmobiledev.moodino.listener.EntryEventLister
import com.iranmobiledev.moodino.ui.entry.adapter.EntryContainerAdapter
import com.iranmobiledev.moodino.utlis.BottomNavVisibility
import com.iranmobiledev.moodino.utlis.MoodinoSharedPreferences
import com.iranmobiledev.moodino.utlis.implementSpringAnimationTrait
import org.greenrobot.eventbus.EventBus
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class EntriesFragment : BaseFragment(), KoinComponent, EntryEventLister {

    private lateinit var binding: FragmentEntriesBinding
    private lateinit var entriesContainerRv: RecyclerView
    private val entryViewModel: EntryViewModel by viewModel()
    private lateinit var navController: NavController
    private val entryContainerAdapter: EntryContainerAdapter by inject()

    override fun onStart() {
        super.onStart()
        val entry = arguments?.getParcelable<Entry>("entry")
        entry?.let {
            entryContainerAdapter.addEntry(it)
        }
        arguments?.clear()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(BottomNavState(true))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEntriesBinding.inflate(inflater, container, false)
        initViews()
        entriesContainerRvImpl()

        binding.addEntryCardView.setOnClickListener {}
        makeSpringAnimation(binding.addEntryCardView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MoodinoSharedPreferences.create(requireContext()).edit().putBoolean("first_enter", true)
            .apply()
    }

    private fun makeSpringAnimation(view: View) {
        view.implementSpringAnimationTrait()
    }

    private fun entriesContainerRvImpl() {
        entriesContainerRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        entriesContainerRv.adapter = entryContainerAdapter
    }

    private fun initViews() {
        entryContainerAdapter.create(requireContext(), this,
            entryViewModel.getEntries() as MutableList<MutableList<Entry>>
        )
        entriesContainerRv = binding.entriesContainerRv
        entriesContainerRv.itemAnimator = null
        navController = NavHostFragment.findNavController(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BottomNavVisibility.currentFragment.value = this.id
    }

    override fun delete(entry: Entry): Boolean {
        val dialog = makeDialog(
            mainText = "Delete loving memory?",
            subText = "This is your life we are talking\nabout. Do you want to delete a\npart of it?",
            icon = R.drawable.ic_delete,
        )
        dialog.setItemEventListener(object : DialogEventListener {
            override fun clickedItem(itemId: Int) {
                when (itemId) {
                    R.id.rightButton -> {
                        entryViewModel.deleteEntry(entry)
                        entryContainerAdapter.removeItem(entry)
                        dialog.dismiss()
                    }
                    R.id.leftButton -> {
                        dialog.dismiss()
                    }
                }
            }
        })
        dialog.show(parentFragmentManager, null)
        return true
    }
}