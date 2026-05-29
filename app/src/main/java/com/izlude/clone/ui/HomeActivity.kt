package com.izlude.clone.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.izlude.clone.R
import com.izlude.clone.util.CloneStorage
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Main home screen with tabs for cloned apps and app picker
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fabAddClone: ExtendedFloatingActionButton
    private lateinit var tvClonedCount: TextView
    private lateinit var tvAvailableApps: TextView

    private val cloneReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.izlude.clone.CLONE_COMPLETE" -> {
                    updateStats()
                    Toast.makeText(
                        this@HomeActivity,
                        getString(R.string.clone_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    // Switch to cloned apps tab
                    viewPager.currentItem = 0
                }
                "com.izlude.clone.CLONE_REMOVED" -> {
                    updateStats()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupViewPager()
        setupFab()
        updateStats()
        registerCloneReceiver()
    }

    private fun initViews() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        fabAddClone = findViewById(R.id.fabAddClone)
        tvClonedCount = findViewById(R.id.tvClonedCount)
        tvAvailableApps = findViewById(R.id.tvAvailableApps)

        val btnSettings = findViewById<ImageButton>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val options = arrayOf("Tentang Aplikasi")
            androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_DualSpace_Dialog)
                .setTitle(getString(R.string.settings))
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> {
                            androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_DualSpace_Dialog)
                                .setTitle("Tentang iZludeClone")
                                .setMessage("Aplikasi kloning (App Cloner) berbasis Virtual OS (BlackBox).\n\nMemungkinkan Anda menjalankan dua akun dalam satu perangkat dengan aman dan terisolasi.")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                }
                .show()
        }
    }

    private fun setupViewPager() {
        val adapter = HomePagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_cloned)
                    tab.setIcon(R.drawable.ic_clone)
                }
                1 -> {
                    tab.text = getString(R.string.tab_add)
                    tab.setIcon(R.drawable.ic_add)
                }
            }
        }.attach()

        // Handle FAB visibility based on tab
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> fabAddClone.show()
                    1 -> fabAddClone.hide()
                }
            }
        })
    }

    private fun setupFab() {
        fabAddClone.setOnClickListener {
            // Switch to app picker tab
            viewPager.currentItem = 1
        }
    }

    fun updateStats() {
        tvClonedCount.text = CloneStorage.getClonedCount().toString()

        // Count available apps (approximate)
        Thread {
            try {
                val pm = packageManager
                @Suppress("DEPRECATION")
                val installedApps = pm.getInstalledApplications(0)
                    .filter { (it.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) == 0 }
                    .size
                runOnUiThread {
                    tvAvailableApps.text = installedApps.toString()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    tvAvailableApps.text = "--"
                }
            }
        }.start()
    }

    private fun registerCloneReceiver() {
        val filter = IntentFilter().apply {
            addAction("com.izlude.clone.CLONE_COMPLETE")
            addAction("com.izlude.clone.CLONE_REMOVED")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(cloneReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(cloneReceiver, filter)
        }
    }

    override fun onResume() {
        super.onResume()
        updateStats()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(cloneReceiver)
        } catch (_: Exception) {
        }
    }

    /**
     * ViewPager adapter for Home tabs
     */
    private inner class HomePagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ClonedAppsFragment()
                1 -> AppPickerFragment()
                else -> ClonedAppsFragment()
            }
        }
    }
}
