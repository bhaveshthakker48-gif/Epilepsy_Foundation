package com.india.epilepsyfoundation.activity.epilepsyDairy

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityEpilepsyDairyQuestionsBinding
import com.india.epilepsyfoundation.entity.AttackDetailsEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AttackDetailsViewModel
import java.util.Calendar
import java.util.Locale

class EpilepsyDairyQuestionsActivity : BaseActivity() {

    private lateinit var binding: ActivityEpilepsyDairyQuestionsBinding
    private lateinit var attackTypes: Array<String>
    private val viewModel: AttackDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityEpilepsyDairyQuestionsBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.title_epilepsy_details)

        attackTypes = resources.getStringArray(R.array.attack_types)

        binding.toolbar.addView(customView)
        binding.btnSubmit.setOnClickListener {
            saveAttackDetails()
        }

        binding.edtDateOfAttack.setOnClickListener {
            showDatePickerDialog(binding.edtDateOfAttack)
        }

        binding.edtTimeOfAttack.setOnClickListener {
            showTimePickerDialog(binding.edtTimeOfAttack)
        }

        setUpAttackSpinner()

        viewModel.saveStatus.observe(this){ isSaved ->
            if (isSaved) {
                Toast.makeText(
                    this,
                    getString(R.string.form_submit_success),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, EpilepsyDairyActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.form_submit_failed), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun saveAttackDetails() {
        val dateOfAttack = binding.edtDateOfAttack.text.toString().trim()
        val timeOfAttack = binding.edtTimeOfAttack.text.toString().trim()
        val min = binding.edtMin.text.toString().trim()
        val sec = binding.edtSec.text.toString().trim()

        if (dateOfAttack.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_date_required), Toast.LENGTH_SHORT).show()
            return
        }

        if (timeOfAttack.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_time_required), Toast.LENGTH_SHORT).show()
            return
        }

        if (min.isEmpty() && sec.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_duration_required), Toast.LENGTH_SHORT).show()
            return
        }

        val minValue = min.toIntOrNull() ?: 0
        val secValue = sec.toIntOrNull() ?: 0

        if (minValue > 60) {
            Toast.makeText(this, getString(R.string.error_max_minute), Toast.LENGTH_SHORT).show()
            return
        }

        if (secValue > 60) {
            Toast.makeText(this, getString(R.string.error_max_second), Toast.LENGTH_SHORT).show()
            return
        }

        val duration = "$minValue min $secValue sec"
        val typeOfAttack = binding.spnTypesOfAttack.selectedItem.toString()
        val detailsOfAttack = binding.edtAttackDetails.text.toString().trim()

        val attackDetailsEntity = AttackDetailsEntity(
            dateOfAttack = dateOfAttack,
            timeOfAttack = timeOfAttack,
            duration = duration,
            typeOfAttack = typeOfAttack,
            detailsOfAttack = detailsOfAttack
        )

        viewModel.saveAttackDetailsData(attackDetailsEntity)
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

    private fun setUpAttackSpinner(){
        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, attackTypes)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spnTypesOfAttack.adapter = adapter
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, EpilepsyDairyActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}