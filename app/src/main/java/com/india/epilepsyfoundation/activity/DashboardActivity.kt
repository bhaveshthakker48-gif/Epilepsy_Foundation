package com.india.epilepsyfoundation.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.activity.assessment.AssessmentActivity
import com.india.epilepsyfoundation.activity.epdetector.EpDetectorActivity
import com.india.epilepsyfoundation.activity.epilepsyDairy.EpilepsyDairyActivity
import com.india.epilepsyfoundation.activity.information.InformationActivity
import com.india.epilepsyfoundation.activity.questions.QuestionnaireActivity
import com.india.epilepsyfoundation.activity.reminder.ReminderActivity
import com.india.epilepsyfoundation.databinding.ActivityDashboardBinding
import com.india.epilepsyfoundation.utils.PrefKeys
import com.india.epilepsyfoundation.utils.SharedPreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(LayoutInflater.from(this))
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


        binding.btnAssesment.setOnClickListener {
            gotoScreen(AssessmentActivity::class.java)
        }

        binding.btnReminder.setOnClickListener {
            gotoScreen(ReminderActivity::class.java)
        }

        binding.btnQuestionnaire.setOnClickListener {
            gotoScreen(QuestionnaireActivity::class.java)
        }

        binding.btnInfo.setOnClickListener {
            gotoScreen(InformationActivity::class.java)
        }

        binding.btnSetting.setOnClickListener {
            gotoScreen(SettingActivity::class.java)
        }

        binding.btnEpdetector.setOnClickListener {
            gotoScreen(EpDetectorActivity::class.java)
        }

        binding.btnDairy.setOnClickListener {
            gotoScreen(EpilepsyDairyActivity::class.java)
        }

        binding.message.setOnClickListener {
            gotoScreen(AllMessagesActivity::class.java)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        val version = getAppVersion(this)
        binding.versionName.text = "Version $version"

        checkForAppUpdate()
        checkWhatsNew(this)

    }

    private fun checkForAppUpdate() {
        val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask: Task<AppUpdateInfo> = appUpdateManager.getAppUpdateInfo()
        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener<AppUpdateInfo> { appUpdateInfo: AppUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE) {
                showUpdateDialog()
            }
        })
    }


    private fun showUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle("Update Available")
            .setMessage("A new version of this app is available. Please update to continue.")
            .setCancelable(false) // force them to choose
            .setPositiveButton(
                "Update"
            ) { dialog: DialogInterface?, which: Int -> openPlayStoreForUpdate() }
            .setNegativeButton(
                "Later"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
    }

    private fun openPlayStoreForUpdate() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun checkWhatsNew(context: Context) {
        val currentVersion = getAppVersion(context)

        val lastVersion: String? = SharedPreference.get(PrefKeys.LAST_SEEN_VERSION)
        if (lastVersion == null || currentVersion != lastVersion) {
            // Show What's New dialog
            showWhatsNewDialog()

            // Save current version
            SharedPreference.set(PrefKeys.LAST_SEEN_VERSION, currentVersion)
        }
    }


    private fun showWhatsNewDialog() {
        try {
            // Get version name from PackageManager
            val versionName = packageManager
                .getPackageInfo(packageName, 0).versionName

            // Build and show dialog
            AlertDialog.Builder(this)
                .setTitle("What's New in version $versionName")
                .setMessage(
                    """
                ✨ Latest Updates:
                
                • Faster performance
                • Bug fixes
                • New dashboard UI
                
                """.trimIndent()
                )
                .setPositiveButton("Got it", null)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getAppVersion(context: Context): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "0.0"
        } catch (e: Exception) {
            "0.0"
        }
    }

    private fun gotoScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }
}
