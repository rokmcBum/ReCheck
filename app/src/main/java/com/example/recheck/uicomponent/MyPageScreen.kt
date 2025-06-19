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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.recheck.viewmodel.UserViewModel
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
        .filter {
            !it.isConsumed && runCatching {
                LocalDate.parse(it.expirationDate.toString(), formatter) >= today
            }.getOrDefault(false)
        }
        .minByOrNull {
            runCatching {
                ChronoUnit.DAYS.between(
                    today,
                    LocalDate.parse(it.expirationDate.toString(), formatter)
                )
            }.getOrDefault(Long.MAX_VALUE)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 상단: 제목 + 버튼들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "내 식재료",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                Button(
                    onClick = { navController.navigate(Routes.AddFood.route) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("+", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        userViewModel.clearUser()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    )
                ) {
                    Text("로그아웃", fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 가장 임박한 식재료 원형 표시
        mostUrgentFood?.let { food ->
            val dday =
                ChronoUnit.DAYS.between(
                    today,
                    LocalDate.parse(food.expirationDate.toString(), formatter)
                )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
                    .size(220.dp)
                    .border(2.dp, Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = food.name,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${dday}일",
                        color = Color(0xFFFF5D5D),
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text("남았어요!", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn 전체 리스트
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(foodsState) { food ->
                val dday =
                    ChronoUnit.DAYS.between(
                        today,
                        LocalDate.parse(food.expirationDate.toString(), formatter)
                    )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            if (dday > 0) {
                                Text(
                                    text = "D-${dday}",
                                    color = Color(0xFFFF5D5D),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = food.name,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${food.expirationDate} 까지",
                                color = Color.Gray
                            )
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
    }
}

