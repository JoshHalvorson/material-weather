package dev.joshhalvorson.materialweather.util.navigation

sealed class NavigationRoute {
    data object Back : NavigationRoute()
    data object Home : NavigationRoute()
    data object Settings : NavigationRoute()
    data object LocationSearch : NavigationRoute()
}