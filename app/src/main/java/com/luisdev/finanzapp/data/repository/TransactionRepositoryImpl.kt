package com.luisdev.finanzapp.data.repository

import com.luisdev.finanzapp.data.local.dao.TransactionDao
import com.luisdev.finanzapp.data.local.entity.toDomain
import com.luisdev.finanzapp.data.local.entity.toEntity
import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getTransactions(): Flow<List<Transaction>> {
        return dao.getTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTransactionById(id: Int): Transaction? {
        // We need to add this to the DAO too
        return dao.getTransactionById(id)?.toDomain()
    }

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }
}
