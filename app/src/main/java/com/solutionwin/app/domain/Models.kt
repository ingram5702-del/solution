package com.solutionwin.app.domain

data class Note(
    val id: Long,
    val title: String,
    val body: String,
    val createdAt: Long,
)

enum class SportEventType { MATCH, TRAINING }

data class SportEvent(
    val id: Long,
    val title: String,
    val type: SportEventType,
    val startAt: Long,
    val reminderMinutes: Int,
)

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
)
