## 功能实现

### TV Launcher 配置：
在 AndroidManifest.xml 中添加了 TV Launcher 所需的 intent-filter
添加了必要的权限（查询所有应用、接收开机完成广播）
### 主界面：
使用 Jetpack Compose 实现了应用网格显示
采用 6 列布局展示所有已安装的应用
适老化设计：大图标、大文字、高对比度颜色
### 遥控器支持：
1. 界面元素支持焦点导航
2. 可以使用方向键在应用间导航
3. OK 键启动应用
4. 菜单键打开设置界面
### 开机自启功能：
实现了 SharedPreferences 保存选择的开机自启应用
创建了 BootReceiver 接收开机广播并启动指定应用
### 设置界面：
创建了 SettingsActivity 用于选择开机自启应用
实现了应用列表显示，支持选择并保存设置
手动调用设置默认桌面


#### Gradle 和构建工具版本
- **Gradle 版本**: 通过 [libs.versions.toml](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/gradle/libs.versions.toml) 文件中的 [agp = "8.12.0"](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/gradle/libs.versions.toml#L2-L2) 可知，使用的是 Android Gradle Plugin 8.12.0 版本
- **Kotlin 版本**: [kotlin = "2.0.21"](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/gradle/libs.versions.toml#L3-L3) 表示使用 Kotlin 2.0.21 版本

#### Android SDK 版本
- **编译 SDK 版本**: [compileSdk = 36](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/app/build.gradle.kts#L11-L11) (Android 17)
- **目标 SDK 版本**: [targetSdk = 36](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/app/build.gradle.kts#L16-L16) (Android 17)
- **最低 SDK 版本**: [minSdk = 24](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/app/build.gradle.kts#L15-L15) (Android 7.0)

#### Java 和 JVM 版本
- **Java 版本**: [JavaVersion.VERSION_11](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/app/build.gradle.kts#L32-L32) (Java 11)
- **JVM 目标版本**: [jvmTarget = "11"](file:///Users/ramsong/AndroidStudioProjects/tvlauncher/app/build.gradle.kts#L36-L36)
