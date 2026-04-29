package com.example.gepetinho.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.gepetinho.presentation.auth.AuthState
import com.example.gepetinho.presentation.auth.AuthViewModel
import com.example.gepetinho.presentation.auth.User
import com.example.gepetinho.presentation.list.ListRoute
import com.example.gepetinho.presentation.list.ListViewModel
import com.example.gepetinho.presentation.login.LoginRoute
import com.example.gepetinho.presentation.login.LoginViewModel

@Composable
fun GepetinhoRoot(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val startDestination = when (authState) {
        is AuthState.LoggedIn -> Graph.Main.route
        AuthState.LoggedOut -> Graph.Auth.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(
            onLoginSuccess = { user ->
                authViewModel.onLoginSuccess(user)
                navController.navigate(Screen.List.route) {
                    popUpTo(Graph.Auth.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        )
        mainGraph(onLogout = authViewModel::logout)
    }
}

private fun NavGraphBuilder.authGraph(
    onLoginSuccess: (User) -> Unit
) {
    navigation(
        route = Graph.Auth.route,
        startDestination = Screen.Login.route
    ) {
        composable(route = Screen.Login.route) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginRoute(
                viewModel = loginViewModel,
                onLoginSuccess = onLoginSuccess
            )
        }
    }
}

private fun NavGraphBuilder.mainGraph(
    onLogout: () -> Unit
) {
    navigation(
        route = Graph.Main.route,
        startDestination = Screen.List.route
    ) {
        composable(route = Screen.List.route) {
            val listViewModel: ListViewModel = hiltViewModel()
            ListRoute(
                viewModel = listViewModel,
                onPokemonClick = {},
                onLogout = onLogout
            )
        }
    }
}

sealed class Graph(val route: String) {
    data object Auth : Graph("auth_graph")
    data object Main : Graph("main_graph")
}

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object List : Screen("list")
}
