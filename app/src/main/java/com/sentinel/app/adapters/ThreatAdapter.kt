package com.sentinel.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.api.CveItem

class ThreatAdapter(
    private var threats: MutableList<CveItem> = mutableListOf()
) : RecyclerView.Adapter<ThreatAdapter.ThreatViewHolder>() {

    class ThreatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCveId: TextView = itemView.findViewById(R.id.tv_cve_id)
        val tvSeverity: TextView = itemView.findViewById(R.id.tv_severity)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val tvPublished: TextView = itemView.findViewById(R.id.tv_published)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThreatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_threat, parent, false)
        return ThreatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ThreatViewHolder, position: Int) {
        val item = threats[position]

        // CVE ID
        holder.tvCveId.text = item.cve.id

        // Description - get English description
        val description = item.cve.descriptions
            .firstOrNull { it.lang == "en" }?.value ?: "No description available"
        holder.tvDescription.text = description

        // Published date - trim to just date part
        val published = item.cve.published.take(10)
        holder.tvPublished.text = "Published: $published"

        // Severity
        val severity = item.cve.metrics?.cvssMetricV2
            ?.firstOrNull()?.baseSeverity ?: "UNKNOWN"
        holder.tvSeverity.text = severity

        // Color based on severity
        val color = when (severity.uppercase()) {
            "HIGH" -> 0xFFFF4757.toInt()
            "MEDIUM" -> 0xFFFFA502.toInt()
            "LOW" -> 0xFF2ED573.toInt()
            else -> 0xFF6B7FA3.toInt()
        }
        holder.tvSeverity.setTextColor(color)
    }

    override fun getItemCount() = threats.size

    // Update data
    fun updateData(newThreats: List<CveItem>) {
        threats.clear()
        threats.addAll(newThreats)
        notifyDataSetChanged()
    }

    // Filter by keyword
    fun filter(query: String, allThreats: List<CveItem>) {
        threats.clear()
        if (query.isEmpty()) {
            threats.addAll(allThreats)
        } else {
            threats.addAll(allThreats.filter { item ->
                val desc = item.cve.descriptions
                    .firstOrNull { it.lang == "en" }?.value ?: ""
                item.cve.id.contains(query, ignoreCase = true) ||
                        desc.contains(query, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }
}