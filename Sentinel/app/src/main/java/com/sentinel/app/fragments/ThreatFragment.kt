package com.sentinel.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.adapters.ThreatAdapter
import com.sentinel.app.api.CveItem
import com.sentinel.app.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThreatFragment : Fragment() {

    private lateinit var rvThreats: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvError: TextView
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var adapter: ThreatAdapter

    private val allThreats = mutableListOf<CveItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_threat, container, false)

        initViews(view)
        setupRecyclerView()
        setupListeners()
        fetchThreats()

        return view
    }

    private fun initViews(view: View) {
        rvThreats = view.findViewById(R.id.rv_threats)
        progressBar = view.findViewById(R.id.progress_threats)
        tvError = view.findViewById(R.id.tv_error)
        etSearch = view.findViewById(R.id.et_threat_search)
        btnSearch = view.findViewById(R.id.btn_threat_search)
    }

    private fun setupRecyclerView() {
        adapter = ThreatAdapter()
        rvThreats.layoutManager = LinearLayoutManager(requireContext())
        rvThreats.adapter = adapter
    }

    private fun setupListeners() {
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            adapter.filter(query, allThreats)
        }
    }

    private fun fetchThreats() {
        // Show loading
        progressBar.visibility = View.VISIBLE
        tvError.visibility = View.GONE
        rvThreats.visibility = View.GONE

        // Run API call on background thread using Coroutines
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.apiService
                    .getRecentVulnerabilities(15, "android")

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body() != null) {
                        val threats = response.body()!!.vulnerabilities
                        allThreats.clear()
                        allThreats.addAll(threats)
                        adapter.updateData(threats)
                        rvThreats.visibility = View.VISIBLE

                        if (threats.isEmpty()) {
                            tvError.text = "No vulnerabilities found"
                            tvError.visibility = View.VISIBLE
                        }
                    } else {
                        showError("Failed to load data. Code: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    showError("Network error: ${e.message}")
                }
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        rvThreats.visibility = View.GONE
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}