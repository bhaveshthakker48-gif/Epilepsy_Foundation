package com.india.epilepsyfoundation.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.service.MedicationAlarmReceiver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object MedicationReminderUtils {

    fun scheduleMedicationAlarms(context: Context, visit: MedicationReminderEntity) {
        cancelMedicationAlarms(context, visit.id)

        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())
        val startDateTime = dateFormat.parse("${visit.startDate} ${visit.startTime}") ?: return
        val calendar = Calendar.getInstance().apply { time = startDateTime }

        val duration = visit.duration.toIntOrNull() ?: return
        val normalizedFrequency = normalizeFrequency(context, visit.frequency)
        val doseTimes = getDoseTimes(calendar, normalizedFrequency)
        val normalizedDurationUnit = normalizeDurationUnit(context, visit.durationUnit)
        val endCalendar = getEndDate(calendar.clone() as Calendar, duration, normalizedDurationUnit)

        var currentDay = calendar.clone() as Calendar
        var dayIndex = 0
        val maxAlarms = 100
        var alarmCount = 0

        while (currentDay <= endCalendar && alarmCount < maxAlarms) {
            doseTimes.forEachIndexed { index, time ->
                val alarmTime = (currentDay.clone() as Calendar).apply {
                    set(Calendar.HOUR_OF_DAY, time.first)
                    set(Calendar.MINUTE, time.second)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                if (alarmTime.timeInMillis > System.currentTimeMillis() && alarmCount < maxAlarms) {
                    scheduleSingleAlarm(context, visit, alarmTime, dayIndex * 10 + index)
                    alarmCount++
                }
            }
            currentDay.add(Calendar.DAY_OF_YEAR, 1)
            dayIndex++
        }

    }

    private fun getEndDate(start: Calendar, duration: Int, unit: String): Calendar {
        return (start.clone() as Calendar).apply {
            when (unit.lowercase(Locale.ROOT)) {
                "days" -> add(Calendar.DAY_OF_YEAR, duration)
                "weeks" -> add(Calendar.WEEK_OF_YEAR, duration)
                "months" -> add(Calendar.MONTH, duration)
                "years" -> add(Calendar.YEAR, duration)
            }
        }
    }

    private fun getDoseTimes(startCalendar: Calendar, frequency: String): List<Pair<Int, Int>> {
        val doseCount = when (frequency.lowercase(Locale.ROOT)) {
            "once" -> 1
            "twice" -> 2
            "thrice" -> 3
            "four" -> 4
            else -> 1
        }

        val startHour = startCalendar.get(Calendar.HOUR_OF_DAY)
        val startMinute = startCalendar.get(Calendar.MINUTE)

        val interval = 24 / doseCount

        return (0 until doseCount).map { index ->
            val hour = (startHour + interval * index) % 24
            Pair(hour, startMinute)
        }
    }


    private fun scheduleSingleAlarm(context: Context, visit: MedicationReminderEntity, calendar: Calendar, index: Int) {
        val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
            action = "com.india.epilepsyfoundation.Medication_ALARM_${visit.id}_$index"
            putExtra("test_name", visit.medicationName)
            putExtra("visit_date", visit.startDate)
            putExtra("visit_time", visit.startTime)
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
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("MedicationAlarm", "✅ Scheduled alarm for visitId=${visit.id} index=$index at ${calendar.time}")
        } catch (e: SecurityException) {
            Log.e("MedicationAlarm", "❌ SecurityException: Cannot schedule exact alarm", e)
        }
    }

    fun cancelMedicationAlarms(context: Context, visitId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (index in 0..99) { // Cancel up to 100 possible alarms
            val intent = Intent(context, MedicationAlarmReceiver::class.java).apply {
                action = "com.india.epilepsyfoundation.Medication_ALARM_${visitId}_$index"
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                getRequestCode(visitId, index),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            Log.d("MedicationAlarm", "❌ Canceled alarm for visitId=$visitId index=$index")
        }
    }

    private fun getRequestCode(visitId: Int, index: Int): Int {
        return visitId * 10 + index
    }

    private fun normalizeDurationUnit(context: Context, unit: String): String {
        return when (unit) {
            context.getString(R.string.days) -> "days"
            context.getString(R.string.weeks) -> "weeks"
            context.getString(R.string.months) -> "months"
            context.getString(R.string.years) -> "years"
            else -> "days"
        }
    }

    private fun normalizeFrequency(context: Context, frequency: String): String {
        return when (frequency) {
            context.getString(R.string.one) -> "once"      // दिन में एक बार
            context.getString(R.string.two) -> "twice"     // दिन में दो बार
            context.getString(R.string.three) -> "thrice"  // दिन में तीन बार
            context.getString(R.string.four) -> "four"     // दिन में चार बार
            else -> "once"
        }
    }

}
