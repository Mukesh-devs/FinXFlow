package com.dev.finxflow.viewmodel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalTaxi
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.finxflow.data.model.Expense
import com.dev.finxflow.ui.home.RecentExpenseUi
import com.dev.finxflow.ui.home.TopCategoryUi
import com.dev.finxflow.utils.CsvExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class HomeUiState(
    val totalExpense: String = "₹24,850.00",
    val monthlyExpense: String = "₹24,850.00",
    val currentMonth: String = "June 2026",
    val dailyExpense: String = "₹1,250.00",
    val recentExpenses: List<RecentExpenseUi> = listOf(
        RecentExpenseUi("Lunch with Team", "Food & Dining", "₹850", Icons.Outlined.Restaurant, Color(0xFFFF8A65)),
        RecentExpenseUi("Auto Fare", "Transport", "₹120", Icons.Outlined.LocalTaxi, Color(0xFF4DB6AC)),
        RecentExpenseUi("Grocery", "Shopping", "₹1,250", Icons.Outlined.LocalGroceryStore, Color(0xFF7986CB)),
        RecentExpenseUi("Coffee", "Food & Dining", "₹120", Icons.Outlined.Fastfood, Color(0xFF8D6E63))
    ),
    val topCategories: List<TopCategoryUi> = listOf(
        TopCategoryUi("Food & Dining", "₹8,650", 0.34f, Color(0xFFFF8A65)),
        TopCategoryUi("Transport", "₹4,200", 0.18f, Color(0xFF4DB6AC)),
        TopCategoryUi("Shopping", "₹6,800", 0.27f, Color(0xFF7986CB))
    )
)

data class ExpensesUiState(
    val allExpenses: List<Expense> = emptyList(),
    val filteredExpenses: List<Expense> = emptyList(),
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All", "Food & Dining", "Transport", "Shopping", "Groceries", "Entertainment"),
    val isLoading: Boolean = false,
    val exportMessage: String? = null
)

class ExpenseViewModel : ViewModel() {

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _expensesUiState = MutableStateFlow(ExpensesUiState())
    val expensesUiState: StateFlow<ExpensesUiState> = _expensesUiState.asStateFlow()

    init {
        loadDemoExpenses()
    }

    private fun loadDemoExpenses() {
        val cal = Calendar.getInstance()
        val demo = listOf(
            Expense(id = 1, name = "Lunch with Team", category = "Food & Dining", amount = 850.0, date = cal.apply { set(2026, 5, 25) }.timeInMillis, description = "Team lunch"),
            Expense(id = 2, name = "Auto Fare", category = "Transport", amount = 120.0, date = cal.apply { set(2026, 5, 26) }.timeInMillis, description = "Auto to station"),
            Expense(id = 3, name = "Grocery Shopping", category = "Groceries", amount = 1250.0, date = cal.apply { set(2026, 5, 26) }.timeInMillis, description = "Weekly groceries"),
            Expense(id = 4, name = "Coffee", category = "Food & Dining", amount = 120.0, date = cal.apply { set(2026, 5, 27) }.timeInMillis, description = "Morning coffee"),
            Expense(id = 5, name = "Movie Tickets", category = "Entertainment", amount = 600.0, date = cal.apply { set(2026, 5, 20) }.timeInMillis, description = "Weekend movie"),
            Expense(id = 6, name = "Uber Ride", category = "Transport", amount = 340.0, date = cal.apply { set(2026, 5, 22) }.timeInMillis, description = "Airport pickup"),
            Expense(id = 7, name = "Dinner Date", category = "Food & Dining", amount = 2100.0, date = cal.apply { set(2026, 5, 23) }.timeInMillis, description = "Anniversary dinner"),
            Expense(id = 8, name = "New Shoes", category = "Shopping", amount = 4500.0, date = cal.apply { set(2026, 5, 18) }.timeInMillis, description = "Nike running shoes")
        )

        _expensesUiState.update {
            it.copy(allExpenses = demo, filteredExpenses = demo)
        }
    }

    fun setDateRange(from: Long?, to: Long?) {
        _expensesUiState.update { it.copy(fromDate = from, toDate = to) }
        applyFilters()
    }

    fun setCategory(category: String) {
        _expensesUiState.update { it.copy(selectedCategory = category) }
        applyFilters()
    }

    private fun applyFilters() {
        val state = _expensesUiState.value
        var filtered = state.allExpenses

        if (state.selectedCategory != "All") {
            filtered = filtered.filter { it.category == state.selectedCategory }
        }

        state.fromDate?.let { from ->
            filtered = filtered.filter { it.date >= from }
        }

        state.toDate?.let { to ->
            filtered = filtered.filter { it.date <= to }
        }

        _expensesUiState.update { it.copy(filteredExpenses = filtered) }
    }

    fun exportToCsv(context: Context) {
        viewModelScope.launch {
            val path = CsvExporter.export(context, _expensesUiState.value.filteredExpenses)
            _expensesUiState.update {
                it.copy(
                    exportMessage = if (path != null) "CSV saved to Downloads" else "Export failed"
                )
            }
        }
    }

    fun clearExportMessage() {
        _expensesUiState.update { it.copy(exportMessage = null) }
    }
}