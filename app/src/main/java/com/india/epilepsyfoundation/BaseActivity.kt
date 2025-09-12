package com.india.epilepsyfoundation

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.india.epilepsyfoundation.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val langCode = SharedPreference.get("lang") ?: "en"
        val context = updateLocale(newBase, langCode)
        super.attachBaseContext(context)
    }

    private fun updateLocale(context: Context, langCode: String?): Context {
        if (langCode.isNullOrEmpty()) return context

        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}
