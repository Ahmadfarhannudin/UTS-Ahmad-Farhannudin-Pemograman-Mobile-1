package com.example.nexusapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private var userName  = "User"
    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        // Ambil data dari intent (login)
        userName  = intent.getStringExtra("USER_NAME")  ?: "User"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val tvEmail   = findViewById<TextView>(R.id.tvEmail)
        val btnDaftar = findViewById<MaterialButton>(R.id.btnDaftarSeminar)

        // Tampilkan nama sesuai data login
        tvWelcome.text = "Halo, ${userName.replaceFirstChar { it.uppercase() }}! 👋"
        tvEmail.text   = userEmail

        btnDaftar.setOnClickListener {
            val intent = Intent(this, FormPendaftaranActivity::class.java)
            intent.putExtra("USER_NAME",  userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }
}
