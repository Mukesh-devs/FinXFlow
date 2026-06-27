package com.dev.finxflow.ui.add

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.finxflow.ui.theme.IndigoBlue
import com.dev.finxflow.ui.theme.IndigoGradientEnd
import com.dev.finxflow.ui.theme.IndigoGradientStart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CategoryOption(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {}
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Food & Dining") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var selectedTab by rememberSaveable { mutableIntStateOf(1) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val categories = remember {
        listOf(
            CategoryOption("Food & Dining", Icons.Outlined.Restaurant, Color(0xFFFF8A65)),
            CategoryOption("Transport", Icons.Outlined.DirectionsCar, Color(0xFF4DB6AC)),
            CategoryOption("Shopping", Icons.Outlined.ShoppingBag, Color(0xFF7986CB)),
            CategoryOption("Groceries", Icons.Outlined.LocalGroceryStore, Color(0xFF8D6E63))
        )
    }

    val selectedCategoryOption = categories.find { it.name == selectedCategory } ?: categories.first()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add Expense",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(IndigoGradientStart, IndigoGradientEnd)
                    )
                )
            )
        },
        bottomBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    GradientFab(onClick = { /* Already on Add */ })
                }
//                NavigationBar(
//                    containerColor = Color.White,
//                    tonalElevation = 0.dp,
//                    modifier = Modifier.navigationBarsPadding()
//                ) {
//                    NavigationBarItem(
//                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(24.dp)) },
//                        label = { Text("Home", fontSize = 12.sp) },
//                        selected = selectedTab == 0,
//                        onClick = {
//                            selectedTab = 0
//                            onHomeClick()
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = IndigoGradientEnd,
//                            selectedTextColor = IndigoGradientEnd,
//                            unselectedIconColor = Color(0xFF64748B),
//                            unselectedTextColor = Color(0xFF64748B),
//                            indicatorColor = Color(0xFFEEF2FF)
//                        )
//                    )
//                    NavigationBarItem(
//                        icon = { Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp)) },
//                        label = { Text("Add", fontSize = 12.sp) },
//                        selected = selectedTab == 1,
//                        onClick = { selectedTab = 1 },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = IndigoGradientEnd,
//                            selectedTextColor = IndigoGradientEnd,
//                            unselectedIconColor = Color(0xFF64748B),
//                            unselectedTextColor = Color(0xFF64748B),
//                            indicatorColor = Color(0xFFEEF2FF)
//                        )
//                    )
//                    NavigationBarItem(
//                        icon = { Icon(Icons.Default.List, contentDescription = "Expenses", modifier = Modifier.size(24.dp)) },
//                        label = { Text("Expenses", fontSize = 12.sp) },
//                        selected = selectedTab == 2,
//                        onClick = {
//                            selectedTab = 2
//                            onExpensesClick()
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = IndigoGradientEnd,
//                            selectedTextColor = IndigoGradientEnd,
//                            unselectedIconColor = Color(0xFF64748B),
//                            unselectedTextColor = Color(0xFF64748B),
//                            indicatorColor = Color(0xFFEEF2FF)
//                        )
//                    )
//                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Centered Wallet Icon
            Surface(
                shape = CircleShape,
                color = IndigoBlue.copy(alpha = 0.1f),
                modifier = Modifier.size(80.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = IndigoBlue,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Amount
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount", fontWeight = FontWeight.Medium) },
                placeholder = { Text("850.00") },
                leadingIcon = {
                    Text(
                        text = "₹",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoBlue,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedLabelColor = IndigoBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(
                            imageVector = selectedCategoryOption.icon,
                            contentDescription = null,
                            tint = selectedCategoryOption.color,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoBlue,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedLabelColor = IndigoBlue
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        text = category.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            onClick = {
                                selectedCategory = category.name
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = dateFormatter.format(Date(selectedDate)),
                    onValueChange = {},
                    enabled = false,
                    readOnly = true,
                    label = { Text("Date", fontWeight = FontWeight.Medium) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = IndigoBlue,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Select date",
                            tint = Color(0xFF64748B)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color(0xFFE2E8F0),
                        disabledLabelColor = IndigoBlue,
                        disabledLeadingIconColor = IndigoBlue
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { selectedDate = it }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK", color = IndigoBlue, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel", color = Color(0xFF64748B))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 100) description = it },
                label = { Text("Description", fontWeight = FontWeight.Medium) },
                placeholder = { Text("Lunch with team") },
                supportingText = {
                    Text(
                        text = "${description.length}/100",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        color = if (description.length >= 100) Color(0xFFEF4444) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IndigoBlue,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedLabelColor = IndigoBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            GradientButton(
                text = "Save Expense",
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(150),
        label = "btn_scale"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .scale(scale)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(IndigoGradientStart, IndigoGradientEnd)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun GradientFab(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(150),
        label = "fab_scale"
    )

    Box(
        modifier = Modifier
            .size(64.dp)
            .scale(scale)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = IndigoGradientEnd.copy(alpha = 0.3f)
            )
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(IndigoGradientStart, IndigoGradientEnd)
                ),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}