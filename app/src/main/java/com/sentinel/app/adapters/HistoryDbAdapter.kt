package com.sentinel.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R

class HistoryDbAdapter(
    private var items: MutableList<Map<String, String>> = mutableListOf(),
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<HistoryDbAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppName: TextView = itemView.findViewById(R.id.tv_history_app_name)
        val tvRisk: TextView = itemView.findViewById(R.id.tv_history_risk)
        val tvPackage: TextView = itemView.findViewById(R.id.tv_history_package)
        val tvDate: TextView = itemView.findViewById(R.id.tv_history_date)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete_history)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_db, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]

        holder.tvAppName.text = item["app_name"] ?: ""
        holder.tvPackage.text = item["package_name"] ?: ""
        holder.tvDate.text = item["scan_date"] ?: ""

        val risk = item["risk_level"] ?: "LOW"
        holder.tvRisk.text = risk

        val color = when (risk.uppercase()) {
            "HIGH" -> 0xFFFF4757.toInt()
            "MEDIUM" -> 0xFFFFA502.toInt()
            else -> 0xFF2ED573.toInt()
        }
        holder.tvRisk.setTextColor(color)

        holder.btnDelete.setOnClickListener {
            val id = item["id"] ?: return@setOnClickListener
            onDeleteClick(id)
        }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Map<String, String>>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun filter(query: String, allItems: List<Map<String, String>>) {
        items.clear()
        if (query.isEmpty()) {
            items.addAll(allItems)
        } else {
            items.addAll(allItems.filter { item ->
                item["app_name"]?.contains(query, ignoreCase = true) == true
            })
        }
        notifyDataSetChanged()
    }
}