package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityWhodasquestionBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import java.util.*

class WHODASQuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhodasquestionBinding
    private lateinit var questions: List<String>
    private lateinit var selectedAnswers: MutableMap<Int, Pair<Int, String>>
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityWhodasquestionBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.whodastitle)

        binding.toolbar.addView(customView)

        questions = resources.getStringArray(R.array.who_questions).toList()
        selectedAnswers = mutableMapOf()

        setupQuestion(currentQuestionIndex)

        binding.btnNextQ.setOnClickListener {
            val radioButtons = listOf(
                binding.radioptn1,
                binding.radioptn2,
                binding.radioptn3,
                binding.radioptn4,
                binding.radioptn5,
            )

            val checkedId = binding.radioGroupoptions.checkedRadioButtonId
            if (checkedId != -1) {
                val selectedIndex = radioButtons.indexOfFirst { it.id == checkedId } + 1
                val selectedText = radioButtons[selectedIndex - 1].text.toString()
                selectedAnswers[currentQuestionIndex] = Pair(selectedIndex, selectedText)

                if (currentQuestionIndex < questions.size - 1) {
                    currentQuestionIndex++
                    setupQuestion(currentQuestionIndex)
                } else {
                    var totalScore = 0
                    val maxScore = questions.size * 4
                    val answerIdMap = HashMap<Int, Int>()
                    val answerTextMap = HashMap<Int, String>()

                    selectedAnswers.forEach { (index, pair) ->
                        val score = pair.first - 1
                        totalScore += score
                        answerIdMap[index] = pair.first
                        answerTextMap[index] = pair.second
                    }

                    val percentageScore = String.format("%.2f", (totalScore.toDouble() / maxScore) * 100).toDouble()


                    val intent = Intent(this, WHODASQuestionSecActivity::class.java)
                    intent.putExtra("answer_ids", answerIdMap)
                    intent.putExtra("answer_texts", answerTextMap)
                    intent.putExtra("percentage_score", percentageScore)
                    startActivity(intent)
                    finish()

                }
            } else {
                showToast(getString(R.string.toast_select_answer_continue))
            }
        }

        binding.btnPrevQ.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                setupQuestion(currentQuestionIndex)
            }
        }


    }

    private fun setupQuestion(index: Int) {
        val radioButtons = listOf(
            binding.radioptn1,
            binding.radioptn2,
            binding.radioptn3,
            binding.radioptn4,
            binding.radioptn5,
        )

        binding.textQuestion.text = questions[index]
        binding.radioGroupoptions.clearCheck()

        // Restore answer properly
        selectedAnswers[index]?.let { savedPair ->
            val selectedIndex = savedPair.first - 1
            if (selectedIndex in radioButtons.indices) {
                radioButtons[selectedIndex].isChecked = true
            }
        }

        // Hide previous button on first question
        binding.btnPrevQ.visibility = if (index == 0) View.INVISIBLE else View.VISIBLE
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
