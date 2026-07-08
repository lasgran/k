package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String,
    val role: String // "Administrador" or "Funcionário"
)

@Entity(tableName = "production_records")
data class ProductionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val shift: String, // "Manhã" or "Noite"
    val user: String, // Username of creator
    val timestamp: Long,
    // Production items
    val uramakiSalmao: Int,
    val uramakiShimeji: Int,
    val uramakiSkin: Int,
    val uramakiGrelhado: Int,
    val nigiriSalmao: Int,
    val nigiriSkin: Int,
    val jow: Int,
    val batera: Int
)

@Entity(tableName = "closing_records")
data class ClosingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD (one closing per day)
    val user: String, // Username of closer
    val timestamp: Long,
    val lomboRestante: Double,
    val barrigaRestante: Double,
    val observations: String = "",
    val sobraUramakiSalmao: Int = 0,
    val sobraUramakiShimeji: Int = 0,
    val sobraUramakiSkin: Int = 0,
    val sobraUramakiGrelhado: Int = 0,
    val sobraNigiriSalmao: Int = 0,
    val sobraNigiriSkin: Int = 0,
    val sobraJow: Int = 0,
    val sobraBatera: Int = 0
)
