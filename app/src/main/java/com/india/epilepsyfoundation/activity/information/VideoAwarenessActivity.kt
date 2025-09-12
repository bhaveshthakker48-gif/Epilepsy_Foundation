package com.india.epilepsyfoundation.activity.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityVideoAwarenessBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.*

class VideoAwarenessActivity : BaseActivity() {

    private lateinit var binding: ActivityVideoAwarenessBinding
    private var isFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityVideoAwarenessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        playVideo()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.toolbar_with_logo, binding.toolbar, false)
        val titleTextView = customView.findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = getString(R.string.title_awareness_video)
        binding.toolbar.addView(customView)
    }

    private fun playVideo() {
        val videoView = binding.videoView1
        val playPauseButton = findViewById<ImageButton>(R.id.playPauseButton)
        val zoomButton = findViewById<ImageButton>(R.id.zoomButton)
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        val playIcon = binding.playIcon

        val uriPath = "android.resource://${packageName}/${R.raw.m}"
        val uri = Uri.parse(uriPath)
        videoView.setVideoURI(uri)
        videoView.requestFocus()
        videoView.start()
        playPauseButton.setImageResource(R.drawable.ic_pause)
        playIcon.visibility = View.GONE

        playPauseButton.setOnClickListener {
            if (videoView.isPlaying) {
                videoView.pause()
                playPauseButton.setImageResource(R.drawable.ic_play)
                playIcon.visibility = View.VISIBLE // Show play icon on pause
            } else {
                videoView.start()
                playPauseButton.setImageResource(R.drawable.ic_pause)
                playIcon.visibility = View.GONE // Hide on play
            }
        }

        playIcon.setOnClickListener {
            videoView.start()
            playPauseButton.setImageResource(R.drawable.ic_pause)
            playIcon.visibility = View.GONE
        }

        videoView.setOnClickListener {
            if (!videoView.isPlaying) {
                playIcon.visibility = View.VISIBLE
            }
        }

        videoView.setOnPreparedListener { mp ->
            seekBar.max = mp.duration
            val handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    if (videoView.isPlaying) {
                        seekBar.progress = videoView.currentPosition
                    }
                    handler.postDelayed(this, 500)
                }
            })

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) videoView.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        zoomButton.setOnClickListener {
            isFullscreen = !isFullscreen

            requestedOrientation = if (isFullscreen) {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            if (isFullscreen) {
                requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_IMMERSIVE
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        )
                supportActionBar?.hide()

                // Match parent for fullscreen
                val params = binding.videoView1.layoutParams
                params.width = FrameLayout.LayoutParams.MATCH_PARENT
                params.height = FrameLayout.LayoutParams.MATCH_PARENT
                binding.videoView1.layoutParams = params

            } else {
                requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                supportActionBar?.show()

                // Restore original layout params from XML (match constraints)
                val params = binding.videoView1.layoutParams
                params.width = 0 // match_constraint
                params.height = FrameLayout.LayoutParams.WRAP_CONTENT
                binding.videoView1.layoutParams = params
            }

            binding.videoView1.requestLayout()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu?.findItem(R.id.menu_language)
        val spinner = item?.actionView as? Spinner

        val languages = listOf("English", "हिन्दी", "मराठी")
        val adapter = ArrayAdapter(this, R.layout.toolbar_spinner_item, languages)
        adapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item)
        spinner?.adapter = adapter

        // Set current language
        when (getCurrentLanguage()) {
            "en" -> spinner?.setSelection(0)
            "hi" -> spinner?.setSelection(1)
            "mr" -> spinner?.setSelection(2)
        }

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLang = when (position) {
                    0 -> "en"
                    1 -> "hi"
                    2 -> "mr"
                    else -> "en"
                }

                if (selectedLang != getCurrentLanguage()) {
                    SharedPreference.set("lang", selectedLang)
                    recreate()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        return true
    }

    private fun getCurrentLanguage(): String {
        return SharedPreference.get("lang") ?: Locale.getDefault().language
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, InformationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }
}
