package com.india.epilepsyfoundation.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivitySettingBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.*

class SettingActivity : BaseActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(LayoutInflater.from(this))
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


        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "N/A"
        }
        val versionText = getString(R.string.epilepsy_version_name, versionName)
        binding.tvAppVersion.text = versionText

        binding.langSectionLayout.setOnClickListener {
            showLanguageDialog()
        }

        binding.aboutUs.setOnClickListener {
            showAboutUsDialog()
        }
    }

    private fun showAboutUsDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.about_us_dailogue, null)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.show()
    }


    private fun showLanguageDialog() {
        val languages = arrayOf("English", "हिन्दी", "मराठी")
        val langCodes = arrayOf("en", "hi", "mr")

        val currentLang = getCurrentLanguage()
        var selectedIndex = langCodes.indexOf(currentLang).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(this)
            .setTitle("Select Language")
            .setSingleChoiceItems(languages, selectedIndex) { dialog, which ->
                val selectedLang = langCodes[which]
                if (selectedLang != currentLang) {
                    SharedPreference.set("lang", selectedLang)
                    recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getCurrentLanguage(): String {
        return SharedPreference.get("lang") ?: Locale.getDefault().language
    }

    private fun setLocale(langCode: String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        SharedPreference.set("lang", langCode)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
