package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.databinding.ActivityWhodasquestionSecBinding
import com.india.epilepsyfoundation.entity.WhodasQuestionEntity
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AssessmentViewmodel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WHODASQuestionSecActivity : BaseActivity() {

    private lateinit var binding: ActivityWhodasquestionSecBinding
    private val viewModel: AssessmentViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityWhodasquestionSecBinding.inflate(LayoutInflater.from(this))
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

        binding.btnSubmitForm.setOnClickListener {
            saveWhodasData()
        }

        viewModel.saveWhodasStatus.observe(this) { isSaved ->
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

    private fun saveWhodasData() {
        val h1 = binding.edth1.text.toString().toIntOrNull() ?: 0
        val h2 = binding.edth2.text.toString().toIntOrNull() ?: 0
        val h3 = binding.edth3.text.toString().toIntOrNull() ?: 0

        if (h1 >= 30 || h2 >= 30 || h3 >= 30) {
            Toast.makeText(this, getString(R.string.high_score_warning), Toast.LENGTH_SHORT).show()
            return
        }


        val answerIds = intent.getSerializableExtra("answer_ids") as? HashMap<Int, Int> ?: return
        val answerTexts = intent.getSerializableExtra("answer_texts") as? HashMap<Int, String> ?: return
        val percentageScore = intent.getDoubleExtra("percentage_score", 0.0)


        val entity = WhodasQuestionEntity(
            questionFirsAnswertId = answerIds[0] ?: -1,
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

            questionTwelweAnswerId = answerIds[11] ?: -1,
            questionTwelweAnswer = answerTexts[11] ?: "",

            totalScore = percentageScore.toFloat(),
            firstQuestionAnswer = h1,
            secondQuestionAnswer = h2,
            thirdQuestionAnswer = h3,
            date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        )


        Log.d("pawan", "save whodas entity ${entity.toString()}")

        viewModel.saveWhodasQuestionData(entity)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}