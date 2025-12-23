package com.example.myfinances.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cd_accounts")
data class CDAccount(
    @PrimaryKey val accountNumber: String,
    val initialBalance: Double,
    val currentBalance: Double,
    val interestRate: Double
)
