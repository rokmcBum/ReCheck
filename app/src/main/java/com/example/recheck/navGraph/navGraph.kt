package com.example.recheck.navGraph

import AddFoodScreen
import LoginScreen
import RegisterScreen
import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.recheck.calendar.ui.CalendarScreen
import com.example.recheck.model.Routes
import com.example.recheck.uicomponent.HistoryScreen
import com.example.recheck.uicomponent.InitScreen
import com.example.recheck.uicomponent.MyPageScreen
import com.example.recheck.uicomponent.RecipeScreen
import com.example.recheck.uicomponent.ShareScreen
import com.example.recheck.uicomponent.WelcomeScreen
import com.example.recheck.uicomponent.permissions.NotificationSettingsScreen
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.UserViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun NavGraph(
    navController: NavHostController,
    userViewModel: UserViewModel,
    foodViewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Init.route,
        modifier = modifier
    ) {
        composable(Routes.Init.route) {
            InitScreen(navController)
        }
        composable(Routes.Login.route) {
            // ① 로그인 상태 구독
            val user by userViewModel.user.collectAsState()
            // ② 로그인 되어 있으면 바로 MyPage로
            LaunchedEffect(user) {
                if (user.id != 0) {
                    navController.navigate(Routes.Mypage.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                }
            }
            // ③ 아니면 로그인 화면
            LoginScreen(
                userViewModel = userViewModel,
                navController = navController,
            )
        }
        composable(Routes.Register.route) {
            RegisterScreen(userViewModel = userViewModel, navController = navController)
        }
        composable(Routes.Main.route) {
            InitScreen(navController)
        }
        composable(Routes.Welcome.route) {
            WelcomeScreen(navController)
        }
        composable(Routes.Calendar.route) {
            CalendarScreen(
                currentUserId = userViewModel.user.value.id,
                navController = navController
            )
        }
        composable(Routes.AddFood.route) {
            AddFoodScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
        composable(Routes.Mypage.route) {
            MyPageScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
        composable(Routes.Notification.route) {
            NotificationSettingsScreen(navController)
        }
        composable(Routes.Share.route) {
            ShareScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
        composable(Routes.History.route) {
            HistoryScreen(
                userViewModel = userViewModel,
                foodViewModel = foodViewModel,
                navController = navController
            )
        }
//        composable(Routes.Recipes.route) { backStackEntry ->
//            val ingredient = backStackEntry.arguments?.getString("ingredient") ?: ""
//            RecipeScreen(ingredient, foodViewModel, navController)
//        }
        composable(
            route = "${Routes.Recipes.route}/{ingredient}",
            arguments = listOf(navArgument("ingredient") { type = NavType.StringType })
        ) { backStackEntry ->
            val ingredient = backStackEntry.arguments?.getString("ingredient") ?: ""
            RecipeScreen(
                ingredient = ingredient,
                navController = navController
            )
        }

    }
}