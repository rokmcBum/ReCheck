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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun HistoryScreen(userViewModel: UserViewModel,
                  foodViewModel: FoodViewModel,
                  navController: NavController) {
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
                text = "히스토리",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Row {

                Button(
                    onClick = {
                        userViewModel.clearUser()
                        foodViewModel.clearFoods()
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

        //

        if (foodsState.isEmpty()) {
            Text("식재료를 등록해 주세요", color = Color.Gray)
        }
        Column {
            Text("소비기한 내 먹은 식재료 TOP 3",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5D5D)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(200.dp)
                ,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(foodsState.sortedByDescending {
                    it.ConsumeCount
                }.take(3)) { food ->
                    val parsedDate = runCatching {
                        LocalDate.parse(food.expirationDate.toString(), formatter)
                    }.getOrNull()

                    val dday =
                        parsedDate?.let { ChronoUnit.DAYS.between(today, it) } ?: Long.MAX_VALUE

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFFF5D5D), shape = MaterialTheme.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = food.name,

                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${food.ConsumeCount.toString()}번 소비",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Column {
            Text("소비기한 내 못 먹은 식재료 TOP 3",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5D5D)
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(200.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(foodsState.sortedBy {
                    it.ConsumeCount
                }.take(3)) { food ->
                    val parsedDate = runCatching {
                        LocalDate.parse(food.expirationDate.toString(), formatter)
                    }.getOrNull()

                    val dday =
                        parsedDate?.let { ChronoUnit.DAYS.between(today, it) } ?: Long.MAX_VALUE

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFFF5D5D), shape = MaterialTheme.shapes.medium)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = food.name,

                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${food.ConsumeCount.toString()}번 소비",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
