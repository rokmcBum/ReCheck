package com.example.recheck.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

data class Recipe(
    val title: String,
   // val imageUrl: String,
    val detailUrl: String
)
class RecipeViewModel : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes = _recipes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun fetchRecipes(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = "https://www.10000recipe.com/recipe/list.html?q=${keyword}"
                val doc = Jsoup.connect(url).get()
                val elements = doc.select(".common_sp_list_ul li.common_sp_list_li")

                val list = elements.map {
                    val title = it.selectFirst(".common_sp_caption_tit")?.text() ?: ""
                   // val imageUrl = it.selectFirst(".common_sp_thumb img")?.attr("src") ?: ""
                    val detailUrl = "https://www.10000recipe.com" +
                            (it.selectFirst("a.common_sp_link")?.attr("href") ?: "")

                    Recipe(title,detailUrl)//imageUrl,  detailUrl
                }

                _recipes.value = list
                _error.value = null
            } catch (e: Exception) {
                _recipes.value = emptyList()
                _error.value = "레시피 가져오기 실패: ${e.message}"
            }
        }
    }
}