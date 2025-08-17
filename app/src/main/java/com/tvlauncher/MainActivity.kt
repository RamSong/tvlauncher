package com.tvlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tvlauncher.ui.theme.TvlauncherTheme
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.KeyEvent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.Key.Companion.Menu
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.tvlauncher.model.AppInfo

const val PREF_AUTO_START_APP = "auto_start_app"

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
        const val PREFS_NAME = "launcher_prefs"
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        setContent {
            TvlauncherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppGrid(modifier = Modifier.padding(innerPadding), sharedPreferences = sharedPreferences)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {
                // 菜单键打开设置界面
                openSettings()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun AppGrid(modifier: Modifier = Modifier, sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val apps: List<AppInfo> = remember { loadInstalledApps(context) }
    val selectedApp = remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(6),
        modifier = modifier
            .fillMaxSize()
            .background(
                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface
            ),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 添加标题行
        item(span = { GridItemSpan(6) }) {
            Text(
                text = "TV Launcher",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        items(items = apps) { app: AppInfo ->
            AppItem(
                app = app,
                isSelected = app.packageName == selectedApp.value,
                onClick = {
                    // 启动应用
                    app.launchIntent?.let { context.startActivity(it) }
                },
                onSelectAutoStart = {
                    // 设置为开机自启应用
                    selectedApp.value = app.packageName
                    with(sharedPreferences.edit()) {
                        putString(PREF_AUTO_START_APP, app.packageName)
                        apply()
                    }
                }
            )
        }

        // 添加设置按钮
        item(span = { GridItemSpan(6) }) {
            Button(
                onClick = {
                    // 打开设置界面
                    val intent = Intent(context, SettingsActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "设置",
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun AppItem(
    app: AppInfo,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSelectAutoStart: () -> Unit
) {
    val borderModifier = if (isSelected) {
        Modifier.border(4.dp, Color.Yellow)
    } else {
        Modifier
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable { onClick() }
            .focusable()
            .then(borderModifier)
    ) {
        androidx.compose.foundation.layout.Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            app.icon?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(80.dp)
                        .align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            }
            Text(
                text = app.name,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                maxLines = 2
            )
            
            if (isSelected) {
                Text(
                    text = "★",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    color = Color.Yellow
                )
            }
        }
    }
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

private fun loadInstalledApps(context: Context): List<AppInfo> {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    
    val apps = mutableListOf<AppInfo>()
    val pm = context.packageManager
    val activities: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
    
    for (resolveInfo in activities) {
        val appInfo = resolveInfo.activityInfo
        val appName = appInfo.loadLabel(pm).toString()
        val packageName = appInfo.packageName
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        
        val icon = try {
            drawableToBitmap(appInfo.loadIcon(pm))
        } catch (e: Exception) {
            null
        }
        
        apps.add(AppInfo(appName, packageName, launchIntent, icon))
    }
    
    // 按名称排序
    apps.sortBy { it.name }
    
    return apps
}