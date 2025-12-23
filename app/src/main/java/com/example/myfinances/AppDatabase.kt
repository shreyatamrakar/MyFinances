package com.example.myfinances

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfinances.dao.CDAccountDao
import com.example.myfinances.dao.CheckingAccountDao
import com.example.myfinances.dao.LoanAccountDao
import com.example.myfinances.model.CDAccount
import com.example.myfinances.model.CheckingAccount
import com.example.myfinances.model.LoanAccount

@Database(
    entities = [CDAccount::class, LoanAccount::class, CheckingAccount::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cdAccountDao(): CDAccountDao
    abstract fun loanAccountDao(): LoanAccountDao
    abstract fun checkingAccountDao(): CheckingAccountDao
}