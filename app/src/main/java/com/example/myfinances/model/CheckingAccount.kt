package com.example.myfinances.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checking_accounts")
data class CheckingAccount(
    @PrimaryKey val accountNumber: String,
    val currentBalance: Double
)