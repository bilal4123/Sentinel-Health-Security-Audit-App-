package com.sentinel.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sentinel.app.R

class SettingsFragment : Fragment() {

    private lateinit var rgScanMode: RadioGroup
    private lateinit var rgTheme: RadioGroup
    private lateinit var swThreatAlerts: Switch
    private lateinit var swWeeklyReport: Switch
    private lateinit var swAutoScan: Switch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        initViews(view)
        setupListeners()
        loadSavedSettings()

        return view
    }

    private fun initViews(view: View) {
        rgScanMode = view.findViewById(R.id.rg_scan_mode)
        rgTheme = view.findViewById(R.id.rg_theme)
        swThreatAlerts = view.findViewById(R.id.sw_threat_alerts)
        swWeeklyReport = view.findViewById(R.id.sw_weekly_report)
        swAutoScan = view.findViewById(R.id.sw_auto_scan)
    }

    private fun setupListeners() {
        rgScanMode.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rb_quick -> "Quick Scan"
                R.id.rb_deep -> "Deep Scan"
                else -> "Scheduled Scan"
            }
            Toast.makeText(context, "Scan mode set to: $mode", Toast.LENGTH_SHORT).show()
        }

        rgTheme.setOnCheckedChangeListener { _, checkedId ->
            val theme = when (checkedId) {
                R.id.rb_dark -> "Dark Theme"
                R.id.rb_light -> "Light Theme"
                else -> "Follow System"
            }
            Toast.makeText(context, "Theme set to: $theme", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSavedSettings() {
        // Load saved preferences from SharedPreferences
    }
}