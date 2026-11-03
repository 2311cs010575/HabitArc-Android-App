# HabitArc: AI-Driven Digital Wellbeing & Financial Advisor

[![Android Platform](https://img.shields.io/badge/Platform-Android_SDK_37-green.svg)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin_2.1.10-blue.svg)](https://kotlinlang.org/)
[![UI Framework](https://img.shields.io/badge/UI-Jetpack_Compose_MD3-purple.svg)](https://developer.android.com/jetpack/compose)
[![AI Engine](https://img.shields.io/badge/AI_Engine-Groq_Llama_3.3_70B-orange.svg)](https://groq.com)
[![Database](https://img.shields.io/badge/Storage-Room_SQLite_Database-red.svg)](https://developer.android.com/training/data-storage/room)
[![Dependency Injection](https://img.shields.io/badge/DI-Dagger_Hilt-yellow.svg)](https://dagger.dev/hilt/)

---

## 📌 Project Abstract & Motivation

In today's hyper-connected world, individuals face two deeply intertwined behavioral challenges: **digital overconsumption** (excessive app usage and smartphone addiction) and **impulsive financial spending**. Mindless screen time spent on social media or e-commerce apps directly drives unbudgeted online purchases.

**HabitArc** is a unified Android application designed to bridge the gap between digital wellness and personal finance. Built using modern Android architecture, HabitArc tracks real-time foreground app usage metrics via Android's `UsageStatsManager`, tracks categorized financial expenses, and utilizes the **Groq API running Llama 3.3 (70B parameters)** to generate next-day screen time forecasts, weekly spending projections, and actionable wellbeing advice.

---

## 🚀 Key Features

### 1. 📱 Real-Time Digital Wellbeing & App Usage Tracker
* Queries Android's `UsageStatsManager` system service to record active foreground application screen time and launch counts.
* Filters out system background processes (`android`, `systemui`, launchers) to deliver clean app usage statistics.
* Provides a dynamic fallback usage calculator using activity package hashes if system usage permissions are pending.

### 2. 💰 Expense & Budget Manager
* Full CRUD capabilities (Add, Edit, Update, Delete) for categorized financial entries (e.g., Food, Transport, Shopping, Entertainment).
* Dynamic monthly budget threshold tracking with circular and linear progress indicators.
* Categorized spending distribution breakdown.

### 3. 📈 Interactive Analytics & Custom Canvas Charts
* Visualizes 7-day screen time trends (in hours) and spending habits (in ₹).
* Custom hardware-accelerated **Canvas rendering** utilizing **Cubic Bézier Spline Curves** for smooth trendlines with dynamic vertical color gradients.

### 4. 🤖 Groq LLM AI Predictive Engine (`llama-3.3-70b-versatile`)
* Sends 7-day usage summaries and categorized spending history to Groq's high-speed AI inference API.
* Enforces strict structured JSON schema responses to return:
  1. Next-day screen time predictions (in minutes).
  2. Weekly financial spending projections (in ₹).
  3. Trend indicators (`UP`, `DOWN`, `STABLE`).
  4. Context-aware, actionable digital wellbeing advice.
* Offline heuristic fallback engine for seamlessly continuing operation when disconnected from the network.

### 5. ⚡ Automated Background Sync & Persistent Storage
* **Android WorkManager (`AppUsageWorker`)** periodically updates usage statistics in SQLite every hour.
* **Room Database** acts as the single source of truth for users, usage logs, expenses, predictions, and notifications.
* **SharedPreferences** securely stores authentication tokens and custom API keys.

---

## 🛠️ Tech Stack & Development Tools

| Component | Technology Used | Description |
| :--- | :--- | :--- |
| **Language** | **Kotlin (v2.1.10)** | Modern, null-safe Kotlin with Coroutines & StateFlow. |
| **UI Toolkit** | **Jetpack Compose (Material 3)** | Declarative UI framework with custom glassmorphism styling. |
| **Architecture** | **MVVM + Repository Pattern** | Clean separation of UI, ViewModel, Repository, and DAOs. |
| **Dependency Injection** | **Dagger Hilt** | Automated DI for ViewModels, Repositories, DB, and Workers. |
| **Local Database** | **Room DB (SQLite)** | Offline-first local storage for user data and logs. |
| **Background Sync** | **Android WorkManager** | Hourly periodic background execution via `AppUsageWorker`. |
| **AI Inference** | **Groq REST API (`Llama 3.3 70B`)** | High-throughput Large Language Model endpoint. |
| **HTTP Client** | **OkHttp 3** | Lightweight asynchronous REST network client. |
| **Custom Graphics** | **Android Canvas API** | Hardware-accelerated Bézier curve line and bar charts. |
| **Compiler & Tools** | **Gradle 9.2.1 / KSP 1.0.31** | Build automation and Kotlin Symbol Processing. |

---

## 🧮 Core Algorithms Explained

### 1. Generative LLM Predictive Inference Algorithm
* **File**: [`GroqApiClient.kt`](file:///c:/Users/prashanth/AndroidStudioProjects/MyApplication2/app/src/main/java/com/example/aiusage/ai/GroqApiClient.kt)
* **Logic**: Summarizes 7-day app usage per app name (in hours) and financial spending per category (in ₹). Constructs a structured system prompt requiring Groq's API (`llama-3.3-70b-versatile`) to return a strict JSON payload:
  $$\text{Prompt Payload} = f(\text{UsageSummary}, \text{ExpenseBreakdown}, \text{MonthlyBudget})$$

### 2. Cubic Bézier Spline Curve Interpolation Algorithm
* **File**: [`LineChart.kt`](file:///c:/Users/prashanth/AndroidStudioProjects/MyApplication2/app/src/main/java/com/example/aiusage/charts/LineChart.kt)
* **Logic**: Calculates dynamic control points for smooth line rendering across data points $(x_0, y_0)$ and $(x_1, y_1)$:
  $$CP_{1x} = x_0 + \frac{x_1 - x_0}{2}, \quad CP_{1y} = y_0$$
  $$CP_{2x} = x_0 + \frac{x_1 - x_0}{2}, \quad CP_{2y} = y_1$$

### 3. Usage Aggregation & System Process Filtering Algorithm
* **File**: [`UsageStatsHelper.kt`](file:///c:/Users/prashanth/AndroidStudioProjects/MyApplication2/app/src/main/java/com/example/aiusage/utils/UsageStatsHelper.kt)
* **Logic**: Filters system launchers (`com.android.launcher`, `systemui`, `googleplay`) and aggregates foreground duration by human-readable app labels to prevent duplicated counts.

### 4. Dynamic Hash-Based Usage Approximation (Fallback Engine)
* **File**: [`UsageStatsHelper.kt`](file:///c:/Users/prashanth/AndroidStudioProjects/MyApplication2/app/src/main/java/com/example/aiusage/utils/UsageStatsHelper.kt)
* **Logic**: Generates dynamic estimates when system permission is pending using package name hash codes:
  $$\text{EstimatedMinutes} = \left((\text{packageName.hashCode()} \bmod 75) + 15\right) \times \frac{\text{CurrentHour}}{24}$$

---

## 📂 Codebase Directory Structure

```
com.example.aiusage/
│
├── ai/                      # Groq AI Client & JSON schema payload parser
│   └── GroqApiClient.kt
│
├── charts/                  # Custom Canvas graphics for line & bar charts
│   ├── BarChart.kt
│   └── LineChart.kt
│
├── dao/                     # Room DB DAOs (Data Access Objects)
│   ├── BudgetDao.kt
│   ├── ExpenseDao.kt
│   ├── NotificationDao.kt
│   ├── PredictionDao.kt
│   ├── UsageDao.kt
│   └── UserDao.kt
│
├── database/                # SQLite database configuration
│   └── AppDatabase.kt
│
├── di/                      # Dagger Hilt Dependency Injection modules
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── entity/                  # Room Database Entities
│   ├── Budget.kt
│   ├── Expense.kt
│   ├── NotificationRecord.kt
│   ├── PredictionRecord.kt
│   ├── UsageRecord.kt
│   └── User.kt
│
├── navigation/              # Compose Navigation Controller & NavGraph
│   ├── NavGraph.kt
│   └── Screen.kt
│
├── repository/              # Repository Layer (Interfaces & Impl)
│   ├── ExpenseRepository.kt & ExpenseRepositoryImpl.kt
│   ├── UsageRepository.kt & UsageRepositoryImpl.kt
│   ├── PredictionRepository.kt & PredictionRepositoryImpl.kt
│   └── UserRepository.kt & UserRepositoryImpl.kt
│
├── screens/                 # Jetpack Compose Screens (UI Layer)
│   ├── HomeScreen.kt
│   ├── UsageScreen.kt
│   ├── ExpenseScreen.kt
│   ├── AddExpenseScreen.kt & EditExpenseScreen.kt
│   ├── PredictionScreen.kt
│   ├── NotificationsScreen.kt
│   ├── ReportsScreen.kt
│   ├── ProfileScreen.kt
│   ├── BudgetManagerScreen.kt
│   ├── LoginScreen.kt & RegisterScreen.kt
│   └── MainAppScaffold.kt
│
├── utils/                   # System helpers & UsageStats query tools
│   └── UsageStatsHelper.kt
│
├── viewmodel/               # Android ViewModels (State Management)
│   ├── AuthViewModel.kt
│   ├── DashboardViewModel.kt
│   ├── ExpenseViewModel.kt
│   ├── NotificationViewModel.kt
│   ├── PredictionViewModel.kt
│   └── SplashViewModel.kt
│
└── worker/                  # WorkManager background worker
    └── AppUsageWorker.kt
```

---

## ⚡ How to Build & Run

### Prerequisites
* **Android Studio**: Studio Jellyfish / Ladybug or newer.
* **JDK**: OpenJDK 17 or Java 21.
* **Device / Emulator**: Android 8.0 (API Level 26) or higher.

### Step-by-Step Instructions
1. **Clone & Open**: Open the project root directory in Android Studio.
2. **Gradle Sync**: Allow Android Studio to sync dependencies via Gradle 9.2.1.
3. **Build APK via Command Line**:
   ```bash
   ./gradlew clean assembleDebug
   ```
4. **Install on Device**:
   ```bash
   ./gradlew installDebug
   ```
5. **Grant Usage Access**: On first launch, open the alert banner to grant **Usage Access** permissions for HabitArc in Android System Settings.

---

## 🎓 Guide for Project Examiner Presentation

When demonstrating this project to evaluators:

1. **Highlight Architecture**: Explain the clean separation between UI (Compose), State Management (ViewModel), Business Logic (Repositories), and Persistence (Room DB).
2. **Demonstrate AI Integration**: Show how raw log data (screen time & expenses) is aggregated and converted into structured prompts sent to Groq's Llama 3.3 70B model.
3. **Show Custom Canvas Drawing**: Point out that the charts are custom-built using Kotlin Canvas math (Bézier curves), avoiding heavy third-party dependencies.
4. **Showcase Background Reliability**: Explain how `AppUsageWorker` keeps usage data updated in the background even when the app is closed.
