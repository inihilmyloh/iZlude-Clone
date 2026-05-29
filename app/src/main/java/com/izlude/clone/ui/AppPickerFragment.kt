package com.izlude.clone.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
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
 * Fragment for picking apps to clone.
 * Shows all installed user apps with search functionality.
 */
class AppPickerFragment : Fragment() {

    private lateinit var rvApps: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var adapter: AppListAdapter
    private lateinit var cloneManager: CloneManager

    private var allApps: List<AppInfo> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cloneManager = CloneManager.getInstance(requireContext())
        rvApps = view.findViewById(R.id.rvApps)
        etSearch = view.findViewById(R.id.etSearch)
        shimmerLayout = view.findViewById(R.id.shimmerLayout)

        setupRecyclerView()
        setupSearch()
        loadApps()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter(
            context = requireContext(),
            onCloneClick = { appInfo ->
                cloneApp(appInfo)
            }
        )

        rvApps.layoutManager = LinearLayoutManager(requireContext())
        rvApps.adapter = adapter

        // Smooth scrolling
        rvApps.setHasFixedSize(true)
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString() ?: ""
                val filtered = AppUtils.filterApps(allApps, query)
                adapter.updateApps(filtered)
            }
        })
    }

    private fun loadApps() {
        shimmerLayout.visibility = View.VISIBLE
        shimmerLayout.startShimmer()
        rvApps.visibility = View.GONE

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                allApps = AppUtils.loadInstalledApps(requireContext())

                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                rvApps.visibility = View.VISIBLE

                adapter.updateApps(allApps)
            } catch (e: Exception) {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                rvApps.visibility = View.VISIBLE

                Toast.makeText(
                    requireContext(),
                    "Gagal memuat aplikasi: ${e.message}",
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
                Toast.makeText(
                    requireContext(),
                    "${appInfo.appName} berhasil dikloning!",
                    Toast.LENGTH_SHORT
                ).show()

                // Update the item in the list
                refreshCloneStatus()

                // Update parent stats
                (activity as? HomeActivity)?.updateStats()
            }
            is CloneManager.CloneResult.AlreadyCloned -> {
                Toast.makeText(
                    requireContext(),
                    "${appInfo.appName} sudah dikloning",
                    Toast.LENGTH_SHORT
                ).show()
            }
            is CloneManager.CloneResult.NeedsProvisioning -> {
                // Not used anymore with Virtual Engine
            }
            is CloneManager.CloneResult.Error -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.clone_failed) + ": ${result.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun refreshCloneStatus() {
        val clonedPackages = CloneStorage.getClonedPackages()
        allApps = allApps.map { app ->
            app.copy(isCloned = clonedPackages.contains(app.packageName))
        }
        val query = etSearch.text?.toString() ?: ""
        adapter.updateApps(AppUtils.filterApps(allApps, query))
    }

    override fun onResume() {
        super.onResume()
        if (allApps.isNotEmpty()) {
            refreshCloneStatus()
        }
    }
}
