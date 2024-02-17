package dev.joshhalvorson.materialweather.util.navigation

sealed class NavigationRoute {
    object Back : NavigationRoute()
    object Home : NavigationRoute()
    object Settings : NavigationRoute()
}