package com.example.recheck.uicomponent

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Google")
        Spacer(modifier = Modifier.width(8.dp))
        Text("Sign in with Google")
    }
}