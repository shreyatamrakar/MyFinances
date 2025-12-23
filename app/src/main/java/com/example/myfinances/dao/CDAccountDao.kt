package com.example.myfinances.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myfinances.model.CDAccount

@Dao
interface CDAccountDao {
    @Insert
    suspend fun insertCDAccount(cd: CDAccount)

    @Query("SELECT * FROM cd_accounts")
    suspend fun getAllCDAccount(): List<CDAccount>
}