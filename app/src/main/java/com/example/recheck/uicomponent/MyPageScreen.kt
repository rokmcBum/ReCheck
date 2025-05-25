package com.example.recheck.uicomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.week12.viewmodel.UserViewModel

@Composable
fun MyPageScreen(userViewModel: UserViewModel, navController: NavController) {
    val userState by userViewModel.user.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Row {
                Text(userState.name)
            }
            Row {
                Text(userState.password)
            }
            Row {
                Text(userState.id.toString())
            }
            Row {
                Text(userState.email)
            }
        }
        Button(
            onClick = {
                userViewModel.clearUser()
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Mypage.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(60.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text("로그아웃", fontWeight = FontWeight.Bold)
        }
    }
}