package com.luisdev.finanzapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.domain.usecase.AddTransactionUseCase
import com.luisdev.finanzapp.domain.usecase.DeleteTransactionUseCase
import com.luisdev.finanzapp.domain.usecase.GetTransactionsUseCase
import com.luisdev.finanzapp.domain.usecase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            getTransactionsUseCase().collectLatest {
                _transactions.value = it
            }
        }
    }

    fun addTransaction(description: String, amount: Double, date: Long, categoryId: String = "1") {
        viewModelScope.launch {
            val newTransaction = Transaction(
                description = description,
                amount = amount,
                date = date,
                categoryId = categoryId
            )
            addTransactionUseCase(newTransaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            updateTransactionUseCase(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
        }
    }
}
