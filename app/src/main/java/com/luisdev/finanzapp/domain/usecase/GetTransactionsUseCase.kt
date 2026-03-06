package com.luisdev.finanzapp.domain.usecase

import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> {
        return repository.getTransactions()
    }
}
