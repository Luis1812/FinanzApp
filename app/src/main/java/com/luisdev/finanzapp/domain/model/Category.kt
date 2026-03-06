package com.luisdev.finanzapp.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.luisdev.finanzapp.ui.theme.BrandGreen
import com.luisdev.finanzapp.ui.theme.TextGray

data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
) {
    companion object {
        val defaultCategories = listOf(
            Category("1", "Alimentación", Icons.Rounded.ShoppingCart, Color(0xFFE67E22)),
            Category("2", "Transporte", Icons.Rounded.DirectionsBus, Color(0xFF3498DB)),
            Category("3", "Ocio", Icons.Rounded.Celebration, Color(0xFF9B59B6)),
            Category("4", "Salud", Icons.Rounded.MedicalServices, Color(0xFFE74C3C)),
            Category("5", "Vivienda", Icons.Rounded.Home, Color(0xFFF1C40F)),
            Category("6", "Sueldo", Icons.Rounded.MonetizationOn, BrandGreen),
            Category("7", "Regalos", Icons.Rounded.CardGiftcard, Color(0xFF1ABC9C)),
            Category("8", "Viajes", Icons.Rounded.AirplanemodeActive, Color(0xFF1350CE)),
            Category("9", "Otros", Icons.Rounded.Category, TextGray)
        )
    }
}
