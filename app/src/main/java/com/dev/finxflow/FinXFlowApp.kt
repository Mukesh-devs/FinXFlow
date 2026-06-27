package com.dev.finxflow

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
// COMPACT TOP-NOTCHED BOTTOM BAR SHAPE
// ==========================================
class TopNotchedBottomBarShape(
    private val notchPosition: Float,
    private val notchWidthRadius: Dp,
    private val notchDepth: Dp,
    private val barHeight: Dp,
    private val cornerRadius: Dp = 12.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val width = size.width
        val height = size.height
        val notchCenterX = width * notchPosition.coerceIn(0.05f, 0.95f)
        val radius = with(density) { notchWidthRadius.toPx() }
        val depth = with(density) { notchDepth.toPx() }
        val corner = with(density) { cornerRadius.toPx() }
        val barTop = with(density) { (height - barHeight.toPx()).coerceAtLeast(corner) }

        val path = Path().apply {
            // Bottom-left corner
            moveTo(0f, height - corner)
            quadraticBezierTo(0f, height, corner, height)

            // Bottom edge
            lineTo(width - corner, height)

            // Bottom-right corner
            quadraticBezierTo(width, height, width, height - corner)

            // Right edge up
            lineTo(width, barTop + corner)
            quadraticBezierTo(width, barTop, width - corner, barTop)

            // Top edge to notch end
            lineTo(notchCenterX + radius, barTop)

            // Concave cutout — smooth cubic bezier
            cubicTo(
                notchCenterX + radius, barTop,
                notchCenterX + radius * 0.5f, barTop - depth,
                notchCenterX, barTop - depth
            )
            cubicTo(
                notchCenterX - radius * 0.5f, barTop - depth,
                notchCenterX - radius, barTop,
                notchCenterX - radius, barTop
            )

            // Top edge to top-left
            lineTo(corner, barTop)
            quadraticBezierTo(0f, barTop, 0f, barTop + corner)

            close()
        }
        return Outline.Generic(path)
    }
}

// ==========================================
// ROOT APP — COMPACT VERSION
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

    LaunchedEffect(currentRouteIndex) {
        if (currentRouteIndex >= 0) selectedItem = currentRouteIndex
    }

    // Notch position: 1/6, 3/6, 5/6 for SpaceAround
    val notchPosition = remember(bottomNavItems.size, selectedItem) {
        (2 * selectedItem + 1) / (2 * bottomNavItems.size).toFloat()
    }

    val animatedNotchPosition by animateFloatAsState(
        targetValue = notchPosition,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "notch_position"
    )

    val density = LocalDensity.current
    var containerWidth by remember { mutableIntStateOf(0) }

    val indicatorXOffset = with(density) {
        if (containerWidth > 0) {
            (containerWidth * (animatedNotchPosition - 0.5f)).toDp()
        } else 0.dp
    }

    // ==========================================
    // COMPACT DIMENSIONS
    // ==========================================
    val barHeight = 70.dp          // Was 80.dp
    val notchDepth = 0.dp         // Was 28.dp
    val notchWidthRadius = 56.dp   // Was 36.dp
    val indicatorSize = 56.dp      // Was 56.dp
    val shapeHeight = barHeight + notchDepth
    val containerHeight = barHeight + notchDepth + indicatorSize / 2

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(containerHeight)
                        .padding(horizontal = 12.dp)
                        .onSizeChanged { containerWidth = it.width },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // ==========================================
                    // 1. BOTTOM BAR (drawn first)
                    // ==========================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(shapeHeight)
                            .align(Alignment.BottomCenter)
                            .shadow(
                                elevation = 10.dp,
                                shape = TopNotchedBottomBarShape(
                                    notchPosition = animatedNotchPosition,
                                    notchWidthRadius = notchWidthRadius,
                                    notchDepth = notchDepth,
                                    barHeight = barHeight,
                                    cornerRadius = 12.dp
                                ),
                                clip = true,
                                spotColor = Color.Black.copy(alpha = 0.06f)
                            )
                            .background(
                                color = Color(0xFFF8FAFC),
                                shape = TopNotchedBottomBarShape(
                                    notchPosition = animatedNotchPosition,
                                    notchWidthRadius = notchWidthRadius,
                                    notchDepth = notchDepth,
                                    barHeight = barHeight,
                                    cornerRadius = 12.dp
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeight)
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 8.dp)
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            bottomNavItems.forEachIndexed { index, item ->
                                val isSelected = selectedItem == index

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                if (selectedItem != index) {
                                                    selectedItem = index
                                                    navController.navigate(item.route) {
                                                        popUpTo(HomeRoute) { inclusive = false }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                }
                                            }
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    if (isSelected) {
                                        Box(modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = IndigoGradientEnd
                                        )
                                    } else {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.label,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 2. FLOATING INDICATOR (drawn on top)
                    // ==========================================
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(x = indicatorXOffset, y = 0.dp)
                            .size(indicatorSize)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(IndigoGradientStart, IndigoGradientEnd)
                                ),
                                shape = CircleShape
                            )
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                spotColor = IndigoGradientEnd.copy(alpha = 0.35f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        val selectedIcon = bottomNavItems[selectedItem].icon
                        Icon(
                            imageVector = selectedIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
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