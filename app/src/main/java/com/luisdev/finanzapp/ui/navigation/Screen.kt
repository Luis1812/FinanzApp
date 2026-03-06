package com.luisdev.finanzapp.ui.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object AddTransaction : Screen("add_transaction?transactionId={transactionId}") {
        fun createRoute(transactionId: Int? = null) = if (transactionId != null) {
            "add_transaction?transactionId=$transactionId"
        } else {
            "add_transaction?transactionId=-1"
        }
    }
    object InitialConfiguration : Screen("initial_configuration")
    object Profile : Screen("profile")
    object FullScreenImage : Screen("full_screen_image/{imagePath}") {
        fun createRoute(imagePath: String) = "full_screen_image/${URLEncoder.encode(imagePath, StandardCharsets.UTF_8.toString())}"
    }
}