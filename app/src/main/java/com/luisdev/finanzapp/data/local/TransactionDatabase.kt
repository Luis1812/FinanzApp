package com.luisdev.finanzapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.luisdev.finanzapp.data.local.dao.TransactionDao
import com.luisdev.finanzapp.data.local.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
abstract class TransactionDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao

    companion object {
        const val DATABASE_NAME = "finanzapp_db"
    }
}
