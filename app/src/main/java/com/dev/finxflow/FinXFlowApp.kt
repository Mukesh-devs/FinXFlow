package com.dev.finxflow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
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
import com.dev.finxflow.ui.theme.NavBarBackground
import com.dev.finxflow.ui.theme.NavBarHighlight
import com.dev.finxflow.ui.theme.NavIndicatorEnd
import com.dev.finxflow.ui.theme.NavIndicatorStart
import com.dev.finxflow.ui.theme.NavSelectedIcon
import com.dev.finxflow.ui.theme.NavSelectedLabel
import com.dev.finxflow.ui.theme.NavUnselectedContent
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
// CONVEX TOP-NOTCHED BOTTOM BAR SHAPE
// (unchanged geometry — this was already correct,
// it just needed a non-zero notchDepth to be visible)
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
// ROOT APP — PREMIUM BLACK & WHITE VERSION
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

    // Notch position: 1/6, 3/6, 5/6 — center of each equal-width slot
    val notchPosition = remember(bottomNavItems.size, selectedItem) {
        (2 * selectedItem + 1) / (2 * bottomNavItems.size).toFloat()
    }

    // Spring instead of tween — gives the indicator a soft, premium "settle" bounce
    val animatedNotchPosition by animateFloatAsState(
        targetValue = notchPosition,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
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
    // DIMENSIONS — tuned so the notch actually
    // nests the indicator instead of being flat (0.dp)
    // ==========================================
    val barHeight = 70.dp
    val notchDepth = 30.dp          // was 0.dp — this is what made the bar look flat
    val notchWidthRadius = 38.dp    // sized to hug the 56.dp indicator, not dwarf it
    val indicatorSize = 56.dp
    val cornerRadius = 26.dp        // rounder, pill-like bar = more premium feel
    val shapeHeight = barHeight + notchDepth
    val containerHeight = 90.dp //barHeight + notchDepth + indicatorSize / 2

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
//                        .padding(horizontal = 12.dp)
                        .onSizeChanged { containerWidth = it.width },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val barShape = TopNotchedBottomBarShape(
                        notchPosition = animatedNotchPosition,
                        notchWidthRadius = notchWidthRadius,
                        notchDepth = notchDepth,
                        barHeight = barHeight,
                        cornerRadius = cornerRadius
                    )

                    // ==========================================
                    // 1. BOTTOM BAR (drawn first) — black, convex
                    // ==========================================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(shapeHeight)
                            .align(Alignment.BottomCenter)
                            .shadow(
                                elevation = 14.dp,
                                shape = barShape,
                                clip = true,
                                spotColor = Color.Black.copy(alpha = 0.18f)
                            )
                            .background(color = NavBarBackground, shape = barShape)
                    ) {
                        // Subtle top sheen — fakes a convex, light-catching surface
                        // instead of a flat black slab.
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(shapeHeight)
                                .clip(barShape)
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(NavBarHighlight, Color.Transparent)
                                    )
                                )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(barHeight)
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            bottomNavItems.forEachIndexed { index, item ->
                                val isSelected = selectedItem == index

                                // Each slot gets an EQUAL 1/3 share of the bar width,
                                // matching exactly how notchPosition is calculated above.
                                // (Previously an extra 8.dp Row padding made the tabs sit
                                // narrower than the width used for the notch math — that
                                // mismatch was the misalignment.)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
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
                                            .padding(vertical = 2.dp)
                                    ) {
                                        if (isSelected) {
                                            // Icon lives in the floating indicator instead
                                            Box(modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NavSelectedLabel
                                            )
                                        } else {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.label,
                                                tint = NavUnselectedContent,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.label,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = NavUnselectedContent
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ==========================================
                    // 2. FLOATING INDICATOR (drawn on top) — white, nested in the notch
                    // ==========================================
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(x = indicatorXOffset, y = 0.dp)
                            .size(indicatorSize)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                                spotColor = Color.Black.copy(alpha = 0.25f)
                            )
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(NavIndicatorStart, NavIndicatorEnd)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = bottomNavItems[selectedItem].icon,
                            transitionSpec = {
                                (scaleIn(initialScale = 0.6f, animationSpec = tween(220)) +
                                        fadeIn(animationSpec = tween(220))) togetherWith
                                        (scaleOut(targetScale = 0.6f, animationSpec = tween(150)) +
                                                fadeOut(animationSpec = tween(150)))
                            },
                            label = "nav_icon_swap"
                        ) { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = NavSelectedIcon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
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