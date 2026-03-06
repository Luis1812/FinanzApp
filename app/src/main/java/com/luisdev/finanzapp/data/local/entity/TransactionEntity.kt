package com.luisdev.finanzapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.luisdev.finanzapp.domain.model.Transaction

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val description: String,
    val amount: Double,
    val date: Long,
    val categoryId: String
)

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        description = description,
        amount = amount,
        date = date,
        categoryId = categoryId
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        description = description,
        amount = amount,
        date = date,
        categoryId = categoryId
    )
}
