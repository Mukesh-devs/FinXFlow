package com.dev.finxflow.ui.add

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.OtherHouses
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.finxflow.ui.theme.DangerRed
import com.dev.finxflow.ui.theme.DividerColor
import com.dev.finxflow.ui.theme.GradientEnd
import com.dev.finxflow.ui.theme.GradientStart
import com.dev.finxflow.ui.theme.PrimaryMain
import com.dev.finxflow.ui.theme.TextPrimary
import com.dev.finxflow.ui.theme.TextSecondary
import com.dev.finxflow.ui.theme.TextTertiary
import com.dev.finxflow.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ==========================================
// DATA MODELS
// ==========================================
data class CategoryOption(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val lightColor: Color
)

// ==========================================
// SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onExpensesClick: () -> Unit = {}
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Food") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val categories = remember {
        listOf(
            CategoryOption("Food", Icons.Outlined.Restaurant, Color(0xFFFF8A65), Color(0xFFFFF3E0)),
            CategoryOption("Transport", Icons.Outlined.DirectionsCar, Color(0xFF4DB6AC), Color(0xFFE0F2F1)),
            CategoryOption("Shopping", Icons.Outlined.LocalGroceryStore, Color(0xFF7986CB), Color(0xFFE8EAF6)),
            CategoryOption("Other", Icons.Outlined.OtherHouses, Color(0xFF8D6E63), Color(0xFFF5F5F5))
        )
    }

    val selectedCategoryOption = categories.find { it.name == selectedCategory } ?: categories.first()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    shadowElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GradientStart, GradientEnd)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New Expense",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )

                            IconButton(
                                onClick = { },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = amount,
                    transitionSpec = {
                        fadeIn() + slideInVertically { it / 2 } togetherWith
                                fadeOut() + slideOutVertically { it / 2 }
                    },
                    label = "amount_anim"
                ) { targetAmount ->
                    Text(
                        text = if (targetAmount.isEmpty()) "₹ 0.00" else "₹ $targetAmount",
                        color = GradientEnd,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                tonalElevation = 1.dp,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount", fontSize = 12.sp) },
                        placeholder = { Text("0.00", color = TextTertiary, fontSize = 14.sp) },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = GradientEnd,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GradientEnd,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = Color(0xFFF8FAFF),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category.name
                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) category.lightColor else Color(0xFFF8FAFC),
                                animationSpec = tween(250),
                                label = "cat_bg"
                            )
                            val borderColor by animateColorAsState(
                                targetValue = if (isSelected) category.color else DividerColor,
                                animationSpec = tween(250),
                                label = "cat_border"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { selectedCategory = category.name }
                                    .padding(vertical = 8.dp, horizontal = 2.dp)
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = category.name,
                                    tint = if (isSelected) category.color else TextTertiary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = category.name,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) TextPrimary else TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Date",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, DividerColor, RoundedCornerShape(14.dp))
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = GradientEnd,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = dateFormatter.format(Date(selectedDate)),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "Change",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GradientEnd
                            )
                        }
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
                                    Text("OK", color = GradientEnd, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel", color = TextSecondary)
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 100) description = it },
                        label = { Text("Description", fontSize = 12.sp) },
                        placeholder = { Text("What's this for?", color = TextTertiary, fontSize = 14.sp) },
                        supportingText = {
                            Text(
                                text = "${description.length}/100",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End,
                                color = if (description.length >= 100) DangerRed else TextTertiary,
                                fontSize = 10.sp
                            )
                        },
                        minLines = 2,
                        maxLines = 2,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GradientEnd,
                            unfocusedBorderColor = DividerColor,
                            focusedContainerColor = Color(0xFFF8FAFF),
                            unfocusedContainerColor = Color(0xFFF8FAFC)
                        ),
                        textStyle = TextStyle(
                            fontSize = 14.sp,
                            color = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            CompactSaveButton(
                text = "Save Expense",
                onClick = {
                    viewModel.saveExpense(
                        amount = amount,
                        category = selectedCategory,
                        date = selectedDate,
                        description = description
                    )
                    onSaveClick()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ==========================================
// COMPACT SAVE BUTTON
// ==========================================
@Composable
fun CompactSaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(150),
        label = "btn_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(48.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = GradientEnd.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}