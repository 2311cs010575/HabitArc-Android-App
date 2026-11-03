# ABSTRACT

### **Title:** HabitArc: An AI-Driven Digital Wellbeing & Financial Advisor
**Author/Developer:** Prashanth  
**Platform:** Android (Kotlin, Jetpack Compose, Room DB, Dagger Hilt)  
**AI Inference Engine:** Groq API (`llama-3.3-70b-versatile`)  

---

### **Abstract**

In the modern digital era, individuals face two interconnected behavioral challenges: **digital overconsumption** (excessive screen time and app addiction) and **impulsive financial spending**. Although excessive device usage and impulsive spending are often closely linked—for instance, mindless screen time spent on social media or e-commerce apps leading directly to unbudgeted online purchases—existing solutions treat these issues in isolation. Users are forced to split their attention between dedicated screen-time trackers and standalone budgeting applications, losing the critical correlation between their digital habits and their financial expenses.

This project introduces **HabitArc**, a unified Android application designed to bridge the gap between digital wellbeing and budget management. Developed using modern Android frameworks—including **Jetpack Compose** for a reactive, glassmorphic user interface, **Room DB** for offline-first local data persistence, **Dagger Hilt** for structured dependency injection, and **WorkManager** for scheduled background data synchronization—HabitArc provides a central dashboard for self-reflection.

The application securely queries Android’s `UsageStatsManager` to gather daily foreground app usage metrics, alongside manual budget and expense tracking features. To convert this raw log data into meaningful user action, HabitArc integrates with the high-performance **Groq API** running the **Llama 3.3 (70B parameters)** model. The AI engine processes weekly aggregated screen usage logs and financial expense histories to generate:
1. **Next-day screen time usage predictions** using linear trends.
2. **Weekly financial spending projections** against user-defined budget thresholds.
3. **Actionable, context-aware digital wellbeing recommendations** to promote healthier screen habits and financial behaviors.

By presenting this data in a sleek interface featuring custom Canvas-drawn trend charts, circular budget gauges, and reactive AI notifications, HabitArc provides a holistic, proactive coaching mechanism. The result is a unified tool that fosters balanced device usage and mindful financial spending, helping users build healthier habits from the ground up.

**Keywords:** *Android Development, Kotlin, Jetpack Compose, MVVM Architecture, Digital Wellbeing, Personal Finance, Dagger Hilt, WorkManager, Large Language Models (LLM), Groq API.*
