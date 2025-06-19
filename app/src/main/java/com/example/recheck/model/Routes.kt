package com.example.recheck.model

import okhttp3.Route

sealed class Routes(val route: String) {
    data object Main : Routes("Main")
    data object Login : Routes("Login")
    data object Mypage : Routes("Mypage")
    data object Register : Routes("Register")
    data object Init : Routes("Init")
    data object AddFood : Routes("AddFood")
    data object Calendar : Routes("Calendar")
    data object Notification : Routes("NotificationSettings")
    data object Welcome : Routes("Welcome")
}