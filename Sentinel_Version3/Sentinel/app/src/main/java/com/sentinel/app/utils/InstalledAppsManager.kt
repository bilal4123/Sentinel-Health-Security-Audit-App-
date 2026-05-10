package com.sentinel.app.utils


import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import com.sentinel.app.models.AppInfo
import com.sentinel.app.models.Permission
import java.io.ByteArrayOutputStream
import java.util.Base64

class InstalledAppsManager(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    /**
     * Get all installed apps with their permissions and security analysis
     */
    fun getAllInstalledApps(): List<AppInfo> {
        val installedApps = mutableListOf<AppInfo>()

        // Get all installed packages (excluding system apps if desired)
        val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledPackages(0)
        }

        for (packageInfo in packages) {
            // Skip system apps (optional - remove if you want to include them)
            if (isSystemApp(packageInfo)) continue

            val appInfo = getAppInfoFromPackage(packageInfo)
            if (appInfo != null) {
                installedApps.add(appInfo)
            }
        }

        // Sort by risk score (highest first)
        return installedApps.sortedByDescending { it.riskScore }
    }

    /**
     * Get detailed AppInfo from PackageInfo
     */
    private fun getAppInfoFromPackage(packageInfo: android.content.pm.PackageInfo): AppInfo? {
        val appName = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString() ?: return null
        val packageName = packageInfo.packageName
        val version = packageInfo.versionName ?: "Unknown"

        // Get requested permissions
        val permissions = getPermissionsForPackage(packageInfo)

        // Calculate risk
        val analyzer = PermissionAnalyzer(context)
        val riskLevel = analyzer.calculateRiskLevel(permissions)
        val riskScore = analyzer.calculateRiskScore(permissions)

        // Get icon as Base64 (optional for displaying)
        val iconBase64 = getAppIconBase64(packageInfo)

        // Get target SDK version
        val apiLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageInfo.applicationInfo?.targetSdkVersion ?: 0
        } else {
            0
        }

        // Check for cleartext traffic (requires deeper manifest parsing)
        val cleartextTraffic = checkCleartextTraffic(packageInfo)

        return AppInfo(
            appName = appName,
            packageName = packageName,
            version = version,
            riskScore = riskScore,
            riskLevel = riskLevel,
            permissions = permissions,
            apiLevel = apiLevel,
            cleartextTraffic = cleartextTraffic,
            iconBase64 = iconBase64
        )
    }

    /**
     * Get all permissions requested by an app
     */
    private fun getPermissionsForPackage(packageInfo: android.content.pm.PackageInfo): List<Permission> {
        val permissions = mutableListOf<Permission>()

        val requestedPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageInfo.requestedPermissions ?: emptyArray()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.requestedPermissions ?: emptyArray()
        }

        for (permName in requestedPermissions) {
            val protectionLevel = getPermissionProtectionLevel(permName)
            val icon = getPermissionIcon(permName)
            permissions.add(Permission(permName, protectionLevel, icon))
        }

        return permissions
    }

    /**
     * Get protection level of a permission (dangerous or normal)
     */
    private fun getPermissionProtectionLevel(permName: String): String {
        return try {
            val permissionInfo = packageManager.getPermissionInfo(permName, 0)
            val protectionLevel = permissionInfo.protectionLevel

            when {
                protectionLevel and android.content.pm.PermissionInfo.PROTECTION_DANGEROUS != 0 -> "dangerous"
                else -> "normal"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            "unknown"
        }
    }

    /**
     * Get icon for permission
     */
    private fun getPermissionIcon(permName: String): String {
        return when {
            permName.contains("LOCATION") -> "📍"
            permName.contains("CAMERA") -> "📷"
            permName.contains("MICROPHONE") || permName.contains("RECORD_AUDIO") -> "🎤"
            permName.contains("CONTACTS") -> "👥"
            permName.contains("STORAGE") || permName.contains("WRITE_EXTERNAL") -> "💾"
            permName.contains("SMS") -> "💬"
            permName.contains("PHONE") -> "📞"
            permName.contains("CALENDAR") -> "📅"
            permName.contains("BODY_SENSORS") -> "❤️"
            else -> "🔘"
        }
    }

    /**
     * Get app icon as Base64 string
     */
    private fun getAppIconBase64(packageInfo: android.content.pm.PackageInfo): String {
        return try {
            val iconDrawable = packageInfo.applicationInfo?.loadIcon(packageManager)
            if (iconDrawable != null) {
                val bitmap = drawableToBitmap(iconDrawable)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Base64.getEncoder().encodeToString(byteArray)
                } else {
                    android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT)
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Convert Drawable to Bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * Check if app is a system app
     */
    private fun isSystemApp(packageInfo: android.content.pm.PackageInfo): Boolean {
        return (packageInfo.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_SYSTEM)) != 0
    }

    /**
     * Check if app uses cleartext traffic (simplified - requires manifest parsing)
     */
    private fun checkCleartextTraffic(packageInfo: android.content.pm.PackageInfo): Boolean {
        // This is simplified - full implementation requires parsing AndroidManifest.xml
        // Default to false for now
        return false
    }
}