package com.sentinel.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "sentinel.db"
        const val DATABASE_VERSION = 1

        // Table 1: scanned_apps
        const val TABLE_APPS = "scanned_apps"
        const val COL_APP_ID = "id"
        const val COL_APP_NAME = "app_name"
        const val COL_PACKAGE = "package_name"
        const val COL_RISK = "risk_level"
        const val COL_SCORE = "risk_score"
        const val COL_DATE = "scan_date"

        // Table 2: permissions (linked to scanned_apps via Foreign Key)
        const val TABLE_PERMS = "permissions"
        const val COL_PERM_ID = "id"
        const val COL_PERM_APP_ID = "app_id"
        const val COL_PERM_NAME = "perm_name"
        const val COL_PERM_TYPE = "perm_type"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create Table 1: scanned_apps
        val createAppsTable = """
            CREATE TABLE $TABLE_APPS (
                $COL_APP_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_APP_NAME TEXT NOT NULL,
                $COL_PACKAGE TEXT NOT NULL,
                $COL_RISK TEXT NOT NULL,
                $COL_SCORE INTEGER NOT NULL,
                $COL_DATE TEXT NOT NULL
            )
        """.trimIndent()

        // Create Table 2: permissions with Foreign Key to scanned_apps
        val createPermsTable = """
            CREATE TABLE $TABLE_PERMS (
                $COL_PERM_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_PERM_APP_ID INTEGER NOT NULL,
                $COL_PERM_NAME TEXT NOT NULL,
                $COL_PERM_TYPE TEXT NOT NULL,
                FOREIGN KEY($COL_PERM_APP_ID) REFERENCES $TABLE_APPS($COL_APP_ID)
                ON DELETE CASCADE
            )
        """.trimIndent()

        db.execSQL(createAppsTable)
        db.execSQL(createPermsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPS")
        onCreate(db)
    }

    // ── CREATE: Insert a scanned app ──
    fun insertApp(
        appName: String,
        packageName: String,
        riskLevel: String,
        riskScore: Int,
        scanDate: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_APP_NAME, appName)
            put(COL_PACKAGE, packageName)
            put(COL_RISK, riskLevel)
            put(COL_SCORE, riskScore)
            put(COL_DATE, scanDate)
        }
        return db.insert(TABLE_APPS, null, values)
    }

    // ── CREATE: Insert a permission linked to an app ──
    fun insertPermission(appId: Long, permName: String, permType: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_PERM_APP_ID, appId)
            put(COL_PERM_NAME, permName)
            put(COL_PERM_TYPE, permType)
        }
        db.insert(TABLE_PERMS, null, values)
    }

    // ── READ: Get all scanned apps ──
    fun getAllApps(): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery("SELECT * FROM $TABLE_APPS ORDER BY $COL_DATE DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val row = mapOf(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_ID)),
                    "app_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_NAME)),
                    "package_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PACKAGE)),
                    "risk_level" to cursor.getString(cursor.getColumnIndexOrThrow(COL_RISK)),
                    "risk_score" to cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    "scan_date" to cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
                )
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ── READ: Search apps by name using LIKE ──
    fun searchApps(query: String): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_APPS WHERE $COL_APP_NAME LIKE ? ORDER BY $COL_DATE DESC",
            arrayOf("%$query%")
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mapOf(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_ID)),
                    "app_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_NAME)),
                    "package_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PACKAGE)),
                    "risk_level" to cursor.getString(cursor.getColumnIndexOrThrow(COL_RISK)),
                    "risk_score" to cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    "scan_date" to cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
                )
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ── READ: Get permissions for a specific app ──
    fun getPermissionsForApp(appId: String): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_PERMS WHERE $COL_PERM_APP_ID = ?",
            arrayOf(appId)
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mapOf(
                    "perm_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PERM_NAME)),
                    "perm_type" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PERM_TYPE))
                )
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    // ── UPDATE: Update risk level of an app ──
    fun updateAppRisk(appId: String, newRiskLevel: String, newScore: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_RISK, newRiskLevel)
            put(COL_SCORE, newScore)
        }
        return db.update(TABLE_APPS, values, "$COL_APP_ID = ?", arrayOf(appId))
    }

    // ── DELETE: Delete a scanned app (permissions auto-deleted via CASCADE) ──
    fun deleteApp(appId: String): Int {
        val db = writableDatabase
        return db.delete(TABLE_APPS, "$COL_APP_ID = ?", arrayOf(appId))
    }

    // ── Filter by risk level using ORDER BY ──
    fun getAppsSortedByRisk(): List<Map<String, String>> {
        val db = readableDatabase
        val list = mutableListOf<Map<String, String>>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_APPS ORDER BY $COL_SCORE DESC",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val row = mapOf(
                    "id" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_ID)),
                    "app_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_APP_NAME)),
                    "package_name" to cursor.getString(cursor.getColumnIndexOrThrow(COL_PACKAGE)),
                    "risk_level" to cursor.getString(cursor.getColumnIndexOrThrow(COL_RISK)),
                    "risk_score" to cursor.getString(cursor.getColumnIndexOrThrow(COL_SCORE)),
                    "scan_date" to cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
                )
                list.add(row)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}