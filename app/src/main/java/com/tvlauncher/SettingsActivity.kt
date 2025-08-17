package com.tvlauncher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        sharedPreferences = sharedPreferences,
                        onNavigateToAbout = {
                            // 跳转到关于界面
                            val intent = Intent(this, AboutActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier, 
    sharedPreferences: SharedPreferences,
    onNavigateToAbout: () -> Unit
) {
    val context = LocalContext.current
    val selectedAppPackageName = remember {
        sharedPreferences.getString(SettingsActivity.PREF_AUTO_START_APP, null)
    }
    
    // 获取应用名称
    val selectedAppName = remember(selectedAppPackageName) {
        if (selectedAppPackageName != null) {
            val apps = loadInstalledApps(context)
            apps.find { it.packageName == selectedAppPackageName }?.name ?: "未知应用"
        } else {
            "未设置"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 自启应用设置项
        item {
            SettingItem(
                title = "自启应用：",
                value = selectedAppName,
                onClick = {
                    // 打开应用选择界面
                    val intent = Intent(context, AppSelectorActivity::class.java)
                    context.startActivity(intent)
                }
            )
        }
        
        // 设置默认桌面项
        item {
            SettingItem(
                title = "设置默认桌面",
                onClick = {
                    // 调用系统选择默认桌面的功能
                    try {
                        val intent = Intent("android.settings.HOME_SETTINGS")
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // 如果没有找到处理该intent的应用，则使用备用方案
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                    }
                }
            )
        }
        
        // 关于项
        item {
            SettingItem(
                title = "关于",
                onClick = onNavigateToAbout
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingItem(
    title: String,
    value: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            
            value?.let {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            
            // 箭头图标表示可点击
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "导航",
                tint = Color.Gray
            )
        }
    }
}

// 加载已安装应用的简化版本
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
        
        // 在设置界面中我们不需要启动意图和图标
        apps.add(AppInfo(appName, packageName, null, null))
    }
    
    // 按名称排序
    apps.sortBy { it.name }
    
    return apps
}