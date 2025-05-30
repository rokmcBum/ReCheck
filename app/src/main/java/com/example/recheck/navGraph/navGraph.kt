package com.example.recheck.navGraph

import AddFoodScreen
import RegisterScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recheck.model.Routes
import com.example.recheck.uicomponent.InitScreen
import com.example.recheck.uicomponent.LoginScreen
import com.example.recheck.uicomponent.MyPageScreen
import com.example.recheck.viewmodel.FoodRepository
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.FoodViewModelFactory
import com.example.week12.roomDB.RecheckDatabase
import com.example.week12.viewmodel.UserRepository
import com.example.week12.viewmodel.UserViewModel
import com.example.week12.viewmodel.UserViewModelFactory


@Composable
fun RecheckApp() {
    val context = LocalContext.current
    val db = RecheckDatabase.getDBInstance(context)
    val userRepo = UserRepository(db)
    val foodRepo = FoodRepository(db)
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(userRepo))
    val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(foodRepo))

    NavGraph(userViewModel = userViewModel, foodViewModel = foodViewModel)
}

@Composable
fun NavGraph(userViewModel: UserViewModel, foodViewModel: FoodViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.Init.route
    ) {
//        composable(Routes.Main.route) {
//            if (userViewModel.isUserLoggedIn()) {
//                navController.navigate(Routes.Mypage.route) {
//                    popUpTo(Routes.Main.route) { inclusive = true }
//                }
//            } else {
//                LoginScreen(userViewModel = userViewModel, navController = navController)
//            }
//        }
        composable(Routes.Mypage.route) {
            MyPageScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
        composable(Routes.Register.route) {
            RegisterScreen(userViewModel = userViewModel, navController = navController)
        }
        composable(Routes.Login.route) {
            LoginScreen(userViewModel = userViewModel, navController = navController)
        }
        composable(Routes.Init.route) {
            InitScreen(navController = navController)
        }
        composable(Routes.AddFood.route) {
            AddFoodScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
    }
}