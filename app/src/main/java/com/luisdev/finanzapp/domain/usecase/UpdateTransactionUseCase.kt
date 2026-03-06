package com.luisdev.finanzapp.domain.usecase

import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }
}
