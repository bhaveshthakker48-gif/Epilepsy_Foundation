package com.india.epilepsyfoundation.utils

import android.content.Context
import android.os.Build
import java.util.*

object LocaleHelper {

    fun wrap(context: Context): Context {
        val langCode = SharedPreference.get("lang") ?: Locale.getDefault().language
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            context
        }
    }
}
