package com.example.recheck.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recheck.roomDB.FoodEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FoodViewModelFactory(private val repository: FoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            return FoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}

class FoodViewModel(private val repository: FoodRepository) : ViewModel() {
    private var _foods = MutableStateFlow<List<FoodEntity>>(emptyList())
    val foods = _foods.asStateFlow()

    val _recipes = MutableStateFlow<List<String>>(emptyList())
    val recipes = _recipes.asStateFlow()

    fun getMyFoods(userId: Int) {
        viewModelScope.launch {
            val foods = repository.getMyFoods(userId)
            println("foods$foods")
            _foods.value = foods
        }
    }

    fun insertFood(foodEntity: FoodEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertFood(foodEntity)
                onResult(true)  // ✅ 성공
            } catch (e: SQLiteConstraintException) {
                onResult(false) // ❌ 중복
            } catch (e: Exception) {
                onResult(false) // ❌ 기타 오류
            }
        }
    }


    fun consumeFood(foodId: Int, userId: Int) {
        viewModelScope.launch {
            repository.consumeFood(foodId)
            getMyFoods(userId)
        }
    }

    fun deleteItem(foodEntity: FoodEntity) {
        viewModelScope.launch {
            repository.deleteFood(foodEntity)
        }
    }


    fun clearFoods() {
        _foods.value = emptyList()
    }

    fun getFoodsByUserId(userId: Int, onResult: (List<FoodEntity>) -> Unit) {
        viewModelScope.launch {
            val foods = repository.getFoodsByUserId(userId)
            onResult(foods)
        }
    }
    fun getRecipesByIngredient(ingredient: String) {
        viewModelScope.launch {
            try {
                //val response = apiService.getRecipes(ingredient, API_KEY)
                //_recipes.value = response.results.map { it.title }
            } catch (e: Exception) {
                _recipes.value = listOf("요리 추천 실패: ${e.message}")
            }
        }
    }

}