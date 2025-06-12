package com.example.recheck.navGraph

import androidx.annotation.DrawableRes
import com.example.recheck.R
import com.example.recheck.model.Routes

sealed class BottomNavItem(val route: String, val label: String, @DrawableRes val iconRes: Int) {
    object Home     : BottomNavItem(Routes.Mypage.route,     "내 식재료",     R.drawable.baseline_restaurant_24)
    object Calendar : BottomNavItem(Routes.Calendar.route, "캘린더", R.drawable.baseline_calendar_month_24)
    object History  : BottomNavItem(Routes.AddFood.route,  "히스토리",    R.drawable.baseline_history_24)
    object Share   : BottomNavItem(Routes.AddFood.route,   "공유",  R.drawable.baseline_share_24)
}
