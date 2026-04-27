package com.example.nexusapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnToggleConfirm: ImageButton
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLogin: TextView

    private var isPasswordVisible = false
    private var isConfirmVisible  = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        initViews()
        setupClickListeners()
        setupRealTimeValidation()
        playEntranceAnimation()
    }

    private fun initViews() {
        etFullName        = findViewById(R.id.etFullName)
        etEmail           = findViewById(R.id.etRegEmail)
        etPassword        = findViewById(R.id.etRegPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnTogglePassword = findViewById(R.id.btnToggleRegPassword)
        btnToggleConfirm  = findViewById(R.id.btnToggleConfirm)
        btnRegister       = findViewById(R.id.btnRegister)
        tvLogin           = findViewById(R.id.tvBackToLogin)
    }

    private fun setupClickListeners() {

        findViewById<ImageButton>(R.id.btnBackReg).setOnClickListener { finish() }

        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.transformationMethod =
                if (isPasswordVisible) null else PasswordTransformationMethod.getInstance()
            btnTogglePassword.setImageResource(
                if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
            )
            etPassword.setSelection(etPassword.text.length)
        }

        btnToggleConfirm.setOnClickListener {
            isConfirmVisible = !isConfirmVisible
            etConfirmPassword.transformationMethod =
                if (isConfirmVisible) null else PasswordTransformationMethod.getInstance()
            btnToggleConfirm.setImageResource(
                if (isConfirmVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
            )
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }

        btnRegister.setOnClickListener {
            if (validateAll()) doRegister()
        }

        tvLogin.setOnClickListener { finish() }
    }

    private fun setupRealTimeValidation() {
        etFullName.addTextChangedListener(watcher {
            if (it.trim().isEmpty()) etFullName.error = "Nama lengkap wajib diisi"
            else etFullName.error = null
        })

        etEmail.addTextChangedListener(watcher {
            when {
                it.trim().isEmpty()   -> etEmail.error = "Email wajib diisi"
                !it.contains("@")     -> etEmail.error = "Email harus mengandung @"
                !android.util.Patterns.EMAIL_ADDRESS.matcher(it.trim()).matches()
                    -> etEmail.error = "Format email tidak valid"
                else                  -> etEmail.error = null
            }
        })

        etPassword.addTextChangedListener(watcher {
            when {
                it.isEmpty()  -> etPassword.error = "Password wajib diisi"
                it.length < 6 -> etPassword.error = "Password minimal 6 karakter"
                else          -> etPassword.error = null
            }
        })

        etConfirmPassword.addTextChangedListener(watcher {
            if (it != etPassword.text.toString())
                etConfirmPassword.error = "Password tidak cocok"
            else
                etConfirmPassword.error = null
        })
    }

    private fun watcher(action: (String) -> Unit) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { action(s.toString()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun validateAll(): Boolean {
        var valid = true

        val name = etFullName.text.toString().trim()
        if (name.isEmpty()) {
            etFullName.error = "Nama lengkap wajib diisi"; valid = false
        }

        val email = etEmail.text.toString().trim()
        when {
            email.isEmpty()   -> { etEmail.error = "Email wajib diisi"; valid = false }
            !email.contains("@") -> { etEmail.error = "Email harus mengandung @"; valid = false }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                etEmail.error = "Format email tidak valid"; valid = false
            }
        }

        val pass = etPassword.text.toString()
        when {
            pass.isEmpty()  -> { etPassword.error = "Password wajib diisi"; valid = false }
            pass.length < 6 -> { etPassword.error = "Password minimal 6 karakter"; valid = false }
        }

        val confirm = etConfirmPassword.text.toString()
        if (confirm != pass) {
            etConfirmPassword.error = "Password tidak cocok"; valid = false
        }

        return valid
    }

    private fun doRegister() {
        val name     = etFullName.text.toString().trim()
        val email    = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // ── Cek apakah email sudah terdaftar ──
        val prefs = getSharedPreferences("users", MODE_PRIVATE)
        if (prefs.contains("user_pass_$email")) {
            etEmail.error = "Email sudah terdaftar, gunakan email lain"
            return
        }

        // ── Simpan akun baru ke SharedPreferences ──
        prefs.edit()
            .putString("user_pass_$email", password)   // simpan password
            .putString("user_name_$email", name)       // simpan nama
            .apply()

        Toast.makeText(this, "✅ Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()

        // Kembali ke LoginActivity, kirim email agar auto-fill
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("REG_EMAIL", email)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun playEntranceAnimation() {
        val card  = findViewById<View>(R.id.registerCard)
        val brand = findViewById<View>(R.id.regBrandSection)

        brand.alpha = 0f; brand.translationY = -50f
        card.alpha  = 0f; card.translationY  =  60f

        brand.animate().alpha(1f).translationY(0f).setDuration(600)
            .setStartDelay(150).setInterpolator(DecelerateInterpolator(1.5f)).start()
        card.animate().alpha(1f).translationY(0f).setDuration(700)
            .setStartDelay(350).setInterpolator(DecelerateInterpolator(2f)).start()
    }
}