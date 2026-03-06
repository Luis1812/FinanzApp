package com.luisdev.finanzapp.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luisdev.finanzapp.domain.model.Category
import com.luisdev.finanzapp.domain.model.Transaction
import com.luisdev.finanzapp.ui.theme.*
import com.luisdev.finanzapp.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionId: Int?,
    onClose: () -> Unit = {},
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var isExpense by remember { mutableStateOf(true) }
    var amount by remember { mutableStateOf("0.00") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.defaultCategories[0]) }
    var showCategoryPicker by remember { mutableStateOf(false) }

    // Date Picker State
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )
    val formattedDate = remember(datePickerState.selectedDateMillis) {
        val dateMillis = datePickerState.selectedDateMillis
        if (dateMillis != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(Date(dateMillis))
        } else {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        }
    }

    val transactions by viewModel.transactions.collectAsState()

    LaunchedEffect(transactionId, transactions) {
        if (transactionId != null && transactions.isNotEmpty()) {
            val transaction = transactions.find { it.id == transactionId }
            transaction?.let {
                description = it.description
                amount = it.amount.absoluteValue.toString()
                isExpense = it.amount < 0
                selectedCategory = Category.defaultCategories.find { cat -> cat.id == it.categoryId }
                    ?: Category.defaultCategories[0]
                datePickerState.selectedDateMillis = it.date
            }
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = DarkBackground
                ),
                title = {
                    Text(
                        text = if (transactionId == null) "Añadir Movimiento" else "Editar Movimiento",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    val bias by animateFloatAsState(
                        targetValue = if (isExpense) 1f else -1f,
                        animationSpec = tween(durationMillis = 300),
                        label = "ToggleAnimation"
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardBackground)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight()
                                .align(BiasAlignment(bias, 0f))
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandGreen)
                        )

                        Row(modifier = Modifier.fillMaxSize()) {
                            ToggleItem(
                                text = "Ingreso",
                                isSelected = !isExpense,
                                modifier = Modifier.weight(1f),
                                onClick = { isExpense = false }
                            )
                            ToggleItem(
                                text = "Gasto",
                                isSelected = isExpense,
                                modifier = Modifier.weight(1f),
                                onClick = { isExpense = true }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }

                item {
                    Text(
                        text = "MONTO",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "S/",
                            color = BrandGreen,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        BasicTextFieldComponent(
                            value = amount,
                            onValueChange = { amount = it },
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            ),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }

                // Form Fields
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                        FormField(
                            label = "Categoría",
                            icon = selectedCategory.icon,
                            iconColor = selectedCategory.color,
                            value = selectedCategory.name,
                            trailingIcon = Icons.Rounded.KeyboardArrowDown,
                            onClick = { showCategoryPicker = true }
                        )

                        FormField(
                            label = "Fecha",
                            icon = Icons.Rounded.CalendarToday,
                            value = formattedDate,
                            trailingIcon = Icons.Rounded.CalendarMonth,
                            onClick = { showDatePicker = true }
                        )

                        NoteField(
                            label = "Nota",
                            icon = Icons.AutoMirrored.Rounded.Notes,
                            value = description,
                            onValueChange = { description = it }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }

                // Save Button
                item {
                    Button(
                        onClick = {
                            val amountDouble = amount.toDoubleOrNull() ?: 0.0
                            val finalAmount = if (isExpense) -amountDouble else amountDouble
                            val selectedDate = datePickerState.selectedDateMillis?.let { utcMillis ->
                                val calendarUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                    timeInMillis = utcMillis
                                }
                                Calendar.getInstance().apply {
                                    set(
                                        calendarUTC.get(Calendar.YEAR),
                                        calendarUTC.get(Calendar.MONTH),
                                        calendarUTC.get(Calendar.DAY_OF_MONTH)
                                    )
                                }.timeInMillis
                            } ?: System.currentTimeMillis()

                            if (description.isNotBlank() && amountDouble != 0.0) {
                                if (transactionId == null) {
                                    viewModel.addTransaction(description, finalAmount, selectedDate, selectedCategory.id)
                                } else {
                                    viewModel.updateTransaction(
                                        Transaction(
                                            id = transactionId,
                                            description = description,
                                            amount = finalAmount,
                                            date = selectedDate,
                                            categoryId = selectedCategory.id
                                        )
                                    )
                                }
                                onClose()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (transactionId == null) "Guardar Movimiento" else "Actualizar Movimiento",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Aceptar", color = BrandGreen)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancelar", color = TextGray)
                        }
                    },
                    colors = DatePickerDefaults.colors(
                        containerColor = DarkCardBackground
                    )
                ) {
                    DatePicker(
                        state = datePickerState,
                        colors = DatePickerDefaults.colors(
                            titleContentColor = Color.White,
                            headlineContentColor = Color.White,
                            weekdayContentColor = TextGray,
                            dayContentColor = Color.White,
                            selectedDayContainerColor = BrandGreen,
                            selectedDayContentColor = Color.Black,
                            todayContentColor = BrandGreen,
                            todayDateBorderColor = BrandGreen
                        )
                    )
                }
            }

            AnimatedVisibility(
                visible = showCategoryPicker,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CategoryPickerModal(
                    onCategorySelected = {
                        selectedCategory = it
                        showCategoryPicker = false
                    },
                    onDismiss = { showCategoryPicker = false }
                )
            }
        }
    }
}

@Composable
fun AnimatedVisibilityScope.CategoryPickerModal(onCategorySelected: (Category) -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .animateEnterExit(
                    enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)),
                    exit = slideOutVertically(targetOffsetY = { it }, animationSpec = tween(400))
                )
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardBackground)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    "Seleccionar Categoría",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(Category.defaultCategories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onCategorySelected(category) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(category.color.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    category.icon,
                                    contentDescription = null,
                                    tint = category.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(category.name, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToggleItem(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else TextGray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun FormField(
    label: String,
    icon: ImageVector,
    iconColor: Color = BrandGreen,
    value: String,
    trailingIcon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Text(text = label, color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardBackground)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = value, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
            if (trailingIcon != null) {
                Icon(trailingIcon, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun NoteField(label: String, icon: ImageVector, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(text = label, color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkCardBackground)
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(icon, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.fillMaxSize()) {
                if (value.isEmpty()) {
                    Text("Añade una descripción...", color = TextGray.copy(alpha = 0.5f), fontSize = 16.sp)
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun BasicTextFieldComponent(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        modifier = modifier,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(BrandGreen),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Preview(showBackground = true)
@Composable
fun AddTransactionPreview() {
    FinanzAppTheme {
        AddTransactionScreen(transactionId = null)
    }
}
