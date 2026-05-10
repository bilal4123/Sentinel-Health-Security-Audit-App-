package com.sentinel.app.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sentinel.app.R
import com.sentinel.app.fragments.DashboardFragment
import com.sentinel.app.fragments.HistoryFragment
import com.sentinel.app.fragments.SettingsFragment
import com.sentinel.app.fragments.ThreatFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        val deviceName = intent.getStringExtra("device_name")
        if (!deviceName.isNullOrEmpty()) {
            Toast.makeText(this, "Welcome $deviceName", Toast.LENGTH_SHORT).show()
        }

        bottomNav = findViewById(R.id.bottom_navigation)
        bottomNav.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment())
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val selectedFragment = when (item.itemId) {
            R.id.nav_dashboard -> DashboardFragment()
            R.id.nav_threats -> ThreatFragment()
            R.id.nav_history -> HistoryFragment()
            R.id.nav_settings -> SettingsFragment()
            else -> null
        }
        return selectedFragment?.let { loadFragment(it) } ?: false
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }
}