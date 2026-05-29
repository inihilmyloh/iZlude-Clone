package com.izlude.clone.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.izlude.clone.R
import com.izlude.clone.core.CloneManager
import com.izlude.clone.model.AppInfo
import com.izlude.clone.ui.adapter.AppListAdapter
import com.izlude.clone.util.AppUtils
import com.izlude.clone.util.CloneStorage
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.launch

/**
 * Standalone activity for selecting apps to clone.
 * Can be launched independently from anywhere.
 */
class AppPickerActivity : AppCompatActivity() {

    private lateinit var rvApps: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: AppListAdapter
    private lateinit var cloneManager: CloneManager

    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_app_picker)

        cloneManager = CloneManager.getInstance(this)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.tab_add)
        }

        rvApps = findViewById(R.id.rvApps)
        etSearch = findViewById(R.id.etSearch)

        setupRecyclerView()
        setupSearch()
        loadApps()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter(
            context = this,
            onCloneClick = { appInfo -> cloneApp(appInfo) }
        )
        rvApps.layoutManager = LinearLayoutManager(this)
        rvApps.adapter = adapter
        rvApps.setHasFixedSize(true)
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                adapter.updateApps(AppUtils.filterApps(allApps, query))
            }
        })
    }

    private fun loadApps() {
        lifecycleScope.launch {
            try {
                allApps = AppUtils.loadInstalledApps(this@AppPickerActivity)
                adapter.updateApps(allApps)
            } catch (e: Exception) {
                Toast.makeText(
                    this@AppPickerActivity,
                    "Gagal memuat: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private val REQUEST_PROVISION_MANAGED_PROFILE = 1001

    private fun cloneApp(appInfo: AppInfo) {
        val result = cloneManager.cloneApp(appInfo.packageName, appInfo.appName)
        when (result) {
            is CloneManager.CloneResult.Success -> {
                Toast.makeText(this, "${appInfo.appName} berhasil dikloning!", Toast.LENGTH_SHORT).show()
                refreshCloneStatus()
            }
            is CloneManager.CloneResult.AlreadyCloned -> {
                Toast.makeText(this, "${appInfo.appName} sudah dikloning", Toast.LENGTH_SHORT).show()
            }
            is CloneManager.CloneResult.NeedsProvisioning -> {
                // Not used anymore with Virtual Engine
            }
            is CloneManager.CloneResult.Error -> {
                Toast.makeText(this, "Gagal: ${result.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshCloneStatus() {
        val clonedPackages = CloneStorage.getClonedPackages()
        allApps = allApps.map { it.copy(isCloned = clonedPackages.contains(it.packageName)) }
        val query = etSearch.text?.toString() ?: ""
        adapter.updateApps(AppUtils.filterApps(allApps, query))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
