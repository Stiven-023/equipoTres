package com.univalle.equipotres

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // edge-to-edge (tu import lo tiene, si lo usas)
        enableEdgeToEdge()

        // setContentView al layout del activity (contenedor vacío para fragments)
        setContentView(R.layout.activity_main)

        // Aplica insets (status/navigation bar) al contenedor "main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Agregar (o reemplazar) el LoginFragment al contenedor fragment_container
        // Solo si la Activity se crea por primera vez (evita duplicación en rotaciones)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}
