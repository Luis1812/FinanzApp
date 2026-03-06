package com.luisdev.finanzapp.di

import android.app.Application
import androidx.room.Room
import com.luisdev.finanzapp.data.local.TransactionDatabase
import com.luisdev.finanzapp.data.local.dao.TransactionDao
import com.luisdev.finanzapp.data.repository.TransactionRepositoryImpl
import com.luisdev.finanzapp.domain.repository.TransactionRepository
import com.luisdev.finanzapp.domain.usecase.AddTransactionUseCase
import com.luisdev.finanzapp.domain.usecase.GetTransactionsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTransactionDatabase(app: Application): TransactionDatabase {
        return Room.databaseBuilder(
            app,
            TransactionDatabase::class.java,
            TransactionDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(db: TransactionDatabase): TransactionDao {
        return db.transactionDao
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(dao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideGetTransactionsUseCase(repository: TransactionRepository): GetTransactionsUseCase {
        return GetTransactionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddTransactionUseCase(repository: TransactionRepository): AddTransactionUseCase {
        return AddTransactionUseCase(repository)
    }
}
