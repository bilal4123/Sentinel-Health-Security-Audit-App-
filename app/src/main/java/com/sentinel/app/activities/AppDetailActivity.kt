package com.sentinel.app.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sentinel.app.R
import com.sentinel.app.adapters.PermissionAdapter
import com.sentinel.app.models.AppInfo

class AppDetailActivity : AppCompatActivity() {

    private lateinit var tvAppName: TextView
    private lateinit var tvPackageName: TextView
    private lateinit var tvVersion: TextView
    private lateinit var tvRiskLevel: TextView
    private lateinit var tvApi: TextView
    private lateinit var tvCleartext: TextView
    private lateinit var rvPermissions: RecyclerView

    private var appInfo: AppInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        initViews()

        // F2: Bundle - Receive custom object from Intent
        appInfo = intent.getSerializableExtra("app_info") as? AppInfo

        appInfo?.let { displayAppInfo(it) }
    }

    private fun initViews() {
        tvAppName = findViewById(R.id.tv_app_name)
        tvPackageName = findViewById(R.id.tv_package_name)
        tvVersion = findViewById(R.id.tv_version)
        tvRiskLevel = findViewById(R.id.tv_risk)
        tvApi = findViewById(R.id.tv_api)
        tvCleartext = findViewById(R.id.tv_cleartext)
        rvPermissions = findViewById(R.id.rv_permissions)
    }

    private fun displayAppInfo(app: AppInfo) {
        tvAppName.text = app.appName
        tvPackageName.text = app.packageName
        tvVersion.text = "v${app.version}"
        tvRiskLevel.text = "${app.riskLevel} RISK"
        tvApi.text = "API ${app.apiLevel}"
        tvCleartext.text = if (app.cleartextTraffic) "CLEARTEXT" else "SECURE"

        // Setup permission recycler
        val adapter = PermissionAdapter(app.permissions)
        rvPermissions.layoutManager = LinearLayoutManager(this)
        rvPermissions.adapter = adapter
    }
}