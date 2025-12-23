package com.example.myfinances.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myfinances.model.CheckingAccount

@Dao
interface CheckingAccountDao {
    @Insert
    suspend fun insertCheckingAccount(cd: CheckingAccount)

    @Query("SELECT * FROM checking_accounts")
    suspend fun getAllCheckingAccount(): List<CheckingAccount>
}