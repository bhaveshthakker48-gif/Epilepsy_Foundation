package com.india.epilepsyfoundation.activity.epdetector

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityEpDetectorWarningBinding
import com.india.epilepsyfoundation.service.EpDetectorService
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.Locale

class EpDetectorWarningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEpDetectorWarningBinding
    private val requiredPermissions = arrayOf(
        android.Manifest.permission.ACTIVITY_RECOGNITION,
        android.Manifest.permission.BODY_SENSORS,
        android.Manifest.permission.FOREGROUND_SERVICE_HEALTH,
        android.Manifest.permission.SEND_SMS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityEpDetectorWarningBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars =
            true
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


        SharedPreference.init(this)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.toolbar_with_logo, binding.toolbar, false)

        val titleTextView = customView.findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = getString(R.string.title_epdetector)

        binding.toolbar.addView(customView)
        registerUIReceiver()
        setupUIState()
        setupListeners()
    }

    private fun setupUIState() {
        val isRinging = SharedPreference.getBoolean("FLAGRING", false)
        val isActive = SharedPreference.getBoolean("FLAGACTIVE", false)

        when {
            isRinging -> {
                Log.d("pawan", "Showing cancel UI")
                showCancelUI()
            }

            isActive -> {
                Log.d("pawan", "Showing deactivate UI")
                showDeactivateUI()
            }

            else -> {
                Log.d("pawan", "Showing activate UI")
                showActivateUI()
            }
        }

    }


    private fun setupListeners() {
        // ✅ Activate Button
        binding.activateBtn.setOnClickListener {
            showDeactivateUI() // Update UI
            checkAndRequestPermissions()
        }

        // 🛑 Deactivate Button
        binding.diactivateBtn.setOnClickListener {
            showActivateUI()
            SharedPreference.setBoolean("FLAGACTIVE", false)
            SharedPreference.remove("FLAGRING")

            val stopIntent = Intent(this, EpDetectorService::class.java)
            stopService(stopIntent)
        }

        // 🚨 Cancel Button
        binding.cancelBtn.setOnClickListener {
            Log.d("pawan", "cancel button clicked activity")

            val cancelIntent = Intent("CANCEL_CLICK")
            LocalBroadcastManager.getInstance(this).sendBroadcast(cancelIntent)

            SharedPreference.setBoolean("FLAGRING", false)
            showDeactivateUI()
        }

    }


    private fun registerUIReceiver() {
        val filter = IntentFilter("SHOW_CANCEL_UI")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(cancelUIReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(cancelUIReceiver, filter)
        }
    }


    private val cancelUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("pawan", "Received SHOW_CANCEL_UI")
            SharedPreference.setBoolean("FLAGRING", true)
            showCancelUI()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("pawan", "onResume - FLAGRING: ${SharedPreference.getBoolean("FLAGRING", false)}")

        if (SharedPreference.getBoolean("FLAGRING", false)) {
            showCancelUI()
        } else if (SharedPreference.getBoolean("FLAGACTIVE", false)) {
            showDeactivateUI()

            // ✅ Ensure service is running
            if (!isServiceRunning(EpDetectorService::class.java)) {
                Log.d("pawan", "Service not running, starting again")
                startEpDetectorService()
            }
        } else {
            showActivateUI()
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun checkAndRequestPermissions() {
        val missing = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 101)
        } else {
            startEpDetectorService()
        }
    }

    private fun startEpDetectorService() {
        val serviceIntent = Intent(this, EpDetectorService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        SharedPreference.setBoolean("FLAGACTIVE", true)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            startEpDetectorService()
        } else {
            Toast.makeText(this, "Required permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showActivateUI() {
        Log.d("pawan", "Showing activate UI")
        binding.activate.visibility = View.VISIBLE
        binding.diactivate.visibility = View.GONE
        binding.cancel.visibility = View.GONE
    }

    private fun showDeactivateUI() {
        Log.d("pawan", "Showing deactivate UI")
        binding.activate.visibility = View.GONE
        binding.diactivate.visibility = View.VISIBLE
        binding.cancel.visibility = View.GONE
    }

    private fun showCancelUI() {
        Log.d("pawan", "Showing cancel UI")
        binding.activate.visibility = View.GONE
        binding.diactivate.visibility = View.GONE
        binding.cancel.visibility = View.VISIBLE
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
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
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
        val intent = Intent(this, EpDetectorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
