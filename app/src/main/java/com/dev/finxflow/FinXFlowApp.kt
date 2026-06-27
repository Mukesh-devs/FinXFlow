package com.dev.finxflow

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.finxflow.ui.add.AddExpenseScreen
import com.dev.finxflow.ui.expenses.ExpensesScreen
import com.dev.finxflow.ui.home.HomeScreen
import com.dev.finxflow.ui.theme.IndigoGradientEnd
import com.dev.finxflow.ui.theme.IndigoGradientStart
import kotlinx.serialization.Serializable

// ==========================================
// NAVIGATION ROUTES
// ==========================================
@Serializable
object HomeRoute

@Serializable
object AddExpenseRoute

@Serializable
object ExpensesRoute

// ==========================================
// BOTTOM NAV MODEL
// ==========================================
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

// ==========================================
// ROOT APP
// ==========================================
@Composable
fun FinXFlowApp(
    navController: NavHostController = rememberNavController()
) {
    val bottomNavItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, HomeRoute),
        BottomNavItem("Add", Icons.Default.Add, AddExpenseRoute),
        BottomNavItem("Expenses", Icons.Default.List, ExpensesRoute)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentRouteIndex = bottomNavItems.indexOfFirst { item ->
        currentDestination?.hasRoute(item.route::class) == true
    }.coerceAtLeast(0)

    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    // Keep tab indicator in sync when user uses back gesture/button
    LaunchedEffect(currentRouteIndex) {
        if (currentRouteIndex >= 0) selectedItem = currentRouteIndex
    }

    // Show FAB only on Home
    val showFab = currentDestination?.hasRoute(HomeRoute::class) == true

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp,
                modifier = Modifier.height(80.dp)
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    val selected = selectedItem == index

                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(HomeRoute) { inclusive = false }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoGradientEnd,
                            selectedTextColor = IndigoGradientEnd,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = Color(0xFFEEF2FF)
                        ),
                        alwaysShowLabel = true
                    )
                }
            }
        },
//        floatingActionButton = {
//            if (showFab) {
//                GradientFloatingActionButton(
//                    onClick = {
//                        navController.navigate(AddExpenseRoute) {
//                            popUpTo(HomeRoute) { inclusive = false }
//                            launchSingleTop = true
//                        }
//                    }
//                )
//            }
//        },
//        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300)
                        )
            }
        ) {
            composable<HomeRoute> {
                HomeScreen()
            }
            composable<AddExpenseRoute> {
                AddExpenseScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { navController.popBackStack() },
                    onHomeClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onExpensesClick = {
                        navController.navigate(ExpensesRoute) {
                            popUpTo(HomeRoute) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable<ExpensesRoute> {
                ExpensesScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// ==========================================
// GRADIENT FAB
// ==========================================
@Composable
fun GradientFloatingActionButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(150),
        label = "fab_scale"
    )

    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        ),
        modifier = Modifier
            .size(72.dp)
            .scale(scale),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(IndigoGradientStart, IndigoGradientEnd)
                    ),
                    shape = CircleShape
                )
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color(0xFF4F46E5).copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Add Expense",
//                tint = Color.White,
//                modifier = Modifier.size(32.dp)
//            )
        }
    }
}