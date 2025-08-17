package com.tvlauncher

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "BootReceiver"
        private const val PREFS_NAME = "launcher_prefs"
        private const val PREF_AUTO_START_APP = "auto_start_app"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d(TAG, "Device boot completed")

            // 从SharedPreferences获取自动启动的应用
            val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val autoStartPackageName = prefs.getString(PREF_AUTO_START_APP, null)

            if (!autoStartPackageName.isNullOrEmpty()) {
                try {
                    val pm: PackageManager = context.packageManager
                    val launchIntent = pm.getLaunchIntentForPackage(autoStartPackageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        Log.d(TAG, "Auto-started app: $autoStartPackageName")
                    } else {
                        Log.w(TAG, "No launch intent found for package: $autoStartPackageName")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching auto-start app: ${e.message}")
                }
            }
        }
    }
}