package com.luisdev.finanzapp.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luisdev.finanzapp.domain.model.Category
import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.ui.components.TransactionRow
import com.luisdev.finanzapp.ui.theme.*
import com.luisdev.finanzapp.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(
    onEditTransaction: (Int) -> Unit = {},
    viewModel: TransactionViewModel = hiltViewModel(),
) {
    val transactions by viewModel.transactions.collectAsState()
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    var filterIsIncome by remember { mutableStateOf<Boolean?>(null) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }

    val filteredTransactions = remember(transactions, filterIsIncome, selectedCategoryId) {
        transactions.filter { transaction ->
            val matchesType = when (filterIsIncome) {
                true -> transaction.amount > 0
                false -> transaction.amount < 0
                null -> true
            }
            val matchesCategory = if (selectedCategoryId != null) {
                transaction.categoryId == selectedCategoryId
            } else true

            matchesType && matchesCategory
        }
    }

    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Eliminar Movimiento", color = Color.White) },
            text = { Text("¿Estás seguro de que deseas eliminar este movimiento? Esta acción no se puede deshacer.", color = TextGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        transactionToDelete?.let { viewModel.deleteTransaction(it) }
                        transactionToDelete = null
                    }
                ) {
                    Text("Eliminar", color = ExpenseRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("Cancelar", color = Color.White)
                }
            },
            containerColor = DarkCardBackground
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (transactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterIsIncome == true,
                    onClick = { filterIsIncome = if (filterIsIncome == true) null else true },
                    label = { Text("Ingresos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                        selectedLabelColor = IncomeGreen,
                        containerColor = DarkCardBackground,
                        labelColor = TextGray
                    )
                )
                FilterChip(
                    selected = filterIsIncome == false,
                    onClick = { filterIsIncome = if (filterIsIncome == false) null else false },
                    label = { Text("Gastos") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                        selectedLabelColor = ExpenseRed,
                        containerColor = DarkCardBackground,
                        labelColor = TextGray
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Category.defaultCategories.forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = {
                            selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                        },
                        label = { Text(category.name) },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color.copy(alpha = 0.2f),
                            selectedLabelColor = category.color,
                            selectedLeadingIconColor = category.color,
                            containerColor = DarkCardBackground,
                            labelColor = TextGray
                        )
                    )
                }
            }
        }

        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (transactions.isEmpty()) "No hay transacciones registradas"
                    else "No hay movimientos que coincidan con el filtro",
                    color = TextGray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(filteredTransactions, key = { it.id!! }) { transaction ->
                    val category = Category.defaultCategories.find { it.id == transaction.categoryId }
                        ?: Category.defaultCategories[0]

                    val formattedDate = remember(transaction.date) {
                        formatTransactionDate(transaction.date)
                    }

                    Box(
                        modifier = Modifier
                            .animateItem(
                                fadeInSpec = tween(durationMillis = 300),
                                fadeOutSpec = tween(durationMillis = 300),
                                placementSpec = spring(stiffness = Spring.StiffnessLow)
                            )
                            .combinedClickable(
                                onClick = { transaction.id?.let { onEditTransaction(it) } },
                                onLongClick = { transactionToDelete = transaction }
                            )
                    ) {
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
                }
            }
        }
    }
}

fun formatTransactionDate(timestamp: Long): String {
    val localeES = Locale("es", "ES")
    val date = Date(timestamp)
    val calendar = Calendar.getInstance().apply { time = date }
    val now = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(calendar, now) -> "Hoy"
        isSameDay(calendar, yesterday) -> "Ayer"
        else -> {
            val dayOfWeek = SimpleDateFormat("EEE", localeES).format(date).replaceFirstChar { it.uppercase() }
            val dayOfMonth = SimpleDateFormat("dd", localeES).format(date)
            val month = SimpleDateFormat("MMMM", localeES).format(date).replaceFirstChar { it.uppercase() }
            "$dayOfWeek $dayOfMonth de $month"
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Preview
@Composable
fun TransactionsScreenPreview() {
    FinanzAppTheme {
        TransactionsScreen()
    }
}
