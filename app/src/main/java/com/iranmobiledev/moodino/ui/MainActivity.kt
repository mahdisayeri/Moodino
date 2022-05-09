package com.iranmobiledev.moodino.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.iranmobiledev.moodino.R
import com.iranmobiledev.moodino.base.BaseActivity
import com.iranmobiledev.moodino.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus
import com.iranmobiledev.moodino.ui.calendar.calendarpager.initGlobal
import com.iranmobiledev.moodino.utlis.setupWithNavController

class MainActivity : BaseActivity() {

    private val TAG = "mainActivity"
    lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainActivityViewModel by viewModels()


    private var currentNavController: LiveData<NavController>? = null

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGlobal(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUi()
        onFabClickListener()
    }

    private fun setupUi() {
        navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.background = null
        setFragmentDestinationChangeListener()
    }

    private fun onFabClickListener() {

        val menuItems = arrayListOf(
            binding.yesterdayButtonButton,
            binding.todayButton,
            binding.anotherDayButton
        )

        binding.fabMenu.setOnClickListener {
            viewModel.actionMenu(menuItems)
            viewModel.actionFab(binding.fabMenu)
        }
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState)
        }
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val navGraphIds = listOf(R.navigation.nav_graph)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_graph,
            intent = intent
        )

        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun setFragmentDestinationChangeListener() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.entriesFragment -> showBottomNav()
                R.id.statsFragment -> showBottomNav()
                R.id.calenderFragment -> showBottomNav()
                R.id.moreFragment -> showBottomNav()
                else -> hideBottomNav()
            }
        }
    }

    private fun showBottomNav() {
//        binding.fab.show()
//        binding.fab.isClickable = true
        binding.bottomAppBar.visibility = View.VISIBLE
        binding.bottomAppBar.performShow()
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
//        binding.fab.hide()
//        binding.fab.isClickable = false
        binding.bottomAppBar.visibility = View.GONE
        binding.bottomAppBar.performHide(true)
        binding.bottomNavigationView.visibility = View.GONE
    }

}

