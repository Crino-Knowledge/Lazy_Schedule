# 冰精殿下闪耀忙碌的日程

Cirno's sparkling and bustling daily schedule

一个移动端优先的**长期任务甘特图日程表**，支持 Android 桌面组件（Widget）。网页版单文件、无框架依赖，可直接在浏览器中运行预览；通过 Capacitor 打包为 Android App。

## 功能特性

- **横向时间轴甘特图**：日期轴在上，任务条横跨多天，每天分为 `0-8 / 8-16 / 16-24` 三段
- **固定红线游标**：红线代表"今天 / 当前选中日"，滑动内容时自动定位并实时显示日期
- **紧急度渐变色**：按"距截止天数"红 → 橙 → 黄 → 绿 → 灰平滑渐变，相对选中日期动态计算
- **圆形表盘时钟**：外圈 24 小时 + 内圈分钟，支持点击数字或按住拖动（按下即锁定目标圈）
- **完成归档**：任务不删除，勾选完成即归档；"进行中 / 已完成"两个互斥视图
- **导出 / 导入**：JSON 纯文本，支持复制、系统分享、下载文件，以及粘贴 / 上传导入
- **桌面组件（Widget）**：显示最近 4 个未完成任务 + 紧急度颜色，点击打开 App

## 技术栈

- **前端**：原生 HTML / CSS / JavaScript（单文件，零框架）
- **移动端**：Capacitor 8（WebView 壳 + 原生 Widget）
- **桌面组件**：原生 Java（`AppWidgetProvider`）+ `RemoteViews`

## 目录结构

```
.
├── index.html                 # 网页版源码（可直接用浏览器打开预览）
├── www/                       # Capacitor webDir（网页源码，构建时打包进 App）
├── capacitor.config.json      # Capacitor 配置
├── package.json               # npm 依赖与脚本
├── android/                   # Android 原生工程
│   └── app/src/main/
│       ├── java/com/lazy/schedule/
│       │   ├── MainActivity.java             # 入口，注入 JS↔原生数据桥
│       │   ├── ScheduleBridge.java           # 数据同步（localStorage → SharedPreferences）
│       │   └── ScheduleWidgetProvider.java   # 桌面组件
│       ├── res/layout/widget_schedule.xml    # 组件布局
│       └── res/xml/schedule_widget_info.xml  # 组件配置
└── node_modules/              # 依赖（npm install 生成，不提交）
```

## 本地开发（网页版）

直接用浏览器打开 `index.html` 或 `www/index.html` 即可预览，无需构建。数据保存在浏览器 `localStorage`。

## 打包 Android App

```bash
# 1. 安装依赖
npm install

# 2. 同步网页资源到 Android 工程
npx cap sync android

# 3. 构建 APK（需已安装 Android Studio 及 JDK 17+）
cd android
./gradlew assembleDebug
# Windows 用 gradlew.bat
```

产物位于 `android/app/build/outputs/apk/debug/app-debug.apk`。

### 国内构建：镜像加速

默认的 Gradle / Maven 源在国内可能超时，本项目已在以下位置预置了国内镜像（腾讯云 Gradle、阿里云 Maven）：

- `android/gradle/wrapper/gradle-wrapper.properties`
- `android/build.gradle`

若仍遇到下载超时，可参考上述文件，将 `node_modules/@capacitor/android/capacitor/build.gradle` 内的 `google()` / `mavenCentral()` 前同样加上阿里云镜像仓库。

## 许可证

[MIT](./LICENSE)
