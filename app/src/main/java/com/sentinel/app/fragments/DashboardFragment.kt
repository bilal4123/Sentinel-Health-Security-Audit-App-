package com.sentinel.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.adapters.AppListAdapter
import com.sentinel.app.models.AppInfo
import com.sentinel.app.models.Permission
import com.sentinel.app.utils.PermissionAnalyzer

class DashboardFragment : Fragment() {

    private lateinit var adapter: AppListAdapter
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var rvApps: RecyclerView
    private lateinit var tvTotalApps: TextView
    private lateinit var tvThreatsCount: TextView
    private lateinit var tvSafeCount: TextView
    private lateinit var tvSecurityScore: TextView
    private lateinit var tvSecurityStatus: TextView

    private val installedApps = mutableListOf<AppInfo>()
    private lateinit var analyzer: PermissionAnalyzer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate your existing activity_main.xml
        val view = inflater.inflate(R.layout.activity_main, container, false)

        analyzer = PermissionAnalyzer(requireContext())
        initViews(view)
        setupRecyclerView(view)
        setupListeners(view)
        loadInstalledApps()

        return view
    }

    private fun initViews(view: View) {
        tvTotalApps = view.findViewById(R.id.tv_total_apps)
        tvThreatsCount = view.findViewById(R.id.tv_threats_count)
        tvSafeCount = view.findViewById(R.id.tv_safe_count)
        tvSecurityScore = view.findViewById(R.id.tv_security_score)
        tvSecurityStatus = view.findViewById(R.id.tv_security_status)
        etSearch = view.findViewById(R.id.et_search_apps)
        btnSearch = view.findViewById(R.id.btn_search_apps)
    }

    private fun setupRecyclerView(view: View) {
        rvApps = view.findViewById(R.id.rv_installed_apps)
        rvApps.layoutManager = LinearLayoutManager(requireContext())
        adapter = AppListAdapter(requireContext(), installedApps)
        rvApps.adapter = adapter
    }

    private fun setupListeners(view: View) {
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            adapter.filter(query)
        }

        // Full scan button
        view.findViewById<View>(R.id.btn_full_scan).setOnClickListener {
            scanAllApps()
        }

        // Threats button
        view.findViewById<View>(R.id.btn_threats).setOnClickListener {
            Toast.makeText(context, "Showing threats", Toast.LENGTH_SHORT).show()
        }

        // App Audit button
        view.findViewById<View>(R.id.btn_app_audit).setOnClickListener {
            Toast.makeText(context, "Opening app audit", Toast.LENGTH_SHORT).show()
        }

        // Stats click listeners
        view.findViewById<View>(R.id.stats_total_apps).setOnClickListener {
            Toast.makeText(context, "Total apps: ${installedApps.size}", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.stats_threats).setOnClickListener {
            val threats = installedApps.count { it.riskLevel == "HIGH" }
            Toast.makeText(context, "High risk apps: $threats", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.stats_safe).setOnClickListener {
            val safe = installedApps.count { it.riskLevel == "LOW" }
            Toast.makeText(context, "Safe apps: $safe", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadInstalledApps() {
        installedApps.clear()

        // Sample data
        val whatsappPerms = listOf(
            Permission("ACCESS_FINE_LOCATION", "dangerous", "📍"),
            Permission("CAMERA", "dangerous", "📷"),
            Permission("RECORD_AUDIO", "dangerous", "🎤"),
            Permission("READ_CONTACTS", "dangerous", "👥")
        )

        val whatsapp = AppInfo("WhatsApp Messenger", "com.whatsapp", "2.23.11").apply {
            permissions = whatsappPerms
            riskLevel = analyzer.calculateRiskLevel(whatsappPerms)
            riskScore = analyzer.calculateRiskScore(whatsappPerms)
        }
        installedApps.add(whatsapp)

        val tiktokPerms = listOf(
            Permission("CAMERA", "dangerous", "📷"),
            Permission("RECORD_AUDIO", "dangerous", "🎤")
        )

        val tiktok = AppInfo("TikTok", "com.tiktok", "28.5.3").apply {
            permissions = tiktokPerms
            riskLevel = analyzer.calculateRiskLevel(tiktokPerms)
            riskScore = analyzer.calculateRiskScore(tiktokPerms)
        }
        installedApps.add(tiktok)

        val chromePerms = listOf(
            Permission("ACCESS_FINE_LOCATION", "dangerous", "📍")
        )

        val chrome = AppInfo("Google Chrome", "com.android.chrome", "120.0").apply {
            permissions = chromePerms
            riskLevel = analyzer.calculateRiskLevel(chromePerms)
            riskScore = analyzer.calculateRiskScore(chromePerms)
        }
        installedApps.add(chrome)

        updateStats()
        adapter.updateData(installedApps)
    }

    private fun scanAllApps() {
        Toast.makeText(context, "Scanning all apps...", Toast.LENGTH_SHORT).show()
        loadInstalledApps()
        Toast.makeText(context, "Scan complete!", Toast.LENGTH_SHORT).show()
    }

    private fun updateStats() {
        val totalApps = installedApps.size
        val threats = installedApps.count { it.riskLevel == "HIGH" }
        val safe = installedApps.count { it.riskLevel == "LOW" }
        val avgScore = if (totalApps > 0) installedApps.sumOf { it.riskScore } / totalApps else 0

        tvTotalApps.text = totalApps.toString()
        tvThreatsCount.text = threats.toString()
        tvSafeCount.text = safe.toString()
        tvSecurityScore.text = avgScore.toString()

        tvSecurityStatus.text = when {
            avgScore >= 70 -> "SECURE"
            avgScore >= 40 -> "MODERATE"
            else -> "AT RISK"
        }
    }
}