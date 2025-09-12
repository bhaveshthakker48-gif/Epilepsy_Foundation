package com.india.epilepsyfoundation.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.messaging.FirebaseMessaging
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityRegisterBinding
import com.india.epilepsyfoundation.entity.RegisterEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.RegisterViewmodel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewmodel by viewModels()
    private lateinit var phoneHintLauncher: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.app_names)

        binding.toolbar.addView(customView)


        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                // Log or use the token
                Log.d("FCM", "FCM Token: $token")
            }


        setupLanguageSpinner()
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
            saveRegisterDataLocally()
        }

        if (!SharedPreference.isPrivacyAccepted()) {
            showPrivacyDialog()
            return
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

    private fun showPrivacyDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val webView = dialogView.findViewById<WebView>(R.id.webViewPrivacy)
        val checkBox = dialogView.findViewById<CheckBox>(R.id.checkboxAccept)
        val btnAccept = dialogView.findViewById<TextView>(R.id.btnAccept)
        val btnCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        webView.settings.javaScriptEnabled = true
        webView.loadUrl("file:///android_asset/privacy_policy.html")

        btnAccept.setOnClickListener {
            if (checkBox.isChecked) {
                SharedPreference.setPrivacyAccepted(true)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please accept the privacy policy", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel Button Click
        btnCancel.setOnClickListener {
            dialog.dismiss()
            finishAffinity() // Close the app
        }

        dialog.show()
    }



    private fun setupLanguageSpinner() {
        val languages = listOf("English", "हिन्दी", "मराठी")
        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, languages)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        binding.spnLang.adapter = adapter

        when (getCurrentLanguage()) {
            "en" -> binding.spnLang.setSelection(0)
            "hi" -> binding.spnLang.setSelection(1)
            "mr" -> binding.spnLang.setSelection(2)
        }

        binding.spnLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

        val adapter = ArrayAdapter(this, R.layout.spinner_selected_item, genderOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

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



    private fun saveRegisterDataLocally() {
        val firstName = binding.edtFirstName.text.toString().trim()
        val lastName = binding.edtLastName.text.toString().trim()
        val dob = binding.edtDob.text.toString().trim()
        val gender = binding.spnGender.selectedItem.toString()
        val mobile = binding.edtMobile.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()

        val defaultGender = getString(R.string.gender_select)

        when {
            firstName.isEmpty() -> {
                showToast(getString(R.string.first_name) + " " + getString(R.string.is_required))
                return
            }
            lastName.isEmpty() -> {
                showToast(getString(R.string.last_name) + " " + getString(R.string.is_required))
                return
            }
            dob.isEmpty() -> {
                showToast(getString(R.string.dob) + " " + getString(R.string.is_required))
                return
            }
            gender == defaultGender -> {
                showToast(getString(R.string.gender) + " " + getString(R.string.is_required))
                return
            }
            mobile.isEmpty() -> {
                showToast(getString(R.string.mobile_no) + " " + getString(R.string.is_required))
                return
            }
            mobile.length < 10 -> {
                showToast(getString(R.string.invalid_mobile_number))
                return
            }
            email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast(getString(R.string.invalid_email))
                return
            }
        }

        val entity = RegisterEntity(
            firstName = firstName,
            lastName = lastName,
            dateOfBirth = dob,
            gender = gender,
            mobileNumber = mobile,
            email = email
        )

        viewModel.saveRegisterData(entity)
        SharedPreference.setBoolean("is_registered", true)
        showToast(getString(R.string.submit) + " " + getString(R.string.successful))
        startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}
