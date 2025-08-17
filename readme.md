# TV Launcher

一个专为Android TV设计的定制启动器应用，提供简洁直观的用户界面和实用功能。

## 功能特性

### 主要功能
- 应用网格显示：以6列布局展示设备上所有已安装的应用
- 遥控器导航支持：完全适配遥控器操作，可通过方向键导航，OK键启动应用
- 开机自启：可设置特定应用在设备启动时自动运行
- 适老化设计：采用大图标、大文字和高对比度配色方案，方便老年人使用
- 设置界面：提供友好的设置界面用于配置开机自启应用

### 技术特性
- 使用Kotlin编写，充分利用现代Android开发技术
- 采用Jetpack Compose构建界面，提供流畅的用户体验
- 支持Android TV标准Launcher功能

## 技术规格

### 开发环境
- **编译SDK版本**: 36 (Android 17)
- **目标SDK版本**: 36 (Android 17)
- **最低SDK版本**: 24 (Android 7.0)

### 核心技术栈
- **语言**: Kotlin 2.0.21
- **UI框架**: Jetpack Compose
- **构建工具**: Gradle 8.12.0
- **JVM版本**: Java 11

### 主要依赖
- AndroidX Core KTX 1.10.1
- AndroidX Lifecycle Runtime KTX 2.6.1
- AndroidX Activity Compose 1.8.0
- Jetpack Compose BOM 2024.09.00

<img width="1440" height="960" alt="Snipaste_2025-08-17_14-10-13" src="https://github.com/user-attachments/assets/317fdca2-057a-4989-9e78-b99da412de00" />


## 安装与使用

1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 同步Gradle依赖
4. 构建并部署到Android TV设备

## 使用说明

1. 在主界面，使用遥控器方向键在应用网格中导航
2. 按OK键启动选中的应用
3. 按菜单键打开设置界面
4. 在设置界面可配置开机自启应用

## 开发指南

### 项目结构
```
app/src/main/java/com/tvlauncher
├── MainActivity.kt         # 主界面Activity
├── SettingsActivity.kt     # 设置界面Activity
├── BootReceiver.kt         # 开机广播接收器
├── model/                  # 数据模型
└── ui/                     # 界面组件
```

### 核心组件
- `MainActivity`: 应用主界面，显示应用网格
- `SettingsActivity`: 设置界面，用于配置开机自启应用
- `BootReceiver`: 监听开机广播，启动配置的自启应用

## 许可证

本项目仅供学习和参考使用。


