package com.india.epilepsyfoundation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.database.AppDatabase
import com.india.epilepsyfoundation.entity.ReminderNotificationEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class PathologyTestAlarmReceiver : BroadcastReceiver() {

    private fun applyLocale(context: Context): Context {
        val langCode = SharedPreference.get("lang") ?: "en"
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val localizedContext = applyLocale(context)

        val testName = intent.getStringExtra("test_name") ?: ""
        val labName = intent.getStringExtra("lab_name") ?: ""
        val visitDate = intent.getStringExtra("visit_date") ?: ""
        val visitTime = intent.getStringExtra("visit_time") ?: ""
        val alarmIndex = intent.getIntExtra("alarm_index", -1)

        if (alarmIndex == -1) {
            Log.e("PathologyAlarm", "❌ Received alarm with invalid or missing 'alarm_index'! Aborting...")
            return
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "Pathology_visit_channel_v2"
        val title = localizedContext.getString(R.string.pathology_test_reminder_title)
        val notificationText = if (testName.isNotEmpty() && labName.isNotEmpty() && visitDate.isNotEmpty() && visitTime.isNotEmpty()) {
            localizedContext.getString(R.string.pathology_test_reminder_text, testName, labName, visitDate, visitTime)
        } else {
            localizedContext.getString(R.string.pathology_test_reminder_text_short, testName)
        }

        val reminderNotificationEntity = ReminderNotificationEntity(
            notificationTitle = title,
            notificationContent = notificationText
        )


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = Uri.parse("android.resource://${context.packageName}/raw/notification")
            val channel = NotificationChannel(
                channelId,
                title,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableLights(true)
                enableVibration(true)
                setSound(soundUri, Notification.AUDIO_ATTRIBUTES_DEFAULT)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.app_notification_ic)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), notification)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getInstance(context)
            db.reminderNotificationDao().insert(reminderNotificationEntity)
            Log.d("DoctorAlarm", "✅ Notification saved in Room DB")
        }
    }
}
