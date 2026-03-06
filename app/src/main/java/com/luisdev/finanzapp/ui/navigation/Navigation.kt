package com.luisdev.finanzapp.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.luisdev.finanzapp.ui.AddTransactionScreen
import com.luisdev.finanzapp.ui.FullScreenImageScreen
import com.luisdev.finanzapp.ui.InitialConfigurationScreen
import com.luisdev.finanzapp.ui.MainScreen
import com.luisdev.finanzapp.ui.ProfileScreen
import com.luisdev.finanzapp.ui.SplashScreen

@Composable
fun FinanzAppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(
            route = Screen.Splash.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            }
        ) {
            SplashScreen(
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToSetup = {
                    navController.navigate(Screen.InitialConfiguration.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Main.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700)
                )
            }
        ) {
            MainScreen(
                onNavigateToAddTransaction = {
                    navController.navigate(Screen.AddTransaction.createRoute())
                },
                onNavigateToProfileSetup = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToEditTransaction = { transactionId ->
                    navController.navigate(Screen.AddTransaction.createRoute(transactionId))
                }
            )
        }
        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = -1
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: -1

            AddTransactionScreen(
                transactionId = if (transactionId == -1) null else transactionId,
                onClose = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.InitialConfiguration.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            }
        ) {
            InitialConfigurationScreen(
                onNavigateBack = { navController.popBackStack() },
                onFinish = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.InitialConfiguration.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Profile.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            }
        ) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToFullScreenImage = { imagePath ->
                    navController.navigate(Screen.FullScreenImage.createRoute(imagePath))
                }
            )
        }
        composable(
            route = Screen.FullScreenImage.route,
            arguments = listOf(
                navArgument("imagePath") { type = NavType.StringType }
            ),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(700)
                )
            }
        ) { backStackEntry ->
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
            FullScreenImageScreen(
                imagePath = imagePath,
                onClose = { navController.popBackStack() }
            )
        }
    }
}
