package com.tvlauncher.model

import android.content.Intent
import android.graphics.Bitmap

data class AppInfo(
    val name: String,
    val packageName: String,
    val launchIntent: Intent?,
    val icon: Bitmap?
)