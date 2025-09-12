package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityEpilepsyStigmaQuestionBinding
import com.india.epilepsyfoundation.entity.StigmaScaleQuestionEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AssessmentViewmodel
import java.text.SimpleDateFormat
import java.util.*

class EpilepsyStigmaQuestionActivity : BaseActivity() {

    private lateinit var binding: ActivityEpilepsyStigmaQuestionBinding
    private val viewModel: AssessmentViewmodel by viewModels()
    private lateinit var stigmaQuestions: Array<String>
    private var currentQuestionIndex = 0

    // Store selected answers: Question index → (RadioButton ID, Text)
    private val selectedAnswers = mutableMapOf<Int, Pair<Int, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityEpilepsyStigmaQuestionBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = true
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val customView = LayoutInflater.from(this).inflate(R.layout.toolbar_with_logo, binding.toolbar, false)
        customView.findViewById<TextView>(R.id.toolbarTitle).text = getString(R.string.Stigmatitle)
        binding.toolbar.addView(customView)

        stigmaQuestions = resources.getStringArray(R.array.stigma_questions)

        loadQuestion(currentQuestionIndex)

        binding.nextBtnSigma.setOnClickListener {
            val selectedId = binding.radioGroupStigma.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, getString(R.string.toast_select_answer_continue), Toast.LENGTH_SHORT).show()
            } else {
                saveAnswer()
                if (currentQuestionIndex < stigmaQuestions.size - 1) {
                    currentQuestionIndex++
                    loadQuestion(currentQuestionIndex)
                }
            }
        }

        binding.btnPrevSigma.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                loadQuestion(currentQuestionIndex)
            }
        }

        binding.SigmaBtnNextQ1.setOnClickListener {
            val selectedId = binding.radioGroupStigma.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, getString(R.string.toast_select_answer_submit), Toast.LENGTH_SHORT).show()
            } else {
                saveAnswer()
                saveStigmaScaleData()
            }
        }

        viewModel.saveStigmaScaleStatus.observe(this) { isSaved ->
            if (isSaved) {
                Toast.makeText(this, getString(R.string.form_submit_success), Toast.LENGTH_LONG).show()
                startActivity(Intent(this, AssessmentActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.form_submit_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadQuestion(index: Int) {
        binding.textquestnStigmaScale.text = stigmaQuestions[index]

        // Clear previous selection
        binding.radioGroupStigma.clearCheck()

        // Restore selected answer if available
        selectedAnswers[index]?.let { savedPair ->
            binding.radioGroupStigma.check(savedPair.first)
        }

        // Show/hide navigation buttons
        binding.btnPrevSigma.visibility = if (index == 0) View.INVISIBLE else View.VISIBLE
        binding.nextBtnSigma.visibility = if (index == stigmaQuestions.size - 1) View.GONE else View.VISIBLE
        binding.SigmaBtnNextQ1.visibility = if (index == stigmaQuestions.size - 1) View.VISIBLE else View.GONE
    }

    private fun saveAnswer() {
        val selectedId = binding.radioGroupStigma.checkedRadioButtonId
        if (selectedId != -1) {
            val selectedButton = findViewById<RadioButton>(selectedId)
            val selectedText = selectedButton.text.toString()

            // Save actual RadioButton ID and text
            selectedAnswers[currentQuestionIndex] = Pair(selectedId, selectedText)
        }
    }

    private fun saveStigmaScaleData() {
        val getScore: (Int) -> Int = { index ->
            selectedAnswers[index]?.first?.let {
                val rb = findViewById<RadioButton>(it)
                binding.radioGroupStigma.indexOfChild(rb) + 1
            } ?: -1
        }

        val entity = StigmaScaleQuestionEntity(
            questionFirstAnswerId = getScore(0),
            questionFirstAnswer = selectedAnswers[0]?.second ?: "",

            questionSecondAnswerId = getScore(1),
            questionSecondAnswer = selectedAnswers[1]?.second ?: "",

            questionThirdAnswerId = getScore(2),
            questionThirdAnswer = selectedAnswers[2]?.second ?: "",

            questionFourthAnswerId = getScore(3),
            questionFourthAnswer = selectedAnswers[3]?.second ?: "",

            questionFifthAnswerId = getScore(4),
            questionFifthAnswer = selectedAnswers[4]?.second ?: "",

            questionSixthAnswerId = getScore(5),
            questionSixthAnswer = selectedAnswers[5]?.second ?: "",

            questionSeventhAnswerId = getScore(6),
            questionSeventhAnswer = selectedAnswers[6]?.second ?: "",

            questionEighthAnswerId = getScore(7),
            questionEighthAnswer = selectedAnswers[7]?.second ?: "",

            questionNinthAnswerId = getScore(8),
            questionNinthAnswer = selectedAnswers[8]?.second ?: "",

            questionTenthAnswerId = getScore(9),
            questionTenthAnswer = selectedAnswers[9]?.second ?: "",

            totalScore = (0..9).sumOf { getScore(it).takeIf { id -> id != -1 } ?: 0 },
            date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        )

        Log.d("pawan", "stigma entity $entity")

        viewModel.saveStigmaScaleQuestionData(entity)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
