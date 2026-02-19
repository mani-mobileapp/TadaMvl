package com.tada.mvl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tada.mvl.ui.screens.BookResultScreen
import com.tada.mvl.ui.screens.DetailScreen
import com.tada.mvl.ui.screens.HistoryScreen
import com.tada.mvl.ui.screens.MapScreen
import com.tada.mvl.ui.viewmodel.MapViewModel

@Composable
fun MVLApp() {
    val navController = rememberNavController()

    val vm: MapViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Destinations.Map.route
    ) {

        composable(Destinations.Map.route) {
            MapScreen(navController = navController, vm = vm)
        }

        composable(Destinations.Detail.route) { backStackEntry ->
            val which = backStackEntry.arguments?.getString("which") ?: "A"
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


