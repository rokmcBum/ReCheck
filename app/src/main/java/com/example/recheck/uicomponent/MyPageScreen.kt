package com.example.recheck.uicomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.recheck.ui.theme.Pretendard
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
            .background(color = Color(0xFFFAFAFA))
    ) {
        // 상단: 제목 + 버튼들
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(color = Color(0xFFFFFFFF))
                .padding(16.dp),
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
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 30.sp)
                }
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
        if (foodsState.isEmpty()) {
            Row(modifier = Modifier.padding(30.dp)) {
                Text("식재료를 등록해 주세요", color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(70.dp))

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
                    .size(220.dp)
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = CircleShape
                    )
                    .border(2.dp, Color(0xFFF1F1F1), CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = food.name,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = Pretendard,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF2C2C2C),
                                textAlign = TextAlign.Center
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${dday}일",
                        color = Color(0xFFFF5D5D),
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "남았어요!",
                        color = Color.Gray,
                        fontSize = 18.sp,
                        fontFamily = Pretendard,
                        fontWeight = FontWeight(600)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(foodsState.sortedByDescending {
                runCatching {
                    LocalDate.parse(it.expirationDate.toString(), formatter)
                }.getOrNull() ?: LocalDate.MAX
            }) { food ->
                val parsedDate = runCatching {
                    LocalDate.parse(food.expirationDate.toString(), formatter)
                }.getOrNull()

                val dday = parsedDate?.let { ChronoUnit.DAYS.between(today, it) } ?: Long.MAX_VALUE

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color(0xFFFFFFFF))
                        .border(1.dp, Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                        .padding(16.dp)
                        .shadow(
                            elevation = 8.dp,
                            spotColor = Color(0x08000000),
                            ambientColor = Color(0x08000000)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                if (dday > 0) {
                                    Row(
                                        modifier = Modifier,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .wrapContentHeight()
                                                .background(
                                                    color = Color(0xFFF5F5F5),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(4.dp),
                                        ) {
                                            Text(
                                                text = "D-${dday}",
                                                color = Color(0xFFFF5D5D),
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = food.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${food.expirationDate} 까지",
                                    color = Color.Gray
                                )
                            }
                            Button(
                                onClick = {
                                    navController.navigate("${Routes.Recipes.route}/${food.name}")
                                },
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFFF5D5D),
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentPadding = PaddingValues(
                                    horizontal = 8.dp,   // 좌우 여백
//                                    vertical   = 4.dp    // 상하 여백
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFFFF5D5D)
                                )
                            ) {
                                Text("요리 추천")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Checkbox(
                                checked = food.isConsumed,
                                onCheckedChange = {
                                    foodViewModel.consumeFood(
                                        foodId = food.id,
                                        userId = userState.id
                                    )
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFF5D5D),  // 체크된 박스 배경
                                    uncheckedColor = Color(0xFFF1F1F1),    // 언체크 박스 테두리
                                    checkmarkColor = Color.White,        // 체크마크 색
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

