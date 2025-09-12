package com.india.epilepsyfoundation.service

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.activity.epdetector.EpDetectorWarningActivity
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.Locale

class EpDetectorService : Service(), SensorEventListener {

    private val TAG = "pawan"

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isShaking = false
    private var shakeStartTime: Long = 0
    private var shakeCheckHandler: Handler? = null
    private var shakeRunnable: Runnable? = null

    private lateinit var receiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        SharedPreference.init(this)
        setLocaleFromPreferences()
        registerShakeReceiver()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // ✅ Initialize ringtone once
        val soundUri = Uri.parse("android.resource://${packageName}/raw/warning")
        ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
    }

    companion object {
        var ringtone: Ringtone? = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        startForegroundWithNotification()
        startShakeDetection()
        return START_STICKY
    }

    private fun startForegroundWithNotification() {
        Log.d(TAG, "Starting foreground service")

        val channelId = "ep_detector_channel"
        val channelName = "Epilepsy Detection"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Epilepsy Detector")
            .setContentText("Monitoring for seizures...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(1, notification)
    }

    private fun startShakeDetection() {
        Log.d(TAG, "Starting shake detection")
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun stopShakeDetection() {
        Log.d(TAG, "Stopping shake detection")
        sensorManager.unregisterListener(this)
    }

    private var lastShakeTimestamp: Long = 0

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            val acceleration = Math.sqrt((x * x + y * y + z * z).toDouble()) - SensorManager.GRAVITY_EARTH
            val currentTime = System.currentTimeMillis()

            if (acceleration > 8) {
                lastShakeTimestamp = currentTime
                if (!isShaking) {
                    isShaking = true
                    shakeStartTime = currentTime
                    Log.d(TAG, "Shake started")
                    startShakeMonitoring()
                }
            } else {
                if (isShaking && (currentTime - lastShakeTimestamp) > 1000) {
                    // Only stop if it’s been quiet for 1 second
                    val elapsed = currentTime - shakeStartTime
                    if (elapsed < 20000) {
                        Log.d(TAG, "Shake stopped before 20 seconds")
                        stopShakeMonitoring()
                    }
                }
            }
        }
    }


    private fun startShakeMonitoring() {
        shakeCheckHandler = Handler(Looper.getMainLooper())
        shakeRunnable = Runnable {
            val elapsed = System.currentTimeMillis() - shakeStartTime
            if (elapsed >= 20000) {
                Log.d("pawan", "Shake lasted 20 seconds — playing ringtone")
                onFitDetected()
            } else {
                Log.d("pawan", "Shake stopped before 20 seconds")
                stopShakeMonitoring()
            }
        }
        shakeCheckHandler?.postDelayed(shakeRunnable!!, 20000)
    }


    private fun stopShakeMonitoring() {
        shakeCheckHandler?.removeCallbacks(shakeRunnable!!)
        isShaking = false
        shakeStartTime = 0
    }


    private fun onFitDetected() {
        Log.d(TAG, "onFitDetected triggered")
        SharedPreference.setBoolean("FLAGRING", true)

        // ✅ Reset shake state so another shake can trigger even if ringtone is playing
        stopShakeMonitoring()
        isShaking = false

        val intent = Intent(this, EpDetectorWarningActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)

        val broadcastIntent = Intent("SHOW_CANCEL_UI")
        sendBroadcast(broadcastIntent)

        playRingtone()
        sendAlertMessages()

        // 🆕 Auto reset after 1 min if user doesn’t press cancel
        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPreference.getBoolean("FLAGRING", false)) {
                Log.d(TAG, "Auto resetting after timeout")
                stopRingtoneAndDetection()
            }
        }, 60000)
    }




    private fun playRingtone() {
        try {
            Log.d(TAG, "playRingtone() called")

            if (ringtone == null) {
                Log.d(TAG, "ringtone is null, creating new")
                val soundUri = Uri.parse("android.resource://${packageName}/raw/warning")
                ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
            }

            if (ringtone?.isPlaying == true) {
                Log.d(TAG, "Ringtone already playing")
                return
            }

            ringtone?.play()
            Log.d(TAG, "Ringtone started")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ringtone: ${e.message}")
        }
    }

    private fun setLocaleFromPreferences() {
        val langCode = SharedPreference.get("lang") ?: Locale.getDefault().language
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    private fun sendAlertMessages() {
        try {
            val contact1 = SharedPreference.get("contactOne")
            val contact2 = SharedPreference.get("contactTwo")

            val language = SharedPreference.get("lang") ?: "en"

            val message = when (language) {
                "hi" -> "सावधान: संभावित दौरे का पता चला है। कृपया तुरंत जांच करें।"
                "mr" -> "आपत्कालीन: संभाव्य झटका ओळखला गेला आहे. कृपया त्वरित तपासा."
                else -> "ALERT: A potential seizure has been detected. Please check immediately."
            }

            if (!contact1.isNullOrEmpty()) sendSMS(contact1, message)
            if (!contact2.isNullOrEmpty()) sendSMS(contact2, message)
        } catch (e: Exception) {
            Log.e("sendAlertMessages", "Error sending alert message", e)
        }
    }


    private fun sendSMS(phoneNumber: String, message: String) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "SMS permission not granted, skipping SMS to $phoneNumber")
            return
        }

        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent to $phoneNumber")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS to $phoneNumber: ${e.message}")
        }
    }

    private fun stopRingtoneAndDetection() {
        Log.d(TAG, "Stopping ringtone and shake detection")

        try {
            if (ringtone?.isPlaying == true) {
                ringtone?.stop()
                Log.d(TAG, "Ringtone stopped")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop ringtone: ${e.message}")
        }

        ringtone = null
        SharedPreference.setBoolean("FLAGRING", false)

        // Reset shake detection state
        isShaking = false
        shakeStartTime = 0
        shakeCheckHandler?.removeCallbacks(shakeRunnable!!)

        // 🔄 Re-register accelerometer cleanly
        stopShakeDetection()
        startShakeDetection()

        Log.d(TAG, "Shake detection restarted")
    }



    private fun registerShakeReceiver() {
        Log.d(TAG, "Registering cancel receiver")

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "CANCEL_CLICK") {
                    Log.d(TAG, "Cancel button clicked")
                    stopRingtoneAndDetection()
                }
            }
        }

        val filter = IntentFilter("CANCEL_CLICK")
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }



    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        Log.d(TAG, "Service destroyed")
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        stopShakeDetection()
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
