package com.dualspace.clone

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.dualspace.clone.ui.HomeActivity

/**
 * Splash screen with animated logo and loading indicator.
 * Transitions to HomeActivity after a short delay.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Make status bar and nav bar transparent
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        animateSplash()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }

    private fun animateSplash() {
        val splashContainer = findViewById<View>(R.id.splashContainer)
        val splashIcon = findViewById<View>(R.id.splashIcon)
        val splashTitle = findViewById<View>(R.id.splashTitle)
        val splashSubtitle = findViewById<View>(R.id.splashSubtitle)
        val glowEffect = findViewById<View>(R.id.glowEffect)
        val progressIndicator = findViewById<View>(R.id.progressIndicator)

        // Initial state
        splashContainer.alpha = 0f
        splashContainer.scaleX = 0.8f
        splashContainer.scaleY = 0.8f
        progressIndicator.alpha = 0f

        // Animate container in
        val fadeIn = ObjectAnimator.ofFloat(splashContainer, "alpha", 0f, 1f).apply {
            duration = 600
        }
        val scaleX = ObjectAnimator.ofFloat(splashContainer, "scaleX", 0.8f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator(1.2f)
        }
        val scaleY = ObjectAnimator.ofFloat(splashContainer, "scaleY", 0.8f, 1f).apply {
            duration = 800
            interpolator = OvershootInterpolator(1.2f)
        }

        // Icon rotation
        val iconRotation = ObjectAnimator.ofFloat(splashIcon, "rotation", -10f, 0f).apply {
            duration = 800
            interpolator = OvershootInterpolator(2f)
        }

        // Glow pulse animation
        val glowPulse = ObjectAnimator.ofFloat(glowEffect, "scaleX", 1f, 1.3f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val glowPulseY = ObjectAnimator.ofFloat(glowEffect, "scaleY", 1f, 1.3f).apply {
            duration = 1500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        // Progress indicator fade in
        val progressFade = ObjectAnimator.ofFloat(progressIndicator, "alpha", 0f, 1f).apply {
            duration = 400
            startDelay = 800
        }

        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY, iconRotation, glowPulse, glowPulseY, progressFade)
            start()
        }

        // Title slide up
        splashTitle.translationY = 20f
        splashTitle.alpha = 0f
        splashTitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(300)
            .start()

        // Subtitle slide up
        splashSubtitle.translationY = 20f
        splashSubtitle.alpha = 0f
        splashSubtitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(500)
            .start()
    }
}
