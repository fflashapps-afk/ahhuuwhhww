package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.MainSearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SearchViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: SearchViewModel = viewModel()
      val themeState by viewModel.themeState.collectAsStateWithLifecycle()
      val darkTheme = when (themeState) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
      }

      MyApplicationTheme(darkTheme = darkTheme) {
        MainSearchScreen(viewModel = viewModel)
      }
    }
  }
}
