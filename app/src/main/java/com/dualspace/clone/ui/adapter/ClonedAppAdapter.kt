package com.dualspace.clone.ui.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dualspace.clone.R
import com.dualspace.clone.model.ClonedApp
import com.dualspace.clone.util.AppUtils
import com.google.android.material.button.MaterialButton

/**
 * RecyclerView adapter for displaying cloned applications
 */
class ClonedAppAdapter(
    private val context: Context,
    private val onOpenClick: (ClonedApp) -> Unit,
    private val onRemoveClick: (ClonedApp) -> Unit
) : RecyclerView.Adapter<ClonedAppAdapter.ClonedAppViewHolder>() {

    private var apps: List<ClonedApp> = emptyList()
    private var lastAnimatedPosition = -1

    fun updateApps(newApps: List<ClonedApp>) {
        val diffResult = DiffUtil.calculateDiff(ClonedAppDiffCallback(apps, newApps))
        apps = newApps
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClonedAppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cloned_app, parent, false)
        return ClonedAppViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClonedAppViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
        animateItem(holder.itemView, position)
    }

    override fun getItemCount(): Int = apps.size

    private fun animateItem(view: View, position: Int) {
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position

            view.alpha = 0f
            view.translationX = -60f

            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val slideIn = ObjectAnimator.ofFloat(view, "translationX", -60f, 0f)

            AnimatorSet().apply {
                playTogether(fadeIn, slideIn)
                duration = 400
                startDelay = (position * 80).toLong().coerceAtMost(400)
                interpolator = DecelerateInterpolator(1.5f)
                start()
            }
        }
    }

    inner class ClonedAppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAppIcon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvAppPackage: TextView = itemView.findViewById(R.id.tvAppPackage)
        private val tvCloneStatus: TextView = itemView.findViewById(R.id.tvCloneStatus)
        private val btnOpen: MaterialButton = itemView.findViewById(R.id.btnOpen)
        private val btnRemove: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(app: ClonedApp) {
            tvAppName.text = app.appName
            tvAppPackage.text = app.packageName

            // Set status
            tvCloneStatus.text = "Aktif"

            // Load app icon
            val icon = AppUtils.getAppIcon(context, app.packageName)
            if (icon != null) {
                ivAppIcon.setImageDrawable(icon)
            } else {
                ivAppIcon.setImageResource(R.drawable.ic_apps)
            }

            // Clone age
            val clonedAgo = getTimeAgo(app.clonedAt)
            tvAppPackage.text = "Dikloning $clonedAgo"

            // Open button
            btnOpen.setOnClickListener {
                it.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction {
                        it.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                        onOpenClick(app)
                    }
                    .start()
            }

            // Remove button
            btnRemove.setOnClickListener {
                it.animate()
                    .rotation(90f)
                    .setDuration(200)
                    .withEndAction {
                        it.rotation = 0f
                        onRemoveClick(app)
                    }
                    .start()
            }

            // Long press to see package name
            itemView.setOnLongClickListener {
                android.widget.Toast.makeText(context, app.packageName, android.widget.Toast.LENGTH_SHORT).show()
                true
            }
        }

        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            val minutes = diff / (1000 * 60)
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "$days hari lalu"
                hours > 0 -> "$hours jam lalu"
                minutes > 0 -> "$minutes menit lalu"
                else -> "baru saja"
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private class ClonedAppDiffCallback(
        private val oldList: List<ClonedApp>,
        private val newList: List<ClonedApp>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].packageName == newList[newPos].packageName
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }
}
