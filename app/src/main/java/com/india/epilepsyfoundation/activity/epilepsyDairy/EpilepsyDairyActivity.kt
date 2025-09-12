package com.india.epilepsyfoundation.activity.epilepsyDairy

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
import com.india.epilepsyfoundation.activity.DashboardActivity
import com.india.epilepsyfoundation.adapter.AttackDetailsAdapter
import com.india.epilepsyfoundation.databinding.ActivityEpilepsyDairyBinding
import com.india.epilepsyfoundation.utils.SharedPreference
import com.india.epilepsyfoundation.viewmodel.AttackDetailsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class EpilepsyDairyActivity : BaseActivity() {

    private lateinit var binding : ActivityEpilepsyDairyBinding
    private lateinit var adapter1: AttackDetailsAdapter
    private val viewModel: AttackDetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setLocale(getCurrentLanguage())
        super.onCreate(savedInstanceState)
        binding = ActivityEpilepsyDairyBinding.inflate(LayoutInflater.from(this))
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

        binding.toolbar.addView(customView)

        binding.fab.setOnClickListener {
            val intent = Intent(this, EpilepsyDairyQuestionsActivity::class.java)
            startActivity(intent)
        }

        adapter1 = AttackDetailsAdapter(emptyList())
        binding.detailsRecyclerview.adapter = adapter1
        binding.detailsRecyclerview.layoutManager = LinearLayoutManager(this)

        fetchAttackDetailsData()
    }

    private fun fetchAttackDetailsData() {
        viewModel.getAllAttackDetailsData { visits ->
            if (visits.isEmpty()) {
                binding.detailsRecyclerview.visibility = View.GONE
                binding.noQolieData.visibility = View.VISIBLE
            } else {
                binding.detailsRecyclerview.visibility = View.VISIBLE
                binding.noQolieData.visibility = View.GONE

                val sortedList = visits.sortedByDescending {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.dateOfAttack)
                }

                adapter1.updateData(sortedList)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}