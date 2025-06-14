package com.example.recheck.uicomponent

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.recheck.viewmodel.FoodViewModel
import com.example.week12.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun MyPageScreen(
    userViewModel: UserViewModel,
    foodViewModel: FoodViewModel,
    navController: NavController,
) {
    val userState by userViewModel.user.collectAsState()
    val foodsState by foodViewModel.foods.collectAsState()

    LaunchedEffect(userState) {
        foodViewModel.getMyFoods(userState.id)
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now()

    val mostUrgentFood = foodsState
        .filter { !it.isConsumed && it.expirationDate >= today }
        .minByOrNull { ChronoUnit.DAYS.between(today, it.expirationDate) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "내 식재료",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(onClick = {
                    navController.navigate(Routes.AddFood.route)
                }) {
                    Text("+", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            mostUrgentFood?.let { food ->
                val dday = ChronoUnit.DAYS.between(today, food.expirationDate)

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                        .size(160.dp)
                        .border(4.dp, Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "D-$dday",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Red
                        )
                        Text(food.name, fontWeight = FontWeight.Bold)
                        Text(food.expirationDate.format(formatter))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            foodsState.forEach { food ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("식재료 이름 : ${food.name}")
                            Text("소비기한 : ${food.expirationDate.format(formatter)}")
                        }
                        Checkbox(
                            checked = food.isConsumed,
                            onCheckedChange = {
                                foodViewModel.consumeFood(
                                    foodId = food.id,
                                    userId = userState.id
                                )
                            }
                        )
                    }
                }
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
                .align(Alignment.BottomCenter)
                .padding(24.dp)
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("로그아웃", fontWeight = FontWeight.Bold)
        }
    }
}
