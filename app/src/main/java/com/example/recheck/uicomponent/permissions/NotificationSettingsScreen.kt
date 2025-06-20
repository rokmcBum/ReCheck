package com.example.recheck.uicomponent.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.recheck.notifications.NotificationScheduler
import com.example.recheck.notifications.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current.applicationContext
    val prefs = context.getSharedPreferences("ReCheckPrefs", Context.MODE_PRIVATE)

    var enabled by remember { mutableStateOf(prefs.getBoolean("notifications_enabled", false)) }
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Dialog to inform missing permission
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("알림 권한 필요") },
            text = { Text("알림을 받으려면 시스템 권한 설정에서 알림을 허용해 주세요.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    // Open system notification settings
                    NotificationHelper.openNotificationSettings(context)
                }) {
                    Text("설정 열기")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("알림 설정") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("만료 알림", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = enabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            // Check permission
                            val hasPerm = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPerm) {
                                enabled = true
                                prefs.edit().putBoolean("notifications_enabled", true).apply()
                                NotificationScheduler.scheduleDailyCheck(context)
                            } else {
                                showPermissionDialog = true
                            }
                        } else {
                            enabled = false
                            prefs.edit().putBoolean("notifications_enabled", false).apply()
                            NotificationScheduler.cancelDailyCheck(context)
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor   = Color(0xFFFF5D5D),                   // on일 때 손가락 닿는 부분
                        checkedTrackColor   = Color(0xFFFF5D5D).copy(alpha = 0.5f),// on일 때 트랙
                        uncheckedThumbColor = Color(0xFF656565),                          // off일 때 손가락
                        uncheckedTrackColor = Color(0xFF656565).copy(alpha = 0.3f)        // off일 때 트랙
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("식재료 소비기한이 끝나기 2일 전부터 하루에 한 번 알림을 받아요.")
        }
    }
}
