package com.tada.mvl.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tada.mvl.ui.screens.BookResultScreen
import com.tada.mvl.ui.screens.DetailScreen
import com.tada.mvl.ui.screens.HistoryScreen
import com.tada.mvl.ui.screens.MapScreen
import com.tada.mvl.ui.viewmodel.MapViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MVLApp() {

    val navController = rememberNavController()
    val vm: MapViewModel = hiltViewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val title = when {
        currentRoute == Destinations.Map.route -> "TADA"
        currentRoute == Destinations.History.route -> "History"
        currentRoute == Destinations.BookResult.route -> "Booking Result"
        currentRoute?.startsWith("detail") == true -> "Details"
        else -> ""
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0), // Edge-to-edge like Uber
        topBar = {

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (currentRoute != Destinations.Map.route) {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFC400),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Destinations.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Destinations.Map.route) {
                MapScreen(navController = navController, vm = vm)
            }

            composable(Destinations.Detail.route) { backStackEntry ->
                val which =
                    backStackEntry.arguments?.getString("which") ?: "A"

                DetailScreen(
                    which = which,
                    vm = vm,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Destinations.BookResult.route) {
                BookResultScreen(
                    vm = vm,
                    onHistory = {
                        navController.navigate(Destinations.History.route)
                    }
                )
            }

            composable(Destinations.History.route) {
                HistoryScreen(
                    vm = vm,
                    onSelect = { book ->
                        vm.setFromHistory(book)
                        navController.popBackStack(
                            Destinations.Map.route,
                            false
                        )
                    }
                )
            }
        }
    }
}


