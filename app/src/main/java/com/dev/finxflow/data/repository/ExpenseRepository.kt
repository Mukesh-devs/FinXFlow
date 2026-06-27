package com.dev.finxflow.data.repository

import com.dev.finxflow.data.database.ExpenseDao
import com.dev.finxflow.data.model.Expense
import com.dev.finxflow.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    /** Inclusive date filter: same date in From & To returns that single day */
    fun getExpensesByDateRange(fromDate: Long, toDate: Long): Flow<List<Expense>> {
        val endExclusive = toDate + 24 * 60 * 60 * 1000
        return expenseDao.getExpensesByDateRange(fromDate, endExclusive)
    }

    fun getTotalExpense(): Flow<Double> =
        expenseDao.getTotalExpense().map { it ?: 0.0 }

    /** Current month: 1st of this month → end of today */
    fun getMonthlyExpense(): Flow<Double> {
        val start = DateUtils.getStartOfCurrentMonth()
        val end = DateUtils.getStartOfToday() + 24 * 60 * 60 * 1000
        return expenseDao.getExpenseSumInRange(start, end).map { it ?: 0.0 }
    }

    /** Today only */
    fun getDailyExpense(): Flow<Double> {
        val start = DateUtils.getStartOfToday()
        val end = start + 24 * 60 * 60 * 1000
        return expenseDao.getExpenseSumInRange(start, end).map { it ?: 0.0 }
    }

    suspend fun addExpense(
        amount: Double,
        category: String,
        date: Long,
        name: String
    ) {
        val expense = Expense(
            amount = amount,
            category = category,
            date = date,
            time = DateUtils.getCurrentTime(),
            name = name.ifBlank { category }
        )
        expenseDao.insertExpense(expense)
    }
    /** Current month total for a specific category */
    fun getCategoryExpense(category: String): Flow<Double> {
        val start = DateUtils.getStartOfCurrentMonth()
        val end = DateUtils.getStartOfToday() + 24 * 60 * 60 * 1000
        return expenseDao.getExpenseSumByCategoryInRange(category, start, end).map { it ?: 0.0 }
    }

    fun getFoodExpense(): Flow<Double> = getCategoryExpense("Food")
    fun getTransportExpense(): Flow<Double> = getCategoryExpense("Transport")
    fun getShoppingExpense(): Flow<Double> = getCategoryExpense("Shopping")
    fun getOtherExpense(): Flow<Double> = getCategoryExpense("Other")

    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)
}