package com.tvlauncher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.tvlauncher.ui.theme.TvlauncherTheme
import com.tvlauncher.model.AppInfo

class SettingsActivity : ComponentActivity() {
    companion object {
        const val PREFS_NAME = "launcher_prefs"
        const val PREF_AUTO_START_APP = "auto_start_app"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        setContent {
            TvlauncherTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "设置",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    SettingsScreen(
                        modifier = Modifier.padding(innerPadding),
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val apps: List<AppInfo> = remember { loadInstalledApps(context) }
    val selectedApp = remember {
        mutableStateOf(
            sharedPreferences.getString(PREF_AUTO_START_APP, null)
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "选择开机自启应用:",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = apps) { app: AppInfo ->
                AppSettingItem(
                    app = app,
                    isSelected = app.packageName == selectedApp.value,
                    onSelect = {
                        selectedApp.value = app.packageName
                        with(sharedPreferences.edit()) {
                            putString(PREF_AUTO_START_APP, app.packageName)
                            apply()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingItem(
    app: AppInfo,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 显示应用图标（如果有）
            app.icon?.let {
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier.size(48.dp)
                )
            } ?: run {
                // 如果没有图标，显示默认图标
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(48.dp)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(40.dp)
                            .aspectRatio(1f)
                            .background(Color.Gray)
                    )
                }
            }

            // 显示应用名称
            Text(
                text = app.name,
                fontSize = 20.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            // 显示选中状态
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Green,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// 为SettingsActivity创建一个简化版的loadInstalledApps
private fun loadInstalledApps(context: Context): List<AppInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    
    val apps = mutableListOf<AppInfo>()
    val pm = context.packageManager
    val activities: List<android.content.pm.ResolveInfo> = pm.queryIntentActivities(intent, 0)
    
    for (resolveInfo in activities) {
        val appInfo = resolveInfo.activityInfo
        val appName = appInfo.loadLabel(pm).toString()
        val packageName = appInfo.packageName
        
        val icon = try {
            drawableToBitmap(appInfo.loadIcon(pm))
        } catch (e: Exception) {
            null
        }
        
        // 在设置界面中我们不需要启动意图
        apps.add(AppInfo(appName, packageName, null, icon))
    }
    
    // 按名称排序
    apps.sortBy { it.name }
    
    return apps
}

private fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is android.graphics.drawable.BitmapDrawable) {
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