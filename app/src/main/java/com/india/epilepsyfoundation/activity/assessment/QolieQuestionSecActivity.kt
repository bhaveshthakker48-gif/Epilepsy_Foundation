package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import android.os.Bundle
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
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityQolieQuestionSecBinding
import com.india.epilepsyfoundation.entity.QolieQuestionEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AssessmentViewmodel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QolieQuestionSecActivity : BaseActivity() {

    private lateinit var binding: ActivityQolieQuestionSecBinding
    private lateinit var rankFields: List<EditText>
    private val viewModel: AssessmentViewmodel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityQolieQuestionSecBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.Qoliftitle)

        binding.toolbar.addView(customView)
        rankFields = listOf(
            binding.edtoptionA,
            binding.edtoptionB,
            binding.edtoptionC,
            binding.edtoptionD,
            binding.edtoptionE,
            binding.edtoptionF,
            binding.edtoptionG
        )

        binding.btnSubmitFormQolie.setOnClickListener {
            if (validateRanks()) {
                saveQulieQuestionsData()
            }
        }

        viewModel.saveStatus.observe(this) { isSaved ->
            if (isSaved) {
                Toast.makeText(
                    this,
                    getString(R.string.form_submit_success),
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, AssessmentActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.form_submit_failed), Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun validateRanks(): Boolean {
        val enteredRanks = mutableListOf<Int>()

        for (editText in rankFields) {
            val text = editText.text.toString()
            if (text.isEmpty()) {
                showToast(getString(R.string.rank_required))
                return false
            }

            val value = text.toIntOrNull()
            if (value == null || value !in 1..7) {
                showToast(getString(R.string.rank_invalid))
                return false
            }

            enteredRanks.add(value)
        }

        val duplicates = enteredRanks.groupBy { it }.filter { it.value.size > 1 }
        if (duplicates.isNotEmpty()) {
            showToast(getString(R.string.rank_duplicate, duplicates.keys.joinToString()))
            return false
        }

        return true
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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

    private fun saveQulieQuestionsData() {
        val ranks = rankFields.map { it.text.toString().toInt() }

        val answerIds = intent.getSerializableExtra("answer_ids") as? HashMap<Int, Int> ?: return
        val answerTexts = intent.getSerializableExtra("answer_texts") as? HashMap<Int, String> ?: return
        val totalScore = intent.getIntExtra("total_score", 0)


        val qEntity = QolieQuestionEntity(
            questionFirstAnswerId = answerIds[0] ?: -1,
            questionFirstAnswer = answerTexts[0] ?: "",

            questionSecondAnswerId = answerIds[1] ?: -1,
            questionSecondAnswer = answerTexts[1] ?: "",

            questionThirdAnswerId = answerIds[2] ?: -1,
            questionThirdAnswer = answerTexts[2] ?: "",

            questionFourthAnswerId = answerIds[3] ?: -1,
            questionFourthAnswer = answerTexts[3] ?: "",

            questionFifthAnswerId = answerIds[4] ?: -1,
            questionFifthAnswer = answerTexts[4] ?: "",

            questionSixthAnswerId = answerIds[5] ?: -1,
            questionSixthAnswer = answerTexts[5] ?: "",

            questionSeventhAnswerId = answerIds[6] ?: -1,
            questionSeventhAnswer = answerTexts[6] ?: "",

            questionEighthAnswerId = answerIds[7] ?: -1,
            questionEighthAnswer = answerTexts[7] ?: "",

            questionNinthAnswerId = answerIds[8] ?: -1,
            questionNinthAnswer = answerTexts[8] ?: "",

            questionTenthAnswerId = answerIds[9] ?: -1,
            questionTenthAnswer = answerTexts[9] ?: "",

            questionEleventhAnswerId = answerIds[10] ?: -1,
            questionEleventhAnswer = answerTexts[10] ?: "",

            totalScore = totalScore,

            firstPointRank = ranks[0],
            secondPointRank = ranks[1],
            thirdPointRank = ranks[2],
            fourthPointRank = ranks[3],
            fifthPointRank = ranks[4],
            sixthPointRank = ranks[5],
            seventhPointRank = ranks[6],

            date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        )

        Log.d("pawan", "save quile entity: $qEntity")

        viewModel.saveQolieQuestionData(qEntity)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}