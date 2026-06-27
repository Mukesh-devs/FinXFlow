package com.dev.finxflow.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dev.finxflow.data.database.AppDatabase
import com.dev.finxflow.data.model.Expense
import com.dev.finxflow.data.repository.ExpenseRepository
import com.dev.finxflow.ui.home.RecentExpenseUi
import com.dev.finxflow.utils.CsvExporter
import com.dev.finxflow.utils.CurrencyUtils
import com.dev.finxflow.utils.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalExpense: String = "₹ 0.00",
    val monthlyExpense: String = "₹ 0.00",
    val currentMonth: String = "",
    val dailyExpense: String = "₹ 0.00",
    val foodExpense: String = "₹ 0.00",      // was "$0.00"
    val transportExpense: String = "₹ 0.00", // was "$0.00"
    val shoppingExpense: String = "₹ 0.00",  // was "$0.00"
    val otherExpense: String = "₹ 0.00"      // was "$0.00"
)

data class ExpensesUiState(
    val filteredExpenses: List<Expense> = emptyList(),
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val exportMessage: String? = null
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _dateRange = MutableStateFlow<Pair<Long?, Long?>>(null to null)

    private val _expensesUiState = MutableStateFlow(ExpensesUiState())
    val expensesUiState: StateFlow<ExpensesUiState> = _expensesUiState.asStateFlow()



    init {
        val db = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(db.expenseDao())

        // ── Home screen aggregates ──
        // ── Home screen aggregates ──
        viewModelScope.launch {
            combine(
                repository.getTotalExpense(),
                repository.getMonthlyExpense(),
                repository.getDailyExpense(),
                repository.getFoodExpense(),
                repository.getTransportExpense(),
                repository.getShoppingExpense(),
                repository.getOtherExpense()
            ) { values: Array<Double> ->
                HomeUiState(
                    totalExpense = CurrencyUtils.formatAmount(values[0]),
                    monthlyExpense = CurrencyUtils.formatAmount(values[1]),
                    currentMonth = DateUtils.getCurrentMonthDisplay(),
                    dailyExpense = CurrencyUtils.formatAmount(values[2]),
                    foodExpense = CurrencyUtils.formatAmount(values[3]),
                    transportExpense = CurrencyUtils.formatAmount(values[4]),
                    shoppingExpense = CurrencyUtils.formatAmount(values[5]),
                    otherExpense = CurrencyUtils.formatAmount(values[6])
                )
            }.collect { _homeUiState.value = it }
        }

        // ── Expenses list (reacts to date range changes) ──
        viewModelScope.launch {
            _dateRange.flatMapLatest { (from, to) ->
                if (from != null && to != null) {
                    repository.getExpensesByDateRange(from, to)
                } else {
                    repository.allExpenses
                }
            }.collect { list ->
                _expensesUiState.update { it.copy(filteredExpenses = list) }
            }
        }
    }

    /** Call this when the user picks From / To dates in ExpensesScreen */
    fun setDateRange(from: Long?, to: Long?) {
        _dateRange.value = from to to
        _expensesUiState.update { it.copy(fromDate = from, toDate = to) }
    }

    /** Call this from AddExpenseScreen on Save */
    fun saveExpense(
        amount: String,
        category: String,
        date: Long,
        description: String
    ) {
        val amt = amount.toDoubleOrNull() ?: 0.0
        if (amt <= 0) return
        viewModelScope.launch {
            repository.addExpense(
                amount = amt,
                category = category,
                date = date,
                name = description
            )
        }
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            val expenses = _expensesUiState.value.filteredExpenses
            if (expenses.isEmpty()) {
                _expensesUiState.update { it.copy(exportMessage = "No expenses to export") }
                return@launch
            }
            val msg = CsvExporter.export(context, expenses)
            _expensesUiState.update { it.copy(exportMessage = msg) }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun clearExportMessage() {
        _expensesUiState.update { it.copy(exportMessage = null) }
    }

    // ── Mappers ──
    private fun Expense.toRecentUi(): RecentExpenseUi {
        return RecentExpenseUi(
            name = this.name,
            category = this.category,
            amount = CurrencyUtils.formatAmount(this.amount),
            icon = categoryToIcon(this.category),
            iconColor = categoryToColor(this.category)
        )
    }

    private fun categoryToIcon(category: String): ImageVector = when (category) {
        "Food", "Food & Dining" -> Icons.Outlined.Restaurant
        "Transport" -> Icons.Outlined.DirectionsCar
        "Shopping" -> Icons.Outlined.ShoppingBag
        "Grocery", "Groceries" -> Icons.Outlined.LocalGroceryStore
        "Entertainment" -> Icons.Outlined.Movie
        else -> Icons.Outlined.AccountBalanceWallet
    }

    private fun categoryToColor(category: String): Color = when (category) {
        "Food", "Food & Dining" -> Color(0xFFFF8A65)
        "Transport" -> Color(0xFF4DB6AC)
        "Shopping" -> Color(0xFF7986CB)
        "Grocery", "Groceries" -> Color(0xFF8D6E63)
        "Entertainment" -> Color(0xFFAB47BC)
        else -> Color(0xFF64748B)
    }
}