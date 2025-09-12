package com.india.epilepsyfoundation.activity.assessment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.india.epilepsyfoundation.BaseActivity
import com.india.epilepsyfoundation.R
import com.india.epilepsyfoundation.adapter.QolieResultAdapter
import com.india.epilepsyfoundation.adapter.StigmaScaleResultAdapter
import com.india.epilepsyfoundation.adapter.WhodasResultAdapter
import com.india.epilepsyfoundation.databinding.ActivityViewAssessmentResultBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AssessmentViewmodel
import java.util.Locale

class ViewAssessmentResultActivity : BaseActivity() {

    private lateinit var binding: ActivityViewAssessmentResultBinding
    private lateinit var adapter1: QolieResultAdapter
    private lateinit var adapter2: StigmaScaleResultAdapter
    private lateinit var adapter3: WhodasResultAdapter
    private val viewModel: AssessmentViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityViewAssessmentResultBinding.inflate(LayoutInflater.from(this))
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
        titleTextView.text = getString(R.string.assessment_result)

        binding.toolbar.addView(customView)

        adapter1 = QolieResultAdapter(emptyList())
        binding.qolieRecyclerview.adapter = adapter1
        binding.qolieRecyclerview.layoutManager = LinearLayoutManager(this)

        adapter2 = StigmaScaleResultAdapter(emptyList())
        binding.stigmaScaleRecyclerview.adapter = adapter2
        binding.stigmaScaleRecyclerview.layoutManager = LinearLayoutManager(this)

        adapter3 = WhodasResultAdapter(emptyList())
        binding.whodasRecyclerview.adapter = adapter3
        binding.whodasRecyclerview.layoutManager = LinearLayoutManager(this)

        fetchQolieData()
        fetchStigmaScaleData()
        fetchWhodaseData()
    }

    private fun fetchQolieData() {
        viewModel.getAllQolieQuestionData { visits ->
            if (visits.isEmpty()) {
                binding.qolieRecyclerview.visibility = View.GONE
                binding.qolieLayout.visibility = View.GONE
                binding.noQolieData.visibility = View.VISIBLE
            } else {
                binding.qolieRecyclerview.visibility = View.VISIBLE
                binding.qolieLayout.visibility = View.VISIBLE
                binding.noQolieData.visibility = View.GONE
                adapter1.updateData(visits.reversed())
            }
        }
    }


    private fun fetchStigmaScaleData() {
        viewModel.getAllStigmaScaleQuestionData { visits ->
            if (visits.isEmpty()) {
                binding.stigmaScaleRecyclerview.visibility = View.GONE
                binding.stigmaLayout.visibility = View.GONE
                binding.noStigmaData.visibility = View.VISIBLE
            } else {
                binding.stigmaScaleRecyclerview.visibility = View.VISIBLE
                binding.stigmaLayout.visibility = View.VISIBLE
                binding.noStigmaData.visibility = View.GONE
                adapter2.updateData(visits.reversed())
            }
        }
    }

    private fun fetchWhodaseData() {
        viewModel.getAllWhodasQuestionData { visits ->
            if (visits.isEmpty()) {
                binding.whodasRecyclerview.visibility = View.GONE
                binding.whodasLayout.visibility = View.GONE
                binding.noWhodasData.visibility = View.VISIBLE
            } else {
                binding.whodasRecyclerview.visibility = View.VISIBLE
                binding.whodasLayout.visibility = View.VISIBLE
                binding.noWhodasData.visibility = View.GONE
                adapter3.updateData(visits.reversed())
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


    private fun gotoScreen(destination: Class<*>) {
        val intent = Intent(this, destination)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, AssessmentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}