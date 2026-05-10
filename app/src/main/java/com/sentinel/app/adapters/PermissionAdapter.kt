package com.sentinel.app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.models.Permission

class PermissionAdapter(
    private var permissions: List<Permission>
) : RecyclerView.Adapter<PermissionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_permission, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val perm = permissions[position]
        holder.tvIcon.text = perm.icon
        holder.tvPermName.text = perm.name
        holder.tvPermDesc.text = perm.description

        if (perm.isDangerous) {
            holder.tvStatus.text = "DANGEROUS"
            holder.tvStatus.setTextColor(Color.parseColor("#FF5252"))
        } else {
            holder.tvStatus.text = "NORMAL"
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))        }
    }

    override fun getItemCount(): Int = permissions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcon: TextView = itemView.findViewById(R.id.tv_icon)
        val tvPermName: TextView = itemView.findViewById(R.id.tv_perm_name)
        val tvPermDesc: TextView = itemView.findViewById(R.id.tv_perm_desc)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
    }
}