package com.example.myfinances.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loan_accounts")
data class LoanAccount(
    @PrimaryKey val accountNumber: String,
    val initialBalance: Double,
    val currentBalance: Double,
    val paymentAmount: Double,
    val interestRate: Double
)