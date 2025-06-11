package org.christianalexandre.remotecontroller

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.christianalexandre.remotecontroller.presentation.App

lateinit var appContext: Context
    private set

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        appContext = this
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}