package com.izlude.clone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.izlude.clone.R
import com.izlude.clone.core.CloneManager
import com.izlude.clone.ui.adapter.ClonedAppAdapter
import com.izlude.clone.util.AppUtils
import com.izlude.clone.util.CloneStorage
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment showing the list of cloned applications
 */
class ClonedAppsFragment : Fragment() {

    private lateinit var rvClonedApps: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var adapter: ClonedAppAdapter
    private lateinit var cloneManager: CloneManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cloned_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cloneManager = CloneManager.getInstance(requireContext())
        rvClonedApps = view.findViewById(R.id.rvClonedApps)
        emptyState = view.findViewById(R.id.emptyState)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadClonedApps()
    }

    private fun setupRecyclerView() {
        adapter = ClonedAppAdapter(
            context = requireContext(),
            onOpenClick = { clonedApp ->
                val success = cloneManager.launchClonedApp(clonedApp.packageName)
                if (!success) {
                    Toast.makeText(
                        requireContext(),
                        "Tidak dapat membuka ${clonedApp.appName}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onRemoveClick = { clonedApp ->
                showRemoveDialog(clonedApp.packageName, clonedApp.appName)
            }
        )

        rvClonedApps.layoutManager = LinearLayoutManager(requireContext())
        rvClonedApps.adapter = adapter

        // Add item animation
        rvClonedApps.itemAnimator?.apply {
            addDuration = 300
            removeDuration = 300
            changeDuration = 200
        }
    }

    private fun loadClonedApps() {
        val clonedApps = CloneStorage.getAllClonedApps()

        if (clonedApps.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            rvClonedApps.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            rvClonedApps.visibility = View.VISIBLE
            adapter.updateApps(clonedApps)
        }
    }

    private fun showRemoveDialog(packageName: String, appName: String) {
        MaterialAlertDialogBuilder(requireContext(), R.style.Theme_DualSpace_Dialog)
            .setTitle(getString(R.string.confirm_remove))
            .setMessage(getString(R.string.confirm_remove_msg, appName))
            .setPositiveButton(getString(R.string.yes_remove)) { _, _ ->
                cloneManager.removeClone(packageName)
                loadClonedApps()
                // Notify parent activity
                (activity as? HomeActivity)?.updateStats()
                Toast.makeText(
                    requireContext(),
                    "$appName dihapus dari kloning",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}
