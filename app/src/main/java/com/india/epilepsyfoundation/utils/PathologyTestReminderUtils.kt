package com.india.epilepsyfoundation.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity
import com.india.epilepsyfoundation.service.PathologyTestAlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object PathologyTestReminderUtils {

    fun schedulePathologyAlarms(context: Context, visit: PathologyTestReminderEntity) {
        cancelPathologyAlarms(context, visit.id)
        scheduleAlarm(context, visit, 0) // Only one alarm; you can add more with other indexes
    }

    fun cancelPathologyAlarms(context: Context, visitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (index in 0..0) { // Only one alarm index
            val intent = Intent(context, PathologyTestAlarmReceiver::class.java).apply {
                action = "com.india.epilepsyfoundation.PATHOLOGY_ALARM_${visitId}_$index"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(visitId, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            Log.d("PathologyAlarm", "Canceled alarm for visitId=$visitId index=$index")
        }
    }

    private fun scheduleAlarm(
        context: Context,
        visit: PathologyTestReminderEntity,
        index: Int
    ) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val visitDateTimeStr = "${visit.reminderDate} ${visit.reminderTime}"

        val visitCalendar = Calendar.getInstance().apply {
            time = dateFormat.parse(visitDateTimeStr) ?: return
        }

//        val visitCalendar = Calendar.getInstance().apply {
//            add(Calendar.MINUTE, 1)
//        }

        val alarmTime = Calendar.getInstance().apply {
            timeInMillis = visitCalendar.timeInMillis
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarmTime.timeInMillis <= System.currentTimeMillis()) {
            Log.w("PathologyAlarm", "⏩ Skipping alarm for visitId=${visit.id} index=$index (past time)")
            return
        }

        val intent = Intent(context, PathologyTestAlarmReceiver::class.java).apply {
            action = "com.india.epilepsyfoundation.PATHOLOGY_ALARM_${visit.id}_$index"
            putExtra("test_name", visit.testName)
            putExtra("visit_date", visit.visitDate)
            putExtra("visit_time", visit.visitTime)
            putExtra("lab_name", visit.labName)
            putExtra("alarm_index", index)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getRequestCode(visit.id, index),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime.timeInMillis,
                pendingIntent
            )
            Log.d("PathologyAlarm", "✅ Scheduled alarm for visitId=${visit.id} index=$index at ${alarmTime.time}")
        } catch (e: SecurityException) {
            Log.e("PathologyAlarm", "❌ SecurityException: Cannot schedule exact alarm", e)
        }
    }

    private fun getRequestCode(visitId: Int, index: Int): Int {
        return visitId * 10 + index
    }
}
