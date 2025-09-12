package com.india.epilepsyfoundation.activity.information

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityUnderstandingEpilepsyBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.Locale

class UnderstandingEpilepsyActivity : BaseActivity() {

    private lateinit var binding: ActivityUnderstandingEpilepsyBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityUnderstandingEpilepsyBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to the activity content (this handles all root layouts properly)
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.toolbar_with_logo, binding.toolbar, false)

        val titleTextView = customView.findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = getString(R.string.lbl_understanding_epi)

        binding.toolbar.addView(customView)


        val combinedHtml = getString(R.string.txt_epi_info1) + getString(R.string.info3)
        binding.instuctionData.text = HtmlCompat.fromHtml(combinedHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.instuctionData.movementMethod = LinkMovementMethod.getInstance()

        binding.soundIc.setOnClickListener {
            if (isPlaying) {
                stopAudio()
            } else {
                playAudioForCurrentLanguage()
            }
        }
    }

    private fun playAudioForCurrentLanguage() {
        val audioResId = when (getCurrentLanguage()) {
            "hi" -> R.raw.understanding_epilepsy_hindi
            "mr" -> R.raw.understanding_epilepsy_hindi
            else -> R.raw.understanding_epilepsy_english
        }

        mediaPlayer = MediaPlayer.create(this, audioResId)
        mediaPlayer?.setOnCompletionListener {
            stopAudio()
        }
        mediaPlayer?.start()
        isPlaying = true
        binding.soundIc.setImageResource(R.drawable.speaker)
    }

    private fun stopAudio() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        binding.soundIc.setImageResource(R.drawable.soundoff)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
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
        val intent = Intent(this, InformationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
