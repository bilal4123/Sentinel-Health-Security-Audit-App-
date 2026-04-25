package com.sentinel.app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.models.ScanResult

class HistoryAdapter(
    private val context: Context,
    private var scanResults: MutableList<ScanResult>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val result = scanResults[position]
        holder.tvAppName.text = result.appName
        holder.tvRiskLevel.text = result.riskLevel
        holder.tvDate.text = result.getFormattedDate()
        holder.tvPermCount.text = "${result.dangerousPermsCount} dangerous perms"

        val colorResId = when (result.riskLevel.lowercase()) {
            "high" -> R.color.risk_high
            "medium" -> R.color.risk_medium
            else -> R.color.risk_safe
        }
        holder.tvRiskLevel.setTextColor(ContextCompat.getColor(context, colorResId))
    }

    override fun getItemCount(): Int = scanResults.size

    fun updateData(newResults: List<ScanResult>) {
        scanResults.clear()
        scanResults.addAll(newResults)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)
        val tvRiskLevel: TextView = itemView.findViewById(R.id.tv_risk_level)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvPermCount: TextView = itemView.findViewById(R.id.tv_perm_count)
    }
}