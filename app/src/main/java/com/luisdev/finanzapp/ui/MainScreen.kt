package com.luisdev.finanzapp.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.ui.components.TransactionRow
import com.luisdev.finanzapp.ui.theme.*
import com.luisdev.finanzapp.ui.viewmodel.TransactionViewModel
import com.luisdev.finanzapp.ui.viewmodel.UserViewModel
import java.util.Locale
import kotlin.math.absoluteValue

enum class Tabs {
    Home, Transactions, Categories
}

@Composable
fun MainScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    onNavigateToEditTransaction: (Int) -> Unit,
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    var currentScreen by rememberSaveable { mutableStateOf(Tabs.Home) }
    val transactions by transactionViewModel.transactions.collectAsState()
    val userPrefs by userViewModel.userPreferences.collectAsState()

    Scaffold(
        topBar = {
            Header(
                title = {
                    when(currentScreen) {
                        Tabs.Home -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().clickable{
                                    onNavigateToProfileSetup()
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(DarkCardBackground)
                                ) {
                                    if (!userPrefs?.profilePicturePath.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = userPrefs?.profilePicturePath,
                                            contentDescription = "Foto de perfil",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            Icons.Rounded.Person,
                                            contentDescription = null,
                                            modifier = Modifier.align(Alignment.Center),
                                            tint = BrandGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "Bienvenido",
                                        color = TextGray,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = "${userPrefs?.firstName} ${userPrefs?.lastName}",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Tabs.Transactions -> {
                            Text("Movimientos", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Tabs.Categories -> {
                            Text("Categorías", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        },
        bottomBar = {
                BottomNavigationBar(
                    currentScreen = currentScreen,
                    onScreenSelected = { currentScreen = it }
                )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentScreen == Tabs.Transactions,
                enter = slideInVertically() + expandVertically(),
                exit = slideOutVertically() + shrinkVertically()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToAddTransaction,
                    containerColor = BrandGreen,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Añadir Transacción")
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    Tabs.Home -> {
                        DashboardContent(
                            transactions,
                            onSeeAllClick = { currentScreen = Tabs.Transactions }
                        )
                    }
                    Tabs.Transactions -> TransactionsScreen(
                        onEditTransaction = onNavigateToEditTransaction,
                        viewModel = transactionViewModel
                    )
                    Tabs.Categories -> CategoriesScreen(viewModel = transactionViewModel)
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    transactions: List<Transaction>,
    onSeeAllClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            TotalBalanceCard(transactions)
        }
        item { ExpensesSection(transactions) }
        item {
            RecentTransactionsSection(
                transactions,
                onSeeAllClick = onSeeAllClick
            )
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header(title: @Composable (() -> Unit)) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
        title = title
    )
}

@Composable
fun TotalBalanceCard(transactions: List<Transaction>) {
    val totalIncome = transactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpenses = transactions.filter { it.amount < 0 }.sumOf { it.amount }.absoluteValue
    val totalBalance = totalIncome - totalExpenses

    Card(
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(BrandGreenLight, BrandGreen)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BALANCE TOTAL",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Icon(
                    Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.7f)
                )
            }
            Text(
                text = "S/${String.format(Locale.getDefault(), "%.2f", totalBalance)}",
                color = Color.Black,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BalanceSubCard(
                    title = "INGRESOS",
                    amount = "+S/${String.format(Locale.getDefault(), "%.2f", totalIncome)}",
                    modifier = Modifier.weight(1f)
                )
                BalanceSubCard(
                    title = "GASTOS",
                    amount = "-S/${String.format(Locale.getDefault(), "%.2f", totalExpenses)}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun BalanceSubCard(title: String, amount: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = Color.Black.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = amount,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExpensesSection(transactions: List<Transaction>) {
    val expenses = transactions.filter { it.amount < 0 }
    val totalSpent = expenses.sumOf { it.amount }.absoluteValue
    val expensesByCategory = expenses.groupBy { it.categoryId }
        .mapValues { entry -> entry.value.sumOf { it.amount }.absoluteValue }
        .toList()
        .sortedByDescending { it.second }
        .take(5) // Show top 5 categories

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gastos",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardBackground),
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(130.dp), contentAlignment = Alignment.Center) {
                    DonutChart(expensesByCategory, totalSpent)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GASTADO", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "S/${String.format(Locale.getDefault(), "%.0f", totalSpent)}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(32.dp))
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    expensesByCategory.forEach { (categoryId, amount) ->
                        val category = com.luisdev.finanzapp.domain.model.Category.defaultCategories.find { it.id == categoryId }
                            ?: com.luisdev.finanzapp.domain.model.Category.defaultCategories.last()
                        ExpenseLegendItem(
                            label = category.name,
                            amount = "S/${String.format(Locale.getDefault(), "%.0f", amount)}",
                            color = category.color
                        )
                    }
                    if (expensesByCategory.isEmpty()) {
                        Text("Sin gastos", color = TextGray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(expensesByCategory: List<Pair<String, Double>>, totalSpent: Double) {
    Canvas(modifier = Modifier.size(130.dp)) {
        val strokeWidth = 14.dp.toPx()
        drawArc(
            color = Color.Gray.copy(alpha = 0.1f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
        
        if (totalSpent > 0) {
            var currentStartAngle = -90f
            expensesByCategory.forEach { (categoryId, amount) ->
                val category = com.luisdev.finanzapp.domain.model.Category.defaultCategories.find { it.id == categoryId }
                    ?: com.luisdev.finanzapp.domain.model.Category.defaultCategories.last()
                val sweepAngle = ((amount / totalSpent) * 360f).toFloat()
                
                drawArc(
                    color = category.color,
                    startAngle = currentStartAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                currentStartAngle += sweepAngle
            }
        }
    }
}

@Composable
fun ExpenseLegendItem(label: String, amount: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            color = TextGray,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = amount,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RecentTransactionsSection(
    transactions: List<Transaction>,
    onSeeAllClick: () -> Unit
) {
    val recentTransactions = transactions.take(5)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Movimientos Recientes",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = onSeeAllClick,
                colors = ButtonDefaults.textButtonColors(contentColor = TextGray)
            ) {
                Text(
                    text = "Ver más",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            recentTransactions.forEach { transaction ->
                val category = com.luisdev.finanzapp.domain.model.Category.defaultCategories.find { it.id == transaction.categoryId }
                    ?: com.luisdev.finanzapp.domain.model.Category.defaultCategories[0]

                val formattedDate = remember(transaction.date) {
                    formatTransactionDate(transaction.date)
                }

                TransactionRow(
                    icon = category.icon,
                    iconColor = category.color,
                    title = transaction.description,
                    categoryName = category.name,
                    date = formattedDate,
                    amount = "${if (transaction.amount > 0) "+" else "-"}S/${transaction.amount.absoluteValue}",
                    amountColor = if (transaction.amount > 0) IncomeGreen else ExpenseRed
                )
            }
            if (recentTransactions.isEmpty()) {
                Text("No hay movimientos recientes", color = TextGray, fontSize = 14.sp)
            }
        }
    }
}



@Composable
fun BottomNavigationBar(currentScreen: Tabs, onScreenSelected: (Tabs) -> Unit) {
    NavigationBar(
        containerColor = DarkBackground,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            icon = { CustomIcon(Icons.Rounded.Home, size = 28.dp) },
            label = { Text("Inicio", fontSize = 12.sp) },
            selected = currentScreen == Tabs.Home,
            onClick = { onScreenSelected(Tabs.Home) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandGreen,
                selectedTextColor = BrandGreen,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { CustomIcon(Icons.Rounded.BarChart, size = 28.dp) },
            label = { Text("Movimientos", fontSize = 12.sp) },
            selected = currentScreen == Tabs.Transactions,
            onClick = { onScreenSelected(Tabs.Transactions) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandGreen,
                selectedTextColor = BrandGreen,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = { CustomIcon(Icons.Rounded.AccountBalanceWallet, size = 28.dp) },
            label = { Text("Categorías", fontSize = 12.sp) },
            selected = currentScreen == Tabs.Categories,
            onClick = { onScreenSelected(Tabs.Categories) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandGreen,
                selectedTextColor = BrandGreen,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun CustomIcon(icon: ImageVector, size: Dp) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(size)
    )
}
