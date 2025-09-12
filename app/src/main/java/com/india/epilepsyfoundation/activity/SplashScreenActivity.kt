package com.india.epilepsyfoundation.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.SurfaceHolder
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivitySplashScreenBinding
import com.india.epilepsyfoundation.utils.SharedPreference

class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoView = binding.videoView
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_video}")
        videoView.setVideoURI(videoUri)

        // Make surface white before first frame
        videoView.setZOrderOnTop(true)
        binding.videoView.setBackgroundColor(Color.WHITE)

        videoView.setOnPreparedListener { mp ->
            binding.placeholderImage.visibility = View.GONE
            binding.videoView.setBackgroundColor(Color.TRANSPARENT)
            mp.isLooping = false
            videoView.start()
        }

        videoView.setOnCompletionListener {
            navigateToNextScreen()
        }

        videoView.setOnErrorListener { _, what, extra ->
            Log.d("pawan", "Error playing video: what=$what, extra=$extra")
            navigateToNextScreen()
            true
        }
    }

    private fun navigateToNextScreen() {
        val isRegistered = SharedPreference.getBoolean("is_registered")
        val intent = if (isRegistered) {
            Intent(this@SplashScreenActivity, DashboardActivity::class.java)
        } else {
            Intent(this@SplashScreenActivity, RegisterActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
