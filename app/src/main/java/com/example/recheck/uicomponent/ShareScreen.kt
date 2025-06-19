package com.example.recheck.uicomponent

import DateInputField
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.recheck.roomDB.FoodEntity
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
    userViewModel: UserViewModel,
    foodViewModel: FoodViewModel,
    navController: NavController
) {
    val userState by userViewModel.user.collectAsState()
    val foodsState by foodViewModel.foods.collectAsState()

    var inputUserId by remember { mutableStateOf("") }
    var sharedFoods by remember { mutableStateOf<List<FoodEntity>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                text = "내 냉장고 공유",
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
        Text("내 공유 ID: ${userState.id}", fontWeight = FontWeight.Medium)
//        Button(onClick = {
//            val clipboardManager = LocalClipboardManager.current
//            clipboardManager.setText(AnnotatedString(userState.id.toString()))
//        }) {
//            Text("내 ID 복사")
//        }

        Divider()

        // 공유할 상대 userId 입력
        OutlinedTextField(
            value = inputUserId,
            onValueChange = { inputUserId = it },
            label = { Text("공유받을 상대의 공유 ID 입력") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFFF5D5D),
                unfocusedBorderColor = Color(0xFFFF5D5D),
                cursorColor = Color(0xFFFF5D5D),
                focusedLabelColor = Color(0xFFFF5D5D)
            )
        )

        Button(
            onClick = onClick@{
                val id = inputUserId.toIntOrNull()
                if (id == null) {
                    errorMessage = "유효한 숫자 ID를 입력하세요."
                    sharedFoods = emptyList()
                    return@onClick
                }
                errorMessage = null
                foodViewModel.getFoodsByUserId(id) { foods ->
                    sharedFoods = foods
                    if (foods.isEmpty()) {
                        errorMessage = "해당 ID의 냉장고가 없거나 비어있습니다."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5D5D),
                contentColor = Color.White)
        ) {
            Text("접속해서 냉장고 보기")
        }

        errorMessage?.let {
            Text(it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(sharedFoods) { food ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFFFF5D5D), shape = MaterialTheme.shapes.medium)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(food.name, fontWeight = FontWeight.Bold)
                        Text("${food.expirationDate} 까지", color = Color.Gray)
                    }
                }
            }
        }

    }
}
