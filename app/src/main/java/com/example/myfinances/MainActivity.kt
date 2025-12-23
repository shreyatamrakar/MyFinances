package com.example.myfinances

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.myfinances.model.CDAccount
import com.example.myfinances.model.CheckingAccount
import com.example.myfinances.model.LoanAccount
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // creating an in-memory version of your Room database
        val db = Room.inMemoryDatabaseBuilder(
            applicationContext,
            AppDatabase::class.java
        ).build()

        enableEdgeToEdge()
        setContent {
            MaterialTheme { // using default theme for now
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyFinanceScreen(db = db)
                }
            }
        }
    }
}

/*@Composable annotation to mark function as a Composable, allowing to
describe part of the UI in a declarative way using Jetpack Compose*/
@Composable
fun MyFinanceScreen(modifier: Modifier = Modifier, db: AppDatabase? = null) {
    var accountType by remember { mutableStateOf("CD") } //default selection
    var accountNumber by remember { mutableStateOf("") }
    var initialBalance by remember { mutableStateOf("") }
    var currentBalance by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var paymentAmount by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope() // get coroutine scope for launching suspend work

    val isFormValid by remember {
        derivedStateOf {
            accountNumber.isNotBlank() && currentBalance.toDoubleOrNull() != null &&
                    when (accountType) {
                        "CD" -> initialBalance.toDoubleOrNull() != null &&
                                interestRate.toDoubleOrNull() != null

                        "Loan" -> initialBalance.toDoubleOrNull() != null &&
                                interestRate.toDoubleOrNull() != null &&
                                paymentAmount.toDoubleOrNull() != null

                        "Checking" -> true
                        else -> false
                    }
        }
    }

    /*logic to clear data form*/
    fun resetForm() {
        accountNumber = ""
        initialBalance = ""
        currentBalance = ""
        interestRate = ""
        paymentAmount = ""
        statusMessage = ""
    }

    fun saveAccount() {
        if (db == null) {
            statusMessage = "Database not initialized"
            return
        }

        coroutineScope.launch {
            try {
                when (accountType) {
                    "Checking" -> {
                        val acc = CheckingAccount(accountNumber, currentBalance.toDouble())
                        db.checkingAccountDao().insertCheckingAccount(acc)

                        val allCheckingAccount = db.checkingAccountDao().getAllCheckingAccount()
                        allCheckingAccount.forEach {
                            Log.d("InMemoryDB", "CheckingAccount: $it")
                        }
                    }

                    "CD" -> {
                        val acc = CDAccount(
                            accountNumber = accountNumber,
                            currentBalance = currentBalance.toDouble(),
                            initialBalance = initialBalance.toDouble(),
                            interestRate = interestRate.toDouble()
                        )
                        db.cdAccountDao().insertCDAccount(acc)

                        val allCDAccount = db.cdAccountDao().getAllCDAccount()
                        allCDAccount.forEach {
                            Log.d("InMemoryDB", "CDAccount: $it")
                        }
                    }

                    "Loan" -> {
                        val acc = LoanAccount(
                            accountNumber = accountNumber,
                            currentBalance = currentBalance.toDouble(),
                            initialBalance = initialBalance.toDouble(),
                            interestRate = interestRate.toDouble(),
                            paymentAmount = paymentAmount.toDouble()
                        )
                        db.loanAccountDao().insertLoanAccount(acc)

                        val allLoanAccount = db.loanAccountDao().getAllLoanAccount()
                        allLoanAccount.forEach {
                            Log.d("InMemoryDB", "LoanAccount: $it")
                        }
                    }
                }

                statusMessage = "$accountType account saved"
            } catch (e: Exception) {
                statusMessage = "Error saving account: ${e.message}"
            }

        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        /*centered horizontally in the screen with the radio selection, textFields, and the button below it
        in a single row, and the TextView to display a saved message*/
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    top = 44.dp,
                    bottom = 14.dp
                ),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select Account Type:")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                listOf("CD", "Loan", "Checking").forEach { type ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = accountType == type, onClick = { accountType = type })
                        Text(type)
                    }
                }
            }

            OutlinedTextField(
                value = accountNumber,
                onValueChange = { accountNumber = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text("Account Number")
                }
            )
            if (accountType != "Checking") {
                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text("Initial Balance")
                    }
                )
            }

            OutlinedTextField(
                value = currentBalance,
                onValueChange = { currentBalance = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text("Current Balance")
                }
            )

            if (accountType != "Checking") {
                OutlinedTextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text("Interest Rate")
                    }
                )
            }

            if (accountType == "Loan") {
                OutlinedTextField(
                    value = paymentAmount,
                    onValueChange = { paymentAmount = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                        Text("Payment Amount")
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Button(
                    onClick = { saveAccount() },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 103, green = 80, blue = 164),
                        contentColor = Color.White,

                        ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "Save",
                        fontWeight = FontWeight.Bold
                    )
                }


                Button(
                    onClick = { resetForm() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(red = 103, green = 80, blue = 164),
                        contentColor = Color.White,

                        ),
                    shape = RectangleShape
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(35.dp))

            Text(
                text = statusMessage,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }
    }
}

/*preview pane*/
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyFinanceScreenPreview() {
    MaterialTheme {
        MyFinanceScreen()
    }
}