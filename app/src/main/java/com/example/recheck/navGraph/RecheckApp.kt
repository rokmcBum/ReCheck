package com.example.recheck.navGraph

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    // 3) Google Sign-In Client & Launcher
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope("https://www.googleapis.com/auth/calendar.readonly"))
        .build()
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val scope = "oauth2:https://www.googleapis.com/auth/calendar.readonly"
                    val token = GoogleAuthUtil.getToken(context, account.account!!, scope)
                    context.getSharedPreferences("ReCheckPrefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("calendar_token", token)
                        .apply()
                    withContext(Dispatchers.Main) {
                        navController.navigate(Routes.Mypage.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    }
                } catch (e: UserRecoverableAuthException) {
                    withContext(Dispatchers.Main) {
                        context.startActivity(e.intent)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "토큰 발급에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Google 로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
        }
    }


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
            onGoogleSignIn = { signInLauncher.launch(googleSignInClient.signInIntent) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}