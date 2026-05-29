package com.dualspace.clone.ui.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dualspace.clone.R
import com.dualspace.clone.model.AppInfo
import com.google.android.material.button.MaterialButton

/**
 * RecyclerView adapter for displaying available apps to clone
 */
class AppListAdapter(
    private val context: Context,
    private val onCloneClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    private var apps: List<AppInfo> = emptyList()
    private var lastAnimatedPosition = -1

    fun updateApps(newApps: List<AppInfo>) {
        val diffResult = DiffUtil.calculateDiff(AppDiffCallback(apps, newApps))
        apps = newApps
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.bind(app)
        animateItem(holder.itemView, position)
    }

    override fun getItemCount(): Int = apps.size

    private fun animateItem(view: View, position: Int) {
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position

            view.alpha = 0f
            view.translationY = 50f

            val fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val slideUp = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f)

            AnimatorSet().apply {
                playTogether(fadeIn, slideUp)
                duration = 350
                startDelay = (position * 50).toLong().coerceAtMost(300)
                interpolator = DecelerateInterpolator(1.5f)
                start()
            }
        }
    }

    inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAppIcon: ImageView = itemView.findViewById(R.id.ivAppIcon)
        private val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
        private val tvAppPackage: TextView = itemView.findViewById(R.id.tvAppPackage)
        private val tvAppSize: TextView = itemView.findViewById(R.id.tvAppSize)
        private val btnAction: MaterialButton = itemView.findViewById(R.id.btnAction)
        private val cloneBadge: FrameLayout = itemView.findViewById(R.id.cloneBadge)

        fun bind(app: AppInfo) {
            tvAppName.text = app.appName
            tvAppPackage.text = app.packageName
            tvAppSize.text = app.getFormattedSize()

            // Set app icon
            if (app.icon != null) {
                ivAppIcon.setImageDrawable(app.icon)
            } else {
                ivAppIcon.setImageResource(R.drawable.ic_apps)
            }

            // Update clone badge and button state
            if (app.isCloned) {
                cloneBadge.visibility = View.VISIBLE
                btnAction.text = "Dikloning ✓"
                btnAction.isEnabled = false
                btnAction.alpha = 0.6f
                btnAction.setBackgroundColor(
                    context.getColor(R.color.surface_elevated)
                )
            } else {
                cloneBadge.visibility = View.GONE
                btnAction.text = context.getString(R.string.clone_app)
                btnAction.isEnabled = true
                btnAction.alpha = 1f
                btnAction.setBackgroundColor(
                    context.getColor(R.color.primary)
                )
            }

            btnAction.setOnClickListener {
                if (!app.isCloned) {
                    // Button press animation
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
                            onCloneClick(app)
                        }
                        .start()
                }
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    private class AppDiffCallback(
        private val oldList: List<AppInfo>,
        private val newList: List<AppInfo>
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
