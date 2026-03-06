package com.luisdev.finanzapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.luisdev.finanzapp.ui.navigation.FinanzAppNavHost
import com.luisdev.finanzapp.ui.theme.FinanzAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanzAppTheme {
                FinanzAppNavHost()
            }
        }
    }
}
