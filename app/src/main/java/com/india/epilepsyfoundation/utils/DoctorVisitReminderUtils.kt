package com.india.epilepsyfoundation.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.service.DoctorVisitAlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DoctorVisitReminderUtils {

    fun scheduleDoctorAlarms(context: Context, visit: DoctorVisitReminderEntity) {
        cancelDoctorAlarms(context, visit.id)

        scheduleAlarm(context, visit, 0, daysBefore = 3, hourOfDay = 7, minute = 0)
        scheduleAlarm(context, visit, 1, daysBefore = 1, hourOfDay = 19, minute = 0)
    }

    fun cancelDoctorAlarms(context: Context, visitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (index in 0..1) {
            val intent = Intent(context, DoctorVisitAlarmReceiver::class.java).apply {
                action = "com.india.epilepsyfoundation.DOCTOR_ALARM_${visitId}_$index"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(visitId, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            Log.d("DoctorAlarm", "Canceled alarm for visitId=$visitId index=$index")
        }
    }

    private fun scheduleAlarm(
        context: Context,
        visit: DoctorVisitReminderEntity,
        index: Int,
        daysBefore: Int,
        hourOfDay: Int,
        minute: Int
    ) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val visitDateTimeStr = "${visit.doctorVisitDate} ${visit.doctorVisitTime}"

        val visitCalendar = Calendar.getInstance().apply {
            time = dateFormat.parse(visitDateTimeStr) ?: return
        }


        val alarmTime = Calendar.getInstance().apply {
            timeInMillis = visitCalendar.timeInMillis
            add(Calendar.DATE, -daysBefore)
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Skip alarm if the time is in the past
        if (alarmTime.timeInMillis <= System.currentTimeMillis()) {
            Log.w("DoctorAlarm", "⏩ Skipping alarm for visitId=${visit.id} index=$index (time is in past)")
            return
        }

        val intent = Intent(context, DoctorVisitAlarmReceiver::class.java).apply {
            action = "com.india.epilepsyfoundation.DOCTOR_ALARM_${visit.id}_$index"
            putExtra("doctor_name", visit.doctorName)
            putExtra("visit_date", visit.doctorVisitDate)
            putExtra("visit_time", visit.doctorVisitTime)
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
            Log.d(
                "DoctorAlarm",
                "✅ Scheduled alarm for visitId=${visit.id} index=$index at ${alarmTime.time}"
            )
        } catch (e: SecurityException) {
            Log.e("DoctorAlarm", "❌ SecurityException: Cannot schedule exact alarm", e)
        }
    }

    private fun getRequestCode(visitId: Int, index: Int): Int {
        return visitId * 10 + index // Ensures unique request code for each alarm
    }
}
