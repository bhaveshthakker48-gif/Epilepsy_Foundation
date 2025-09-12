package com.india.epilepsyfoundation.activity.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.adapter.RadiologyTestReminderAdapter
import com.india.epilepsyfoundation.databinding.ActivityRadiologyTestReminderBinding
import com.india.epilepsyfoundation.entity.RadiologyTestReminderEntity
import com.india.epilepsyfoundation.utils.RadiologyTestReminderUtils
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.ReminderViewmodel
import java.util.Calendar
import java.util.Locale

class RadiologyTestReminderActivity : BaseActivity() {

    private lateinit var binding: ActivityRadiologyTestReminderBinding
    private val viewModel: ReminderViewmodel by viewModels()
    private lateinit var adapter: RadiologyTestReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityRadiologyTestReminderBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.title_radiology_test)

        binding.toolbar.addView(customView)


        adapter = RadiologyTestReminderAdapter(
            emptyList(),
            onDeleteClick = { visit -> showDeleteConfirmationDialog(visit) },
            onEditClick = { visit -> showEditDialog(visit) }
        )
        binding.radiologyRecycleView.adapter = adapter
        binding.radiologyRecycleView.layoutManager = LinearLayoutManager(this)

        fetchDoctorVisits()
    }

    private fun showEditDialog(visit: RadiologyTestReminderEntity) {
        val dialogBinding = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pathology_reminder, null)

        val doctorNameInput = dialogBinding.findViewById<EditText>(R.id.etTestName)
        val visitDateInput = dialogBinding.findViewById<EditText>(R.id.etVisitDate)
        val visitTimeInput = dialogBinding.findViewById<EditText>(R.id.etVisitTime)
        val labNameInput = dialogBinding.findViewById<EditText>(R.id.etLabName)
        val reminderDateInput = dialogBinding.findViewById<EditText>(R.id.etReminderDate)
        val reminderTimeInput = dialogBinding.findViewById<EditText>(R.id.etReminderTime)


        doctorNameInput.setText(visit.testName)
        visitDateInput.setText(visit.visitDate)
        visitTimeInput.setText(visit.visitTime)
        labNameInput.setText(visit.labName)
        reminderDateInput.setText(visit.reminderDate)
        reminderTimeInput.setText(visit.reminderTime)

        visitDateInput.inputType = InputType.TYPE_NULL
        visitTimeInput.inputType = InputType.TYPE_NULL
        reminderDateInput.inputType = InputType.TYPE_NULL
        reminderTimeInput.inputType = InputType.TYPE_NULL

        visitDateInput.setOnClickListener {
            showDatePickerDialog(visitDateInput)
        }

        reminderDateInput.setOnClickListener {
            showDatePickerDialog(reminderDateInput)
        }

        visitTimeInput.setOnClickListener {
            showTimePickerDialog(visitTimeInput)
        }

        reminderTimeInput.setOnClickListener {
            showTimePickerDialog(reminderTimeInput)
        }


        val customTitle = TextView(this).apply {
            text = getString(R.string.edit_reminder)
            setPadding(32, 32, 32, 16) // optional
            textSize = 20f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK) // set your desired color
        }

        val alertDialog = AlertDialog.Builder(this)
            .setCustomTitle(customTitle)
            .setView(dialogBinding)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val updatedVisit = visit.copy(
                    testName = doctorNameInput.text.toString(),
                    visitDate = visitDateInput.text.toString(),
                    visitTime = visitTimeInput.text.toString(),
                    labName = labNameInput.text.toString(),
                    reminderDate = reminderDateInput.text.toString(),
                    reminderTime = reminderTimeInput.text.toString(),
                )
                viewModel.updateRadiologyTestData(updatedVisit) {
                    fetchDoctorVisits()
                    RadiologyTestReminderUtils.scheduleRadiologyAlarms(this, updatedVisit)
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        alertDialog.show()
    }



    private fun showDeleteConfirmationDialog(visit: RadiologyTestReminderEntity) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation_title))
            .setMessage(getString(R.string.delete_confirmation_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteRadiologyTestReminder(visit) {
                    fetchDoctorVisits()
                }
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun fetchDoctorVisits() {
        viewModel.getAllRadiologyTestReminder { visits ->
            if (visits.isEmpty()) {
                binding.txtNoData.visibility = View.VISIBLE
                binding.radiologyRecycleView.visibility = View.GONE
            } else {
                binding.txtNoData.visibility = View.GONE
                binding.radiologyRecycleView.visibility = View.VISIBLE
                adapter.updateData(visits.reversed())
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ReminderActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}