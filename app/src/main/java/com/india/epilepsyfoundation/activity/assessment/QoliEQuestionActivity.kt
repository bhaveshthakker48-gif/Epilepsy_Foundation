package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityQoliEquestionBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.Locale
import kotlin.math.roundToInt

class QoliEQuestionActivity : BaseActivity() {

    private lateinit var binding: ActivityQoliEquestionBinding
    private lateinit var questions: List<String>
    private lateinit var selectedAnswers: MutableMap<Int, Pair<Int, String>>

    private var currentQuestionIndex = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityQoliEquestionBinding.inflate(LayoutInflater.from(this))
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
        questions = resources.getStringArray(R.array.QOLE_questions).toList()
        selectedAnswers = mutableMapOf()

        setupQuestion(currentQuestionIndex)

        binding.nextBtnQolie.setOnClickListener {
            val radioButtons = listOf(
                binding.QolieRadioptn1,
                binding.QolieRadioptn2,
                binding.QolieRadioptn3,
                binding.QolieRadioptn4,
                binding.QolieRadioptn5,
                binding.QolieRadioptn6
            )

            val checkedId = binding.QolieRadioGroupoptions.checkedRadioButtonId
            if (checkedId != -1) {
                val selectedIndex = radioButtons.indexOfFirst { it.id == checkedId } + 1
                val selectedText = radioButtons[selectedIndex - 1].text.toString()
                selectedAnswers[currentQuestionIndex] = Pair(selectedIndex, selectedText)

                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    setupQuestion(currentQuestionIndex)
                } else {
                    val answerIdMap = HashMap<Int, Int>()
                    val answerTextMap = HashMap<Int, String>()
                    var totalScore = 0

                    selectedAnswers.forEach { (index, pair) ->
                        val answerId = pair.first
                        val answerText = pair.second

                        answerIdMap[index] = answerId
                        answerTextMap[index] = answerText

                        // Calculate score
                        val score = when (index) {
                            // Normal scoring
                            0, 3, 4, 5, 6, 7, 9 -> answerId
                            // Reverse scoring
                            1, 2, 8 -> {
                                val maxOption = when (index) {
                                    1 -> 6  // Question 2 has 6 options
                                    2 -> 5  // Question 3 has 5 options
                                    8 -> 4  // Question 9 has 4 options
                                    else -> 0
                                }
                                maxOption - answerId + 1  // Reverse score
                            }
                            else -> 0  // Ignore question 11
                        }

                        totalScore += score
                    }

                    selectedAnswers.forEach { (index, pair) ->
                        answerIdMap[index] = pair.first
                        answerTextMap[index] = pair.second
                    }

                    val numberOfQuestionsUsedForScoring = 10
                    val normalizedScore = (totalScore.toFloat() / numberOfQuestionsUsedForScoring).roundToInt()

                    val intent = Intent(this, QolieQuestionSecActivity::class.java)
                    intent.putExtra("answer_ids", answerIdMap)
                    intent.putExtra("answer_texts", answerTextMap)
                    intent.putExtra("total_score", normalizedScore)
                    startActivity(intent)
                    finish()
                }
            } else {
                showToast(getString(R.string.toast_select_answer_continue))
            }
        }


        binding.btnPrevQolie.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                setupQuestion(currentQuestionIndex)
            }
        }


    }

    private fun setupQuestion(index: Int) {
        val question = questions[index]
        binding.QolieTextQuestion.text = question

        val options = getOptionsForQuestion(index)
        val radioButtons = listOf(
            binding.QolieRadioptn1,
            binding.QolieRadioptn2,
            binding.QolieRadioptn3,
            binding.QolieRadioptn4,
            binding.QolieRadioptn5,
            binding.QolieRadioptn6
        )

        radioButtons.forEach { it.visibility = View.GONE }

        for (i in options.indices) {
            radioButtons[i].text = options[i]
            radioButtons[i].visibility = View.VISIBLE
        }

        binding.QolieRadioGroupoptions.clearCheck()

        selectedAnswers[index]?.let { savedPair ->
            val selectedIndex = savedPair.first - 1
            if (selectedIndex in radioButtons.indices) {
                radioButtons[selectedIndex].isChecked = true
            }
        }

        binding.btnPrevQolie.visibility = if (index == 0) View.INVISIBLE else View.VISIBLE
    }


    private fun getOptionsForQuestion(index: Int): List<String> {
        return when (index) {
            0, 1 -> listOf(
                getString(R.string.Qolie_option1),
                getString(R.string.Qolie_option2),
                getString(R.string.Qolie_option3),
                getString(R.string.Qolie_option4),
                getString(R.string.Qolie_option5),
                getString(R.string.Qolie_option6)
            )
            2 -> listOf(
                getString(R.string.Qolie_option3_1),
                getString(R.string.Qolie_option3_2),
                getString(R.string.Qolie_option3_3),
                getString(R.string.Qolie_option3_4),
                getString(R.string.Qolie_option3_5)
            )
            in 3..7 -> listOf(
                getString(R.string.Qolie_option4_1),
                getString(R.string.Qolie_option3_2),
                getString(R.string.Qolie_option3_3),
                getString(R.string.Qolie_option3_4),
                getString(R.string.Qolie_option4_5)
            )
            8 -> listOf(
                getString(R.string.Qolie_option9_1),
                getString(R.string.Qolie_option9_2),
                getString(R.string.Qolie_option9_3),
                getString(R.string.Qolie_option9_4)
            )
            9 -> listOf(
                getString(R.string.Qolie_option10_1),
                getString(R.string.Qolie_option10_2),
                getString(R.string.Qolie_option10_3),
                getString(R.string.Qolie_option10_4),
                getString(R.string.Qolie_option10_5)
            )
            10 -> listOf(
                getString(R.string.Qolie_option11_1),
                getString(R.string.Qolie_option11_2),
                getString(R.string.Qolie_option11_3),
                getString(R.string.Qolie_option11_4),
                getString(R.string.Qolie_option11_5)
            )
            else -> emptyList()
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

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}