package com.example.myfinances.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myfinances.model.LoanAccount

@Dao
interface LoanAccountDao{
    @Insert
    suspend fun insertLoanAccount(cd: LoanAccount)

    @Query("SELECT * FROM loan_accounts")
    suspend fun getAllLoanAccount(): List<LoanAccount>
}