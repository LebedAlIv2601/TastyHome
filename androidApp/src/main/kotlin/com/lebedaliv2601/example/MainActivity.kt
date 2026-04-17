package com.lebedaliv2601.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.handleDeepLink
import com.arkivanov.decompose.retainedComponent
import com.lebedaliv2601.base.foundation.uri.Uri
import com.lebedaliv2601.example.shared.language.updateContextWithActualLanguage
import com.lebedaliv2601.example.shared.root.Root
import com.lebedaliv2601.example.shared.root.RootComponent

class MainActivity : ComponentActivity() {

    private var rootComponent: RootComponent? = null

    override fun attachBaseContext(newBase: Context?) {
        val newContext = newBase?.let { updateContextWithActualLanguage(context = newBase) } ?: newBase
        super.attachBaseContext(newContext)
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        val app = (applicationContext as App)
        app.activityHolder.activity = this

        rootComponent = retainedComponent { componentContext ->
            appGraph.rootComponentFactory().create(componentContext)
        }

        handleDeepLink { it?.let { rootComponent?.newDeeplink(Uri(it)) } ?: return@handleDeepLink }

        setContent {
            rootComponent?.let { Root(it) }
        }
    }
}