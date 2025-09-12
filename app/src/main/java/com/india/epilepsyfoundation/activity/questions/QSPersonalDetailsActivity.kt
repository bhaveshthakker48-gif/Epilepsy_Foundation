package com.india.epilepsyfoundation.activity.questions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityQspersonalDetailsBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.Calendar
import java.util.Locale

class QSPersonalDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQspersonalDetailsBinding
    private lateinit var phoneHintLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQspersonalDetailsBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.title_question_set)

        binding.toolbar.addView(customView)

        setupGenderSpinner()

        binding.edtDob.setOnClickListener {
            showDatePickerDialog()
        }

        phoneHintLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val phoneNumber = Identity.getSignInClient(this)
                    .getPhoneNumberFromIntent(result.data!!)
                phoneNumber?.let {
                    binding.edtMobile.setText(normalizeIndianMobile(it))
                }
            }
        }

        binding.edtMobile.setOnClickListener {
            requestPhoneNumberHint()
        }

        binding.btnSubmit.setOnClickListener {
            val firstName = binding.edtFirstName.text.toString().trim()
            val lastName = binding.edtLastName.text.toString().trim()
            val dobStr = binding.edtDob.text.toString().trim()
            val gender = binding.spnGender.selectedItem.toString()
            val mobileNumber = binding.edtMobile.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || dobStr.isEmpty() || gender == getString(R.string.gender_select) || mobileNumber.length != 10) {
                Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val age = calculateAgeFromDOB(dobStr)
            if (age < 2 || age > 9) {
                Toast.makeText(this, getString(R.string.age_validation), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val intent = Intent(this, QSQuestionsActivity::class.java).apply {
                putExtra("firstName", firstName)
                putExtra("lastName", lastName)
                putExtra("gender", gender)
                putExtra("dob", dobStr)
                putExtra("age", age)
                putExtra("mobileNumber", mobileNumber)
            }
            startActivity(intent)
        }

        val letterOnlyFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.isEmpty()) return@InputFilter null // allow backspace/delete

            val filtered = source.filter { it.isLetter() } // keep only letters
            if (filtered == source) null else filtered
        }

        binding.edtFirstName.filters = arrayOf(letterOnlyFilter)
        binding.edtLastName.filters = arrayOf(letterOnlyFilter)

        // Capitalize first letter on text change
        fun capitalizeFirstLetter(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty()) {
                        val capitalized = s[0].uppercaseChar() + s.substring(1)
                        if (capitalized != s.toString()) {
                            editText.setText(capitalized)
                            editText.setSelection(capitalized.length)
                        }
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        capitalizeFirstLetter(binding.edtFirstName)
        capitalizeFirstLetter(binding.edtLastName)

    }

    private fun calculateAgeFromDOB(dobStr: String): Int {
        val parts = dobStr.split("-")
        if (parts.size != 3) return -1

        val day = parts[0].toInt()
        val month = parts[1].toInt() - 1 // Calendar months are 0-based
        val year = parts[2].toInt()

        val dobCalendar = Calendar.getInstance().apply {
            set(year, month, day)
        }

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
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

    private fun setupGenderSpinner() {
        val genderOptions = listOf(
            getString(R.string.gender_select),
            getString(R.string.gender_male),
            getString(R.string.gender_female),
            getString(R.string.gender_other)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)
        binding.spnGender.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val dob = String.format("%02d-%02d-%04d", selectedDayOfMonth, selectedMonth + 1, selectedYear)
                binding.edtDob.setText(dob)
            },
            year,
            month,
            day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun requestPhoneNumberHint() {
        val request = GetPhoneNumberHintIntentRequest.builder().build()
        val client = Identity.getSignInClient(this)

        client.getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result ->
                val intentSenderRequest = IntentSenderRequest.Builder(result.intentSender).build()
                phoneHintLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener {
                Log.e("PhoneHint", "Phone number not found: ${it.localizedMessage}")
                Toast.makeText(this, "SIM phone number not available. Please enter manually.", Toast.LENGTH_LONG).show()
                // Allow manual entry
                binding.edtMobile.isEnabled = true
            }
    }



    private fun normalizeIndianMobile(number: String?): String {
        return number?.filter { it.isDigit() }?.takeLast(10) ?: ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, QuestionnaireActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}