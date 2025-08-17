package com.tvlauncher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tvlauncher.ui.theme.TvlauncherTheme
import com.tvlauncher.model.AppInfo

class AppSelectorActivity : ComponentActivity() {
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
                                    text = "选择自启应用",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AppSelectorScreen(
                        modifier = Modifier.padding(innerPadding),
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
    }
}

@Composable
fun AppSelectorScreen(modifier: Modifier = Modifier, sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val apps = remember { loadInstalledApps(context) }
    val selectedApp = remember {
        mutableStateOf(
            sharedPreferences.getString(AppSelectorActivity.PREF_AUTO_START_APP, null)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = apps) { app: AppInfo ->
            AppSelectorItem(
                app = app,
                isSelected = app.packageName == selectedApp.value,
                onSelect = {
                    selectedApp.value = app.packageName
                    with(sharedPreferences.edit()) {
                        putString(AppSelectorActivity.PREF_AUTO_START_APP, app.packageName)
                        apply()
                    }
                    // 选择后返回上一个界面
                    (context as AppSelectorActivity).finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectorItem(
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

// 为AppSelectorActivity创建一个简化版的loadInstalledApps
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