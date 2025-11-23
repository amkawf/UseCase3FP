package pam.mobile.usecase3fp.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Goals : Screen("goals")
}