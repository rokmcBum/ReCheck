package com.example.recheck.uicomponent

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.recheck.viewmodel.FoodViewModel
import com.example.recheck.viewmodel.RecipeViewModel
//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

@Composable
fun RecipeScreen(
    ingredient: String,
    navController: NavController,
    viewModel: RecipeViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(ingredient) {
        viewModel.fetchRecipes(ingredient)
    }

    val recipes by viewModel.recipes.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = Color(0xFFFF5D5D)
                )
            }
            Text(" ${ingredient} 요리 추천", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            if (error != null) {
                Text(error ?: "", color = Color.Red)
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(recipes) { recipe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipe.detailUrl))
                            context.startActivity(intent)

                        }
                ) {

                    Row(Modifier.padding(8.dp)) {
//
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(recipe.title, fontWeight = FontWeight.Bold)
                           // Text(recipe.detailUrl, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}


