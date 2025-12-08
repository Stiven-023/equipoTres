package com.univalle.equipotres.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.univalle.equipotres.R
import com.univalle.equipotres.databinding.ActivityMainBinding
import com.univalle.equipotres.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultar ActionBar
        supportActionBar?.hide()

        // Hacer que la app use toda la pantalla
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Determinar el destino inicial según la sesión
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (sessionManager.isLoggedIn()) {
            navGraph.setStartDestination(R.id.homeFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }

        navController.graph = navGraph
    }

    override fun onPause() {
        super.onPause()
        // No borrar la sesión si la actividad se está reiniciando por rotación/config change
        //if (!isChangingConfigurations) {
        //    sessionManager.clearSession()
        //}
    }

    override fun onDestroy() {
        super.onDestroy()
        // Igual protección por si se llama durante un cambio de configuración
        //if (!isChangingConfigurations) {
        //    sessionManager.clearSession()
        //}
    }
}