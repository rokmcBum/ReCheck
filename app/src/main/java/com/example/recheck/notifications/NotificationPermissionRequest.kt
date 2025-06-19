package com.example.recheck.notifications

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * 화면에 이 Composable을 배치만 하면,
 * POST_NOTIFICATIONS 퍼미션이 없을 때 다이얼로그를 띄워 요청합니다.
 */
@Composable
fun RequestNotificationPermissionIfNeeded() {
    val context = LocalContext.current
    val activity = (context as? Activity) ?: return

    // 1) 현재 퍼미션 보유 여부
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2) 런처 준비
    val launcher = rememberLauncherForActivityResult(RequestPermission()) { granted ->
        hasPermission.value = granted
    }

    // 3) 필요 시 다이얼로그 띄우기
    if (!hasPermission.value && activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        != PackageManager.PERMISSION_GRANTED
    ) {
        AlertDialog(
            onDismissRequest = { /* 닫기 막기 */ },
            title = { Text("알림 권한 필요") },
            text  = { Text("식재료 만료 알림을 받으려면 알림 권한을 허용해 주세요.") },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) {
                    Text("허용")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // 여기선 그냥 닫거나, 권한 없이 계속 진행하도록 처리
                }) {
                    Text("취소")
                }
            }
        )
    }
}
