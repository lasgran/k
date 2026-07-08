package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.ProductionViewModel
import com.example.ui.ProductionViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ProductionViewModel by viewModels {
        val app = application as ProductionApp
        ProductionViewModelFactory(app.productionRepository, app.userPreferencesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val session by viewModel.sessionState.collectAsState()

                // Automatic session redirection (Login <-> Home)
                LaunchedEffect(session.isLoggedIn) {
                    if (session.isLoggedIn) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = if (session.isLoggedIn) "home" else "login",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login") {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToMorning = { navController.navigate("morning_production") },
                            onNavigateToNight = { navController.navigate("night_production") },
                            onNavigateToClosing = { navController.navigate("closing") },
                            onNavigateToHistory = { navController.navigate("history") },
                            onNavigateToDashboard = { navController.navigate("dashboard") },
                            onNavigateToUsers = { navController.navigate("users") },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("morning_production") {
                        ProductionFormScreen(
                            viewModel = viewModel,
                            shift = "Manhã",
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("night_production") {
                        ProductionFormScreen(
                            viewModel = viewModel,
                            shift = "Noite",
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("closing") {
                        ClosingFormScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("history") {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("dashboard") {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable("users") {
                        UsersScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

