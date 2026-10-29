package com.example.diariodedespesas

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.diariodedespesas.data.local.AppDatabase
import com.example.diariodedespesas.data.repository.AuthRepository
import com.example.diariodedespesas.ui.navigation.AppNavigation
import com.example.diariodedespesas.ui.navigation.Route
import com.example.diariodedespesas.ui.theme.DiarioDeDespesasTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val prefs = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val authRepo = AuthRepository(db.userDao(), prefs)
        val startDestination = if (authRepo.currentUser != null) Route.DASHBOARD else Route.LOGIN

        setContent {
            DiarioDeDespesasTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    db = db,
                    authRepo = authRepo,
                    startDestination = startDestination
                )
            }
        }
    }
}
