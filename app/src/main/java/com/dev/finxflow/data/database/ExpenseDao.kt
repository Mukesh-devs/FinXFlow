package com.dev.finxflow.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.dev.finxflow.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC, time DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    // inclusive-exclusive range:  [startDate , endDate)
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date < :endDate ORDER BY date DESC, time DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date < :endDate")
    fun getExpenseSumInRange(startDate: Long, endDate: Long): Flow<Double?>

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)
}