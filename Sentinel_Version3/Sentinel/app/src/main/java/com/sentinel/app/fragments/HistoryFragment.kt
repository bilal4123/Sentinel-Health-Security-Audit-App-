package com.sentinel.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.adapters.HistoryDbAdapter
import com.sentinel.app.database.DatabaseHelper
import com.sentinel.app.firestore.FirestoreHelper
import com.sentinel.app.models.ScanResult
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: HistoryDbAdapter
    private lateinit var rvHistory: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnAddSample: Button
    private lateinit var btnSortRisk: Button
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var firestoreHelper: FirestoreHelper
    private lateinit var btnSyncCloud: Button

    private val allItems = mutableListOf<Map<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        dbHelper = DatabaseHelper(requireContext())
        firestoreHelper = FirestoreHelper(requireContext())
        initViews(view)
        setupRecyclerView()
        setupListeners()
        loadFromDatabase()
        observeCloudScans()

        return view
    }

    private fun initViews(view: View) {
        rvHistory = view.findViewById(R.id.rv_history_db)
        tvEmpty = view.findViewById(R.id.tv_empty)
        btnAddSample = view.findViewById(R.id.btn_add_sample)
        btnSortRisk = view.findViewById(R.id.btn_sort_risk)
        etSearch = view.findViewById(R.id.et_history_search)
        btnSearch = view.findViewById(R.id.btn_history_search)
        btnSyncCloud = view.findViewById(R.id.btn_sync_cloud)
    }

    private fun setupRecyclerView() {
        adapter = HistoryDbAdapter(mutableListOf()) { appId ->
            deleteApp(appId)
        }
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        rvHistory.adapter = adapter
    }

    private fun setupListeners() {

        btnAddSample.setOnClickListener {
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val today = sdf.format(Date())

            val id1 = dbHelper.insertApp(
                "WhatsApp Messenger", "com.whatsapp", "HIGH", 85, today
            )
            dbHelper.insertPermission(id1, "ACCESS_FINE_LOCATION", "dangerous")
            dbHelper.insertPermission(id1, "CAMERA", "dangerous")
            dbHelper.insertPermission(id1, "READ_CONTACTS", "dangerous")

            val id2 = dbHelper.insertApp(
                "TikTok", "com.tiktok", "MEDIUM", 55, today
            )
            dbHelper.insertPermission(id2, "CAMERA", "dangerous")
            dbHelper.insertPermission(id2, "RECORD_AUDIO", "dangerous")

            val id3 = dbHelper.insertApp(
                "Google Chrome", "com.android.chrome", "LOW", 20, today
            )
            dbHelper.insertPermission(id3, "ACCESS_FINE_LOCATION", "dangerous")

            Toast.makeText(context, "✅ Scan results saved to database!", Toast.LENGTH_SHORT).show()
            loadFromDatabase()
            syncToCloud()
        }

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isEmpty()) {
                loadFromDatabase()
            } else {
                val results = dbHelper.searchApps(query)
                allItems.clear()
                allItems.addAll(results)
                adapter.updateData(results)
                checkEmpty(results)
            }
        }

        btnSortRisk.setOnClickListener {
            val sorted = dbHelper.getAppsSortedByRisk()
            allItems.clear()
            allItems.addAll(sorted)
            adapter.updateData(sorted)
            Toast.makeText(context, "Sorted by risk score!", Toast.LENGTH_SHORT).show()
        }

        btnSyncCloud.setOnClickListener {
            syncToCloud()
        }
    }

    private fun loadFromDatabase() {
        val items = dbHelper.getAllApps()
        allItems.clear()
        allItems.addAll(items)
        adapter.updateData(items)
        checkEmpty(items)
    }

    private fun deleteApp(appId: String) {
        val deleted = dbHelper.deleteApp(appId)
        if (deleted > 0) {
            Toast.makeText(context, "✅ Deleted successfully!", Toast.LENGTH_SHORT).show()
            loadFromDatabase()
        } else {
            Toast.makeText(context, "Error deleting record", Toast.LENGTH_SHORT).show()
        }
    }

    // FIXED: Corrected ScanResult parameter names
    private fun syncToCloud() {
        lifecycleScope.launch {
            Toast.makeText(context, "Syncing to cloud...", Toast.LENGTH_SHORT).show()

            val items = dbHelper.getAllApps()
            for (item in items) {
                val scanResult = ScanResult(
                    appName = item["app_name"] ?: "",
                    packageName = item["package_name"] ?: "",
                    riskLevel = item["risk_level"] ?: "",
                    scanDate = Date(),
                    dangerousPermsCount = (item["risk_score"]?.toIntOrNull()?.div(20) ?: 0),
                    riskScore = item["risk_score"]?.toIntOrNull() ?: 0
                )
                firestoreHelper.saveScanResult(scanResult)
            }
            Toast.makeText(context, "Synced to cloud!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeCloudScans() {
        lifecycleScope.launch {
            firestoreHelper.observeScanResults().collect { cloudScans ->
                if (cloudScans.isNotEmpty()) {
                    // Update local database with cloud data if needed
                }
            }
        }
    }

    private fun checkEmpty(items: List<Map<String, String>>) {
        if (items.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvHistory.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvHistory.visibility = View.VISIBLE
        }
    }
}