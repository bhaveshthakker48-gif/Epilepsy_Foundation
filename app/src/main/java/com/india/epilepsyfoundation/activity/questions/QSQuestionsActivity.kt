package com.india.epilepsyfoundation.activity.questions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.india.epilepsyfoundation.databinding.ActivityQsquestionsBinding
import com.india.epilepsyfoundation.entity.QuestionnaireEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.QuestionnaireViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QSQuestionsActivity : BaseActivity() {

    private lateinit var binding: ActivityQsquestionsBinding
    private val viewModel: QuestionnaireViewModel by viewModels()
    private var firstName = ""
    private var lastName = ""
    private var gender = ""
    private var dob = ""
    private var age = 0
    private var mobileNumber = ""
    private lateinit var questions: Array<String>
    private var currentQuestionIndex = 0
    private var answer1IsNo = false
    private var answer2IsNo = false
    private var result = ""
    private val answersMap = mutableMapOf<Int, Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityQsquestionsBinding.inflate(LayoutInflater.from(this))
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


        firstName = intent.getStringExtra("firstName") ?: ""
        lastName = intent.getStringExtra("lastName") ?: ""
        gender = intent.getStringExtra("gender") ?: ""
        dob = intent.getStringExtra("dob") ?: ""
        age = intent.getIntExtra("age", 0)
        mobileNumber = intent.getStringExtra("mobileNumber") ?: ""

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val customView = inflater.inflate(R.layout.toolbar_with_logo, binding.toolbar, false)

        val titleTextView = customView.findViewById<TextView>(R.id.toolbarTitle)
        titleTextView.text = getString(R.string.title_question_set)

        binding.toolbar.addView(customView)
        questions = if (age in 2..9) {
            resources.getStringArray(R.array.epilepsy_questionaries_2_9)
        } else {
            resources.getStringArray(R.array.epilepsy_questionnaire_extended)
        }

        showCurrentQuestion()

        binding.btnSubmit.setOnClickListener {
            val selectedId = binding.radioGroup1.checkedRadioButtonId

            // Validate selection
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer before proceeding", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedAnswer = when (selectedId) {
                R.id.radio0 -> 1
                R.id.radio1 -> 0
                R.id.radio2 -> 2
                else -> -1
            }

            // Save the answer
            answersMap[currentQuestionIndex] = selectedAnswer

            // Track special logic for early skip
            when (currentQuestionIndex) {
                0 -> answer1IsNo = selectedAnswer == 0
                1 -> answer2IsNo = selectedAnswer == 0
            }

            // Move to next
            currentQuestionIndex++

            // Apply skip logic
            if (age in 2..9 && currentQuestionIndex == 2 && answer1IsNo && answer2IsNo) {
                currentQuestionIndex = 9
            }

            // Continue
            if (currentQuestionIndex < questions.size) {
                showCurrentQuestion()
            } else {
                if (age in 2..9) {
                    result = getDiagnosis(answersMap)
                    saveQuestionsDataLocally(result)
                } else {
                    saveQuestionsDataLocally("NA")
                }
            }
        }

        viewModel.saveStatus.observe(this) { isSaved ->
            if (isSaved) {
                Toast.makeText(this, "All questions completed. Diagnosis: $result", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, QuestionnaireActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, getString(R.string.form_submit_failed), Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getAnswerId(index: Int): Int {
        return when (val answer = answersMap[index]) {
            null -> 0
            1 -> 1
            0 -> 2
            2 -> 3
            else -> 0
        }
    }

    private fun getAnswerText(index: Int): String {
        val answer = answersMap[index] ?: return ""

        // Convert age to Int safely
        val userAge = age ?: return ""

        // Logic: For age in 2..9, show detailed options. Else, Yes/No only.
        return if (userAge in 2..9) {
            when (index) {
                2 -> when (answer) {
                    1 -> getString(R.string.option_one)
                    0 -> getString(R.string.option_more_than_one)
                    else -> ""
                }
                3 -> when (answer) {
                    1 -> getString(R.string.option_less_than_24)
                    0 -> getString(R.string.option_more_than_24)
                    2 -> getString(R.string.option_not_applicable)
                    else -> ""
                }
                4 -> when (answer) {
                    1 -> getString(R.string.lbl_radio_yes)
                    0 -> getString(R.string.lbl_radio_no)
                    2 -> getString(R.string.option_not_applicable)
                    else -> ""
                }
                else -> when (answer) {
                    1 -> getString(R.string.lbl_radio_yes)
                    0 -> getString(R.string.lbl_radio_no)
                    2 -> getString(R.string.option_not_applicable)
                    else -> ""
                }
            }
        } else {
            // For age > 9, only Yes/No
            when (answer) {
                1 -> getString(R.string.lbl_radio_yes)
                0 -> getString(R.string.lbl_radio_no)
                else -> ""
            }
        }
    }


    private fun getDiagnosis(answers: Map<Int, Int>): String {
        fun get(q: Int): Int = answers[q - 1] ?: -1  // Convert 1-based to 0-based index

        val q = { n: Int -> get(n) }

        // ============ Epilepsy ============
        if ((q(2) == 1 && q(3) == 1 && q(4) == 1 && listOf(q(5), q(6), q(7), q(8), q(9)).all { it == 0 }) ||
            (q(10) == 1) ||
            (q(11) == 1) ||
            (q(10) == 1 && q(11) == 1) ||
            (q(2) == 1 && q(3) == 0 && q(13) == 1) ||
            (q(3) == 1 && q(4) == 0 && listOf(q(5), q(6), q(7), q(8), q(9)).all { it == 0 } && q(13) == 1)
        ) {
            return getString(R.string.diagnosis_epilepsy)
        }

        // ============ No Epilepsy ============
        if (listOf(q(2), q(10), q(11)).all { it == 0 } ||
            ((q(1) == 1 || q(2) == 1) && listOf(q(5), q(6), q(7), q(8), q(9)).any { it == 1 } && q(12) == 0) ||
            (q(1) == 1 && q(2) == 0 && q(10) == 0 && q(11) == 0) ||
            (q(2) == 1 && q(3) == 0 && q(13) == 0 && q(12) == 0) ||
            (q(3) == 1 && q(4) == 0 && listOf(q(5), q(6), q(7), q(8), q(9)).all { it == 0 } && q(13) == 1 && q(12) == 0)
        ) {
            return getString(R.string.diagnosis_no_epilepsy)
        }

        // ============ Indeterminate ============
        if ((q(1) == 1 && listOf(q(5), q(6), q(7), q(8), q(9)).any { it == 1 }) ||
            (q(2) == 1 && listOf(q(5), q(6), q(7), q(8), q(9)).any { it == 1 }) ||
            (q(2) == 1 && q(3) == 0 && q(13) == 0 && q(12) == 1) ||
            (q(3) == 1 && q(4) == 0 && listOf(q(5), q(6), q(7), q(8), q(9)).all { it == 0 } && q(13) == 1 && q(12) == 1)
        ) {
            return getString(R.string.diagnosis_indeterminate)
        }

        return getString(R.string.diagnosis_unknown)
    }




    private fun showCurrentQuestion() {
        binding.textView1.text = questions[currentQuestionIndex]
        binding.radioGroup1.clearCheck()

        binding.radio0.visibility = View.VISIBLE
        binding.radio1.visibility = View.VISIBLE
        binding.radio2.visibility = View.GONE
        binding.txtCause.visibility = View.GONE

        // Apply logic for epilepsy_questionaries_2_9 only
        if (age in 2..9) {
            when (currentQuestionIndex) {
                2 -> { // Question 3
                    binding.radio0.text = getString(R.string.option_one)
                    binding.radio1.text = getString(R.string.option_more_than_one)
                    binding.radio2.visibility = View.GONE
                }

                3 -> { // Question 4
                    binding.radio0.text = getString(R.string.option_less_than_24)
                    binding.radio1.text = getString(R.string.option_more_than_24)
                    binding.radio2.apply {
                        text = getString(R.string.option_not_applicable)
                        visibility = View.VISIBLE
                    }
                }

                4 -> { // Question 5
                    binding.radio0.text = getString(R.string.lbl_radio_yes)
                    binding.radio1.text = getString(R.string.lbl_radio_no)
                    binding.radio2.apply {
                        text = getString(R.string.option_not_applicable)
                        visibility = View.VISIBLE
                    }
                }

                5 -> { // Question 6
                    binding.radio0.text = getString(R.string.lbl_radio_yes)
                    binding.radio1.text = getString(R.string.lbl_radio_no)
                    binding.radio2.visibility = View.GONE
                    binding.txtCause.visibility = View.VISIBLE
                }

                else -> { // All other questions: Yes / No
                    binding.radio0.text = getString(R.string.lbl_radio_yes)
                    binding.radio1.text = getString(R.string.lbl_radio_no)
                    binding.radio2.visibility = View.GONE
                }
            }
        } else {
            // Default for epilepsy_questionnaire_extended — Yes / No questions only
            binding.radio0.text = getString(R.string.lbl_radio_yes)
            binding.radio1.text = getString(R.string.lbl_radio_no)
            binding.radio2.visibility = View.GONE
            binding.txtCause.visibility = View.GONE
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu?.findItem(R.id.menu_language)
        val spinner = item?.actionView as? Spinner

        val languages = listOf("English", "हिन्दी", "मराठी")
        val adapter = ArrayAdapter(
            this,
            R.layout.toolbar_spinner_item,
            languages
        )
        adapter.setDropDownViewResource(R.layout.toolbar_spinner_dropdown_item)
        spinner?.adapter = adapter

        when (getCurrentLanguage()) {
            "en" -> spinner?.setSelection(0)
            "hi" -> spinner?.setSelection(1)
            "mr" -> spinner?.setSelection(2)
        }

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirstSelection = true

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (isFirstSelection) {
                    isFirstSelection = false
                    return
                }

                val selectedLang = when (position) {
                    0 -> "en"
                    1 -> "hi"
                    2 -> "mr"
                    else -> "en"
                }

                if (selectedLang != getCurrentLanguage()) {
                    SharedPreference.set("lang", selectedLang)
                    setLocale(selectedLang)
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

    private fun saveQuestionsDataLocally(result: String) {
        val questionnaireEntity = QuestionnaireEntity(
            firstName = firstName,
            lastName = lastName,
            age = age.toString(),
            gender = gender,
            questionFirsAnswertId = getAnswerId(0),
            questionFirstAnswer = getAnswerText(0),
            questionSecondAnswerId = getAnswerId(1),
            questionSecondAnswer = getAnswerText(1),
            questionThirdAnswerId = getAnswerId(2),
            questionThirdAnswer = getAnswerText(2),
            questionFourthAnswerId = getAnswerId(3),
            questionFourthAnswer = getAnswerText(3),
            questionFifthAnswerId = getAnswerId(4),
            questionFifthAnswer = getAnswerText(4),
            questionSixthAnswerId = getAnswerId(5),
            questionSixthAnswer = getAnswerText(5),
            questionSeventhAnswerId = getAnswerId(6),
            questionSeventhAnswer = getAnswerText(6),
            questionEighthAnswerId = getAnswerId(7),
            questionEighthAnswer = getAnswerText(7),
            questionNinthAnswerId = getAnswerId(8),
            questionNinthAnswer = getAnswerText(8),
            questionTenthAnswerId = getAnswerId(9),
            questionTenthAnswer = getAnswerText(9),
            questionEleventhAnswerId = getAnswerId(10),
            questionEleventhAnswer = getAnswerText(10),
            questionTwelweAnswerId = getAnswerId(11),
            questionTwelweAnswer = getAnswerText(11),
            questionThirteenAnswerId = getAnswerId(12),
            questionThirteenAnswer = getAnswerText(12),
            result = result,
            date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        )

        viewModel.saveQolieQuestionData(questionnaireEntity)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, QuestionnaireActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}