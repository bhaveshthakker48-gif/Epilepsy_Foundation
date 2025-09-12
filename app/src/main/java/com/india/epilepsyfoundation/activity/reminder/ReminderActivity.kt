package com.india.epilepsyfoundation.activity.reminder

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityReminderBinding
import com.india.epilepsyfoundation.entity.DoctorVisitReminderEntity
import com.india.epilepsyfoundation.entity.MedicationReminderEntity
import com.india.epilepsyfoundation.entity.PathologyTestReminderEntity
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.ReminderViewmodel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.content.pm.PackageManager
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.activity.DashboardActivity
import com.india.epilepsyfoundation.utils.DoctorVisitReminderUtils
import com.india.epilepsyfoundation.utils.MedicationReminderUtils
import com.india.epilepsyfoundation.utils.PathologyTestReminderUtils
import com.india.epilepsyfoundation.utils.RadiologyTestReminderUtils


class ReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityReminderBinding
    private val viewModel: ReminderViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(LayoutInflater.from(this))
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


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.toolbar_with_logo, binding.toolbar, false)

        val titleTextView = customView.findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = getString(R.string.title_reminder)

        binding.toolbar.addView(customView)


        binding.lblMedication.setOnClickListener {
            if (binding.lblMediContainer.getVisibility() === View.GONE) {
                binding.lblMediContainer.visibility = View.VISIBLE
            } else {
                binding.lblMediContainer.visibility = View.GONE
            }
        }

        binding.lblNextVisit.setOnClickListener {
            if (binding.lblVisitContainer.getVisibility() === View.GONE) {
                binding.lblVisitContainer.visibility = View.VISIBLE
            } else {
                binding.lblVisitContainer.visibility = View.GONE
            }
        }

        binding.lblPathologyTest.setOnClickListener {
            if (binding.lblPathologyContainer.getVisibility() === View.GONE) {
                binding.lblPathologyContainer.visibility = View.VISIBLE
            } else {
                binding.lblPathologyContainer.visibility = View.GONE
            }
        }

        binding.lblRadiologyTest.setOnClickListener {
            if (binding.lblRadiologyContainer.getVisibility() === View.GONE) {
                binding.lblRadiologyContainer.visibility = View.VISIBLE
            } else {
                binding.lblRadiologyContainer.visibility = View.GONE
            }
        }

        setupDaySpinner()
        setUpFrequencySpinner()
        dateTimeInit()

        binding.btnSubmit.setOnClickListener {
            saveMedicationReminder()
        }

        binding.btnVisitSubmit.setOnClickListener {
            saveDoctorVisitReminder()
        }

        binding.btnPathologySubmit.setOnClickListener {
            savePathologyTestReminder()
        }

        binding.btnRadiologySubmit.setOnClickListener {
            saveRadiologyTestReminder()
        }

        binding.btnViewMedic.setOnClickListener {
            gotoScreen(MedicationReminderActivity::class.java)
        }

        binding.btnViewNextVisit.setOnClickListener {
            gotoScreen(DoctorVisitReminderActivity::class.java)
        }

        binding.btnViewPathTest.setOnClickListener {
            gotoScreen(PathologyTestReminderActivity::class.java)
        }

        binding.btnViewRadTest.setOnClickListener {
            gotoScreen(RadiologyTestReminderActivity::class.java)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("DoctorVisitAlarmReceiver", "Exact alarm permission not granted. Requesting user to enable.")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
                return
            }
        }

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
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
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

    private fun saveMedicationReminder() {
        val medicationName = binding.autoTextMedicationName.text.toString().trim()
        val startDate = binding.txtStartDate.text.toString().trim()
        val startTime = binding.txtStartTime.text.toString().trim()
        val duration = binding.txtDuration.text.toString().trim()
        val dose = binding.txtDose.text.toString().trim()
        val durationUnit = binding.spnDurationUnit.selectedItem.toString()
        val frequency = binding.spnFrequency.selectedItem.toString()

        if (medicationName.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_medication_name_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (startDate.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_start_date_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (startTime.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_start_time_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (duration.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_duration_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (durationUnit == getString(R.string.select_unit)) {
            Toast.makeText(this, getString(R.string.toast_duration_unit_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (frequency == getString(R.string.select_frequency)) {
            Toast.makeText(this, getString(R.string.toast_frequency_empty), Toast.LENGTH_SHORT).show()
            return
        }

        if (dose.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_dose_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val medicationReminderEntity = MedicationReminderEntity(
            medicationName = medicationName,
            startDate = startDate,
            startTime = startTime,
            duration = duration,
            durationUnit = durationUnit,
            frequency = frequency,
            dose = dose
        )
        Toast.makeText(this, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT).show()
        clearMedicationFields()
        Log.d("pawan", "aaaaaa ${medicationReminderEntity}")
        viewModel.saveReminderMedicationData(medicationReminderEntity){ newId ->
            val updatedVisit = medicationReminderEntity.copy(id = newId)
            MedicationReminderUtils.scheduleMedicationAlarms(this, updatedVisit)
        }
    }

    private fun savePathologyTestReminder() {
        if (binding.txtPathTestName.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_test_name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtPathEventDate.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_date_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtPathEventTime.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_time_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtPathLabName.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_lab_name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtPathRemDate.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_reminder_date_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtPathRemTime.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_pathology_reminder_time_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())

        val reminderDateTimeStr = "${binding.txtPathRemDate.text} ${binding.txtPathRemTime.text}"
        val visitDateTimeStr = "${binding.txtPathEventDate.text} ${binding.txtPathEventTime.text}"

        val reminderCalendar = Calendar.getInstance()
        val visitCalendar = Calendar.getInstance()

        try {
            val reminderDateTime = dateTimeFormat.parse(reminderDateTimeStr)
            val visitDateTime = dateTimeFormat.parse(visitDateTimeStr)

            if (reminderDateTime == null || visitDateTime == null) {
                Toast.makeText(this, getString(R.string.toast_invalid_datetime_format), Toast.LENGTH_SHORT).show()
                return
            }

            reminderCalendar.time = reminderDateTime
            visitCalendar.time = visitDateTime

            val now = Calendar.getInstance()

            if (reminderCalendar.before(now)) {
                Toast.makeText(this, getString(R.string.toast_reminder_future_required), Toast.LENGTH_SHORT).show()
                return
            }

            if (!visitCalendar.after(reminderCalendar)) {
                Toast.makeText(this, getString(R.string.toast_visit_after_reminder_required), Toast.LENGTH_SHORT).show()
                return
            }

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.toast_invalid_datetime_format), Toast.LENGTH_SHORT).show()
            return
        }

        val pathologyTestReminderEntity = PathologyTestReminderEntity(
            testName = binding.txtPathTestName.text.toString(),
            visitDate = binding.txtPathEventDate.text.toString(),
            visitTime = binding.txtPathEventTime.text.toString(),
            labName = binding.txtPathLabName.text.toString(),
            reminderDate = binding.txtPathRemDate.text.toString(),
            reminderTime = binding.txtPathRemTime.text.toString(),
        )

        Toast.makeText(this, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT).show()
        clearPathologyTestFields()

        viewModel.saveReminderPathologyTestData(pathologyTestReminderEntity) { newId ->
            val updatedVisit = pathologyTestReminderEntity.copy(id = newId)
            PathologyTestReminderUtils.schedulePathologyAlarms(this, updatedVisit)
        }
    }

    private fun saveRadiologyTestReminder() {
        if (binding.txtRadTestName.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_test_name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtRadEventDate.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_date_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtRadEventTime.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_time_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtRadLabName.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_lab_name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtRadRemDate.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_reminder_date_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtRadRemTime.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_radiology_reminder_time_empty), Toast.LENGTH_SHORT).show()
            return
        }

        val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())

        val reminderDateTimeStr = "${binding.txtRadRemDate.text} ${binding.txtRadRemTime.text}"
        val visitDateTimeStr = "${binding.txtRadEventDate.text} ${binding.txtRadEventTime.text}"

        val reminderCalendar = Calendar.getInstance()
        val visitCalendar = Calendar.getInstance()

        try {
            val reminderDateTime = dateTimeFormat.parse(reminderDateTimeStr)
            val visitDateTime = dateTimeFormat.parse(visitDateTimeStr)

            if (reminderDateTime == null || visitDateTime == null) {
                Toast.makeText(this, getString(R.string.toast_invalid_datetime_format), Toast.LENGTH_SHORT).show()
                return
            }

            reminderCalendar.time = reminderDateTime
            visitCalendar.time = visitDateTime

            val now = Calendar.getInstance()

            if (reminderCalendar.before(now)) {
                Toast.makeText(this, getString(R.string.toast_reminder_future_required), Toast.LENGTH_SHORT).show()
                return
            }

            if (!visitCalendar.after(reminderCalendar)) {
                Toast.makeText(this, getString(R.string.toast_visit_after_reminder_required), Toast.LENGTH_SHORT).show()
                return
            }

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.toast_invalid_datetime_format), Toast.LENGTH_SHORT).show()
            return
        }

        val radiologyTestReminderEntity = RadiologyTestReminderEntity(
            testName = binding.txtRadTestName.text.toString(),
            visitDate = binding.txtRadEventDate.text.toString(),
            visitTime = binding.txtRadEventTime.text.toString(),
            labName = binding.txtRadLabName.text.toString(),
            reminderDate = binding.txtRadRemDate.text.toString(),
            reminderTime = binding.txtRadRemTime.text.toString(),
        )

        Toast.makeText(this, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT).show()
        clearRadiologyTestFields()
        viewModel.saveReminderRadiologyTestData(radiologyTestReminderEntity) { newId ->
            val updatedVisit = radiologyTestReminderEntity.copy(id = newId)
            RadiologyTestReminderUtils.scheduleRadiologyAlarms(this, updatedVisit)
        }
    }

    private fun saveDoctorVisitReminder() {
        if (binding.txtVisitDoc.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_doctor_name_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.autoTextVisiteventDate.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_visit_date_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.txtVisitEventTime.text.toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_visit_time_empty), Toast.LENGTH_SHORT).show()
            return
        }
        if (!binding.chkReminder.isChecked) {
            Toast.makeText(this, getString(R.string.toast_enable_reminder_checkbox), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDateStr = binding.autoTextVisiteventDate.text.toString()
        val selectedTimeStr = binding.txtVisitEventTime.text.toString()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault())

        val selectedDateTime = try {
            dateFormat.parse("$selectedDateStr $selectedTimeStr")
        } catch (e: Exception) {
            null
        }

        if (selectedDateTime == null) {
            Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show()
            return
        }

        val calendarNow = Calendar.getInstance()
        val minimumAllowed = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }

        if (selectedDateTime.before(minimumAllowed.time)) {
            Toast.makeText(this, "Please select a visit date at least 1 day in the future", Toast.LENGTH_LONG).show()
            return
        }

        val doctorVisitReminderEntity = DoctorVisitReminderEntity(
            doctorName = binding.txtVisitDoc.text.toString(),
            doctorVisitDate = selectedDateStr,
            doctorVisitTime = selectedTimeStr,
            reminder = binding.chkReminder.isChecked
        )

        Toast.makeText(this, getString(R.string.toast_reminder_saved), Toast.LENGTH_SHORT).show()
        clearDoctorVisitFields()
        viewModel.saveReminderDoctorVisitData(doctorVisitReminderEntity) { newId ->
            val updatedVisit = doctorVisitReminderEntity.copy(id = newId)
            DoctorVisitReminderUtils.scheduleDoctorAlarms(this, updatedVisit)
        }
    }

    // Select date and time
    private fun dateTimeInit(){
        binding.txtStartDate.setOnClickListener {
            showDatePickerDialog(binding.txtStartDate)
        }

        binding.imgStartDatePic.setOnClickListener {
            showDatePickerDialog(binding.txtStartDate)
        }

        binding.txtStartTime.setOnClickListener {
            showTimePickerDialog(binding.txtStartTime)
        }

        binding.imgEventTimePick.setOnClickListener {
            showTimePickerDialog(binding.txtStartTime)
        }

        binding.autoTextVisiteventDate.setOnClickListener {
            showDatePickerDialog(binding.autoTextVisiteventDate)
        }

        binding.imgVisitEventDatePick.setOnClickListener {
            showDatePickerDialog(binding.autoTextVisiteventDate)
        }

        binding.txtVisitEventTime.setOnClickListener {
            showTimePickerDialog(binding.txtVisitEventTime)
        }

        binding.imgVisitEventTimePick.setOnClickListener {
            showTimePickerDialog(binding.txtVisitEventTime)
        }

        binding.txtPathEventDate.setOnClickListener {
            showDatePickerDialog(binding.txtPathEventDate)
        }

        binding.imgPathEventDatePick.setOnClickListener {
            showDatePickerDialog(binding.txtPathEventDate)
        }

        binding.txtPathEventTime.setOnClickListener {
            showTimePickerDialog(binding.txtPathEventTime)
        }

        binding.imgPathEventTimePick.setOnClickListener {
            showTimePickerDialog(binding.txtPathEventTime)
        }

        binding.txtPathRemDate.setOnClickListener {
            showDatePickerDialog(binding.txtPathRemDate)
        }

        binding.imgPathRemDatePicker.setOnClickListener {
            showDatePickerDialog(binding.txtPathRemDate)
        }

        binding.txtPathRemTime.setOnClickListener {
            showTimePickerDialog(binding.txtPathRemTime)
        }

        binding.imgPathRemTimePicker.setOnClickListener {
            showTimePickerDialog(binding.txtPathRemTime)
        }

        binding.txtRadEventDate.setOnClickListener {
            showDatePickerDialog(binding.txtRadEventDate)
        }

        binding.imgRadEventDatePick.setOnClickListener {
            showDatePickerDialog(binding.txtRadEventDate)
        }

        binding.txtRadEventTime.setOnClickListener {
            showTimePickerDialog(binding.txtRadEventTime)
        }

        binding.imgRadEventTimePick.setOnClickListener {
            showTimePickerDialog(binding.txtRadEventTime)
        }

        binding.txtRadRemDate.setOnClickListener {
            showDatePickerDialog(binding.txtRadRemDate)
        }

        binding.imgRadRemDatePicker.setOnClickListener {
            showDatePickerDialog(binding.txtRadRemDate)
        }

        binding.txtRadRemTime.setOnClickListener {
            showTimePickerDialog(binding.txtRadRemTime)
        }

        binding.imgRadRemTimePicker.setOnClickListener {
            showTimePickerDialog(binding.txtRadRemTime)
        }
    }


    //Date Picker Dailogue
    private fun showDatePickerDialog(targetView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                targetView.text = selectedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    //Time Picker Dailogue
    private fun showTimePickerDialog(targetEditText: EditText) {
        val dialogView = layoutInflater.inflate(R.layout.custom_time_picker_dialog, null)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.minutePicker)
        val ampmPicker = dialogView.findViewById<NumberPicker>(R.id.ampmPicker)

        val calendar = Calendar.getInstance()
        var currentHour = calendar.get(Calendar.HOUR)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val isAM = calendar.get(Calendar.AM_PM) == Calendar.AM

        // Hour picker: 1 to 12
        hourPicker.minValue = 1
        hourPicker.maxValue = 12
        hourPicker.value = if (currentHour == 0) 12 else currentHour
        hourPicker.wrapSelectorWheel = true

        // Minute picker: 0 to 59
        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = currentMinute
        minutePicker.wrapSelectorWheel = true

        // AM/PM picker
        val ampmValues = arrayOf("AM", "PM")
        ampmPicker.minValue = 0
        ampmPicker.maxValue = 1
        ampmPicker.displayedValues = ampmValues
        ampmPicker.value = if (isAM) 0 else 1
        ampmPicker.wrapSelectorWheel = false

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Select Time")
            .setPositiveButton("OK") { _, _ ->
                val hour = hourPicker.value
                val minute = minutePicker.value
                val ampm = ampmValues[ampmPicker.value]
                val time = String.format("%02d:%02d %s", hour, minute, ampm)
                targetEditText.setText(time)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    //Duration Spinner
    private fun setupDaySpinner() {
        val dayOptions = listOf(
            getString(R.string.select_unit),
            getString(R.string.days),
            getString(R.string.weeks),
            getString(R.string.months),
            getString(R.string.years)
        )

        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, dayOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnDurationUnit.adapter = adapter
    }

    //Frequency Spinner
    private fun setUpFrequencySpinner() {
        val frequencyOptions = listOf(
            getString(R.string.select_frequency),
            getString(R.string.one),
            getString(R.string.two),
            getString(R.string.three),
            getString(R.string.four)
        )

        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, frequencyOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnFrequency.adapter = adapter
    }



    private fun gotoScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    //clear all fields
    private fun clearDoctorVisitFields() {
        binding.txtVisitDoc.text.clear()
        binding.autoTextVisiteventDate.setText("")
        binding.txtVisitEventTime.setText("")
        binding.chkReminder.isChecked = false
    }

    private fun clearMedicationFields(){
        binding.autoTextMedicationName.text.clear()
        binding.txtStartDate.setText("")
        binding.txtStartTime.setText("")
        binding.txtDuration.text.clear()
        binding.txtDose.text.clear()
        binding.spnDurationUnit.setSelection(0)
        binding.spnFrequency.setSelection(0)
    }

    private fun clearPathologyTestFields(){
        binding.txtPathTestName.setText("")
        binding.txtPathEventDate.setText("")
        binding.txtPathEventTime.setText("")
        binding.txtPathLabName.setText("")
        binding.txtPathRemDate.setText("")
        binding.txtPathRemTime.setText("")
    }

    private fun clearRadiologyTestFields(){
        binding.txtRadTestName.setText("")
        binding.txtRadEventDate.setText("")
        binding.txtRadEventTime.setText("")
        binding.txtRadLabName.setText("")
        binding.txtRadRemDate.setText("")
        binding.txtRadRemTime.setText("")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

}