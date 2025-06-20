package com.example.recheck.navGraph

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.recheck.model.Routes
import com.example.recheck.roomDB.RecheckDatabase
import com.example.recheck.viewmodel.FoodRepository
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.FoodViewModelFactory
import com.example.recheck.viewmodel.UserRepository
import com.example.recheck.viewmodel.UserViewModel
import com.example.recheck.viewmodel.UserViewModelFactory

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun RecheckApp() {
    val context = LocalContext.current

    // 1) Database & Repos & ViewModels
    val db = RecheckDatabase.getDBInstance(context)
    val userRepo = UserRepository(db)
    val foodRepo = FoodRepository(db)
    val userVM: UserViewModel = viewModel(factory = UserViewModelFactory(userRepo))
    val foodVM: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodRepo))

    // 2) NavController
    val navController = rememberNavController()

    // 현재 경로 관찰
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 4) Scaffold + BottomBar + NavGraph 호출
    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf(
                    Routes.Init.route,
                    Routes.Login.route,
                    Routes.Register.route,
                    Routes.Welcome.route,
                )
            ) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            userViewModel = userVM,
            foodViewModel = foodVM,
            modifier = Modifier.padding(innerPadding)
        )
    }
}