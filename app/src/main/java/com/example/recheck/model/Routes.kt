package com.example.recheck.model

sealed class Routes(val route: String) {
    data object Main : Routes("Main")
    data object Login : Routes("Login")
    data object Mypage : Routes("Mypage")
    data object Register : Routes("Register")
    data object Init : Routes("Init")
}