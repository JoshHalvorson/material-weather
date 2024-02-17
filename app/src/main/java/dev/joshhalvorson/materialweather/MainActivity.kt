package dev.joshhalvorson.materialweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.joshhalvorson.materialweather.ui.screens.HomeScreen
import dev.joshhalvorson.materialweather.ui.screens.SettingsScreen
import dev.joshhalvorson.materialweather.ui.theme.MaterialWeatherTheme
import dev.joshhalvorson.materialweather.util.navigation.NavigationRoute

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialWeatherTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavigationRoute.Home.javaClass.simpleName
                    ) {
                        composable(NavigationRoute.Home.javaClass.simpleName) {
                            HomeScreen(
                                navigateTo = {
                                    handleNavigation(
                                        navController = navController,
                                        route = it
                                    )
                                }
                            )
                        }

                        composable(NavigationRoute.Settings.javaClass.simpleName) {
                            SettingsScreen(
                                navigateTo = {
                                    handleNavigation(
                                        navController = navController,
                                        route = it
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleNavigation(navController: NavController, route: NavigationRoute) {
        if (route == NavigationRoute.Back) {
            navController.popBackStack()
            return
        }

        navController.navigate(route.javaClass.simpleName) {
            launchSingleTop = true
        }
    }
}