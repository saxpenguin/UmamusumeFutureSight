package com.saxpenguin.umamusumefuturesight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.saxpenguin.umamusumefuturesight.navigation.AppNavigation
import com.saxpenguin.umamusumefuturesight.ui.theme.UmamusumeFutureSightTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the application.
 * Annotated with @AndroidEntryPoint to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UmamusumeFutureSightTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
