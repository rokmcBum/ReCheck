package com.example.recheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.recheck.navGraph.RecheckApp
import com.example.recheck.ui.theme.ReCheckTheme
import com.example.recheck.uicomponent.permissions.RequestNotificationPermissionIfNeeded

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReCheckTheme {
                RequestNotificationPermissionIfNeeded()
                RecheckApp()
            }
        }
    }
}