package com.sentinel.app.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.activities.AppDetailActivity
import com.sentinel.app.activities.ThreatDetailActivity
import com.sentinel.app.models.AppInfo
import java.util.Locale

class AppListAdapter(
    private val context: Context,
    private var apps: MutableList<AppInfo>
) : RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    private var appsFull: MutableList<AppInfo> = apps.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.tvAppName.text = app.appName
        holder.tvPackageName.text = app.packageName
        holder.tvRiskLevel.text = app.riskLevel

        // Set risk color based on level

        val colorResId = when (app.riskLevel.lowercase()) {
            "high" -> R.color.risk_high
            "medium" -> R.color.risk_medium
            else -> R.color.risk_safe
        }
        holder.tvRiskLevel.setTextColor(ContextCompat.getColor(context, colorResId))

        // F2: Click listener to pass bundle data
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ThreatDetailActivity::class.java)
            intent.putExtra("app_name", app.appName)
            intent.putExtra("package_name", app.packageName)
            intent.putExtra("risk_level", app.riskLevel)
            intent.putExtra("risk_score", app.riskScore)
            intent.putStringArrayListExtra("permissions", ArrayList(app.permissions.map { it.name }))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = apps.size

    // F5: Search/Filter feature
    fun filter(query: String?) {
        if (query.isNullOrBlank()) {
            apps.clear()
            apps.addAll(appsFull)
        } else {
            val filtered = mutableListOf<AppInfo>()
            val lowerQuery = query.lowercase(Locale.getDefault())
            for (app in appsFull) {
                if (app.appName.lowercase().contains(lowerQuery) ||
                    app.packageName.lowercase().contains(lowerQuery)) {
                    filtered.add(app)
                }
            }
            apps.clear()
            apps.addAll(filtered)
        }
        notifyDataSetChanged()
    }

    fun updateData(newApps: List<AppInfo>) {
        apps.clear()
        apps.addAll(newApps)
        appsFull.clear()
        appsFull.addAll(newApps)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppName: TextView = itemView.findViewById(R.id.tv_app_name)
        val tvPackageName: TextView = itemView.findViewById(R.id.tv_package_name)
        val tvRiskLevel: TextView = itemView.findViewById(R.id.tv_risk_level)
    }
}