package com.example.nexusapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class HasilPendaftaranActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_pendaftaran)
        supportActionBar?.hide()

        // Tampilkan data pendaftaran
        findViewById<TextView>(R.id.tvNamaHasil).text         = intent.getStringExtra("NAMA")          ?: "-"
        findViewById<TextView>(R.id.tvEmailHasil).text        = intent.getStringExtra("EMAIL")         ?: "-"
        findViewById<TextView>(R.id.tvHpHasil).text           = intent.getStringExtra("HP")            ?: "-"
        findViewById<TextView>(R.id.tvJenisKelaminHasil).text = intent.getStringExtra("JENIS_KELAMIN") ?: "-"
        findViewById<TextView>(R.id.tvSeminarHasil).text      = intent.getStringExtra("SEMINAR")       ?: "-"

        // Ambil data user login untuk dikirim balik ke MainActivity
        val userName  = intent.getStringExtra("USER_NAME")  ?: "User"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        findViewById<MaterialButton>(R.id.btnKembaliUtama).setOnClickListener {
            // Kirim kembali nama & email asli ke halaman utama
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("USER_NAME",  userName)
            intent.putExtra("USER_EMAIL", userEmail)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}
