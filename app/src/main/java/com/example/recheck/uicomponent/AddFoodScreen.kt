import android.app.DatePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.recheck.model.Routes
import com.example.recheck.roomDB.FoodEntity
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    userViewModel: UserViewModel,
    foodViewModel: FoodViewModel,
    navController: NavController
) {
    val user by userViewModel.user.collectAsState()
    var name by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TopAppBar(
                title = { Text("식재료 등록하기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 이름 입력
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("식재료 이름 입력") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DateInputField(
                label = "소비기한 선택",
                selectedDate = expirationDate,
                onDateSelected = { expirationDate = it }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 등록 버튼
            Button(
                onClick = {
                    if (name.isBlank() || expirationDate.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("모든 항목을 입력해 주세요")
                        }
                    } else {
                        val parsedDate: LocalDate = LocalDate.parse(expirationDate, ISO_DATE)

                        foodViewModel.insertFood(
                            FoodEntity(
                                name = name,
                                expirationDate = parsedDate,
                                isConsumed = false,
                                userId = user.id,
                            )
                        ) { success ->
                            if (success) {
                                navController.navigate(Routes.Mypage.route) {
                                    popUpTo(Routes.AddFood.route) { inclusive = true }
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("이미 존재하는 이메일입니다")
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("등록 버튼")
            }
        }
    }
}

@Composable
fun DateInputField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, y, m, d ->
            val formatted = "%04d-%02d-%02d".format(y, m + 1, d)
            onDateSelected(formatted)
        }, year, month, day)
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            datePickerDialog.show()
        }) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

