package com.example.recheck.viewmodel

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.recheck.roomDB.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val EMPTY_USER = UserEntity(
    id = 0,
    name = "",
    email = "",
    password = ""
)

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown viewmodel class")
    }
}

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private var _user = MutableStateFlow<UserEntity>(EMPTY_USER)
    val user = _user.asStateFlow()

    fun getUser(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.getUser(email, password)
            println("user$user")
            if (user != null) {
                _user.value = user
            }
        }
    }

    fun insertUser(userEntity: UserEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertUser(userEntity)
                onResult(true)  // ✅ 성공
            } catch (e: SQLiteConstraintException) {
                onResult(false) // ❌ 중복
            } catch (e: Exception) {
                onResult(false) // ❌ 기타 오류
            }
        }
    }


    fun updateItem(userEntity: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(userEntity)
        }
    }

    fun deleteItem(userEntity: UserEntity) {
        viewModelScope.launch {
            repository.deleteUser(userEntity)
        }
    }

    fun clearUser() {
        _user.value = EMPTY_USER
    }

    fun isUserLoggedIn(): Boolean {
        val current = _user.value
        return current.name.isNotEmpty() && current.email.isNotEmpty()
    }
}