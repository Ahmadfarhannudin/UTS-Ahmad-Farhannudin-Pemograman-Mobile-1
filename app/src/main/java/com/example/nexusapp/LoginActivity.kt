package com.example.nexusapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogle: MaterialButton
    private lateinit var btnApple: MaterialButton
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvSignUp: TextView
    private lateinit var loginCard: CardView
    private lateinit var brandSection: View
    private lateinit var signUpSection: View
    private lateinit var blob1: View
    private lateinit var blob2: View
    private lateinit var blob3: View

    // TextView error realtime
    private lateinit var tvEmailError: TextView
    private lateinit var tvPasswordError: TextView

    private var isPasswordVisible = false
    private var blobAnimators = mutableListOf<ValueAnimator>()

    // Data user hardcode
    private val validUsers = mutableMapOf(
        "admin" to Pair("admin123", "Admin"),
        "user@nexus.com"  to Pair("user123",  "User"),
        "ahmad@gmail.com" to Pair("ahmad123", "Ahmad")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        setContentView(R.layout.activity_login)
        initViews()
        setupWindowInsets()
        setupClickListeners()
        setupInputFocusAnimations()
        setupRealtimeValidation()   // ← tambahan baru
        startBlobAnimations()
        playEntranceAnimation()

        // Terima email dari halaman registrasi
        val regEmail = intent.getStringExtra("REG_EMAIL")
        if (!regEmail.isNullOrEmpty()) {
            etEmail.setText(regEmail)
            etEmail.setSelection(regEmail.length)
            Toast.makeText(this, "Akun berhasil dibuat! Silakan login.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        etEmail           = findViewById(R.id.etEmail)
        etPassword        = findViewById(R.id.etPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnLogin          = findViewById(R.id.btnLogin)
        btnGoogle         = findViewById(R.id.btnGoogle)
        btnApple          = findViewById(R.id.btnApple)
        tvForgotPassword  = findViewById(R.id.tvForgotPassword)
        tvSignUp          = findViewById(R.id.tvSignUp)
        loginCard         = findViewById(R.id.loginCard)
        brandSection      = findViewById(R.id.brandSection)
        signUpSection     = findViewById(R.id.signUpSection)
        blob1             = findViewById(R.id.blob1)
        blob2             = findViewById(R.id.blob2)
        blob3             = findViewById(R.id.blob3)
        tvEmailError      = findViewById(R.id.tvEmailError)
        tvPasswordError   = findViewById(R.id.tvPasswordError)
    }

    // ─── REALTIME VALIDASI
    private fun setupRealtimeValidation() {

        // Realtime validasi EMAIL
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                when {
                    email.isEmpty() -> {
                        showEmailError("Email tidak boleh kosong")
                        setInputBorder(etEmail, false)
                    }
                    !email.contains("@") -> {
                        showEmailError("Email harus mengandung '@'")
                        setInputBorder(etEmail, false)
                    }
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                        showEmailError("Format email tidak valid")
                        setInputBorder(etEmail, false)
                    }
                    else -> {
                        hideEmailError()
                        setInputBorder(etEmail, true)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Realtime validasi PASSWORD
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val pass = s.toString()
                when {
                    pass.isEmpty() -> {
                        showPasswordError("Password tidak boleh kosong")
                        setInputBorder(etPassword, false)
                    }
                    pass.length < 6 -> {
                        showPasswordError("Password minimal 6 karakter (${pass.length}/6)")
                        setInputBorder(etPassword, false)
                    }
                    else -> {
                        hidePasswordError()
                        setInputBorder(etPassword, true)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun showEmailError(msg: String) {
        tvEmailError.text       = "⚠ $msg"
        tvEmailError.visibility = View.VISIBLE
    }

    private fun hideEmailError() {
        tvEmailError.visibility = View.GONE
    }

    private fun showPasswordError(msg: String) {
        tvPasswordError.text       = "⚠ $msg"
        tvPasswordError.visibility = View.VISIBLE
    }

    private fun hidePasswordError() {
        tvPasswordError.visibility = View.GONE
    }

    // Ganti border input: merah = error, ungu = valid
    private fun setInputBorder(field: EditText, isValid: Boolean) {
        val container = field.parent.parent
        val bgView    = (container as? android.widget.FrameLayout)?.getChildAt(0)
        bgView?.setBackgroundResource(
            if (isValid) R.drawable.input_bg_focused else R.drawable.input_bg_error
        )
    }
    // ─────────────────────────────────────────────────────────────────────

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClickListeners() {
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.transformationMethod = null
                btnTogglePassword.setImageResource(R.drawable.ic_eye)
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                btnTogglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            etPassword.setSelection(etPassword.text.length)
            animateToggleButton(btnTogglePassword)
        }

        btnLogin.setOnClickListener {
            val email    = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            if (validateInputs(email, password)) {
                attemptLogin(email, password)
            }
        }

        btnGoogle.setOnClickListener {
            animateSocialButton(it)
            Toast.makeText(this, "Google Sign-In coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnApple.setOnClickListener {
            animateSocialButton(it)
            Toast.makeText(this, "Apple Sign-In coming soon!", Toast.LENGTH_SHORT).show()
        }

        tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "Reset link sent to your email!", Toast.LENGTH_SHORT).show()
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin(email: String, password: String) {
        // 1. Ambil data dari SharedPreferences (tempat RegisterActivity menyimpan akun)
        val prefs = getSharedPreferences("users", MODE_PRIVATE)
        val registeredPassword = prefs.getString("user_pass_$email", null)
        val registeredName = prefs.getString("user_name_$email", "User")

        // 2. Ambil data dari hardcode (sebagai cadangan jika ingin tetap bisa pakai admin@nexus.com dll)
        val hardcodedUser = validUsers[email]

        // 3. Cek kecocokan password
        var isLoginValid = false
        var userNameToPass = ""

        if (registeredPassword != null) {
            // Jika akun ditemukan di hasil registrasi
            if (registeredPassword == password) {
                isLoginValid = true
                userNameToPass = registeredName ?: "User"
            }
        } else if (hardcodedUser != null) {
            // Jika tidak ada di registrasi, tapi ada di data hardcode (validUsers)
            if (hardcodedUser.first == password) {
                isLoginValid = true
                userNameToPass = hardcodedUser.second
            }
        }

        // 4. Eksekusi Login
        if (isLoginValid) {
            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(btnLogin, "scaleX", 1f, 0.96f, 1f),
                    ObjectAnimator.ofFloat(btnLogin, "scaleY", 1f, 0.96f, 1f)
                )
                duration = 300
                interpolator = OvershootInterpolator()
                doOnEnd {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra("USER_EMAIL", email)
                    intent.putExtra("USER_NAME",  userNameToPass)
                    startActivity(intent)
                    finish()
                }
                start()
            }
        } else {
            shakeView(etEmail)
            shakeView(etPassword)
            showEmailError("Email atau password salah")
            showPasswordError("Periksa kembali password kamu")
            Toast.makeText(this, "❌ Email atau password salah!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupInputFocusAnimations() {
        etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && etEmail.text.toString().isEmpty()) {
                setInputBorder(etEmail, false)
            }
        }
        etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && etPassword.text.toString().isEmpty()) {
                setInputBorder(etPassword, false)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var valid = true
        if (email.isEmpty()) {
            shakeView(etEmail); showEmailError("Email tidak boleh kosong"); valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            shakeView(etEmail); showEmailError("Format email tidak valid"); valid = false
        }
        if (password.isEmpty()) {
            shakeView(etPassword); showPasswordError("Password tidak boleh kosong"); valid = false
        } else if (password.length < 6) {
            shakeView(etPassword); showPasswordError("Password minimal 6 karakter"); valid = false
        }
        return valid
    }

    private fun shakeView(view: View) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 14f, -14f, 10f, -10f, 6f, -6f, 0f).apply {
            duration = 400; interpolator = AccelerateDecelerateInterpolator(); start()
        }
    }

    private fun animateSocialButton(view: View) {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.94f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.94f, 1f)
            )
            duration = 250; interpolator = OvershootInterpolator(); start()
        }
    }

    private fun animateToggleButton(view: View) {
        ObjectAnimator.ofFloat(view, "rotation", 0f, 180f).apply {
            duration = 300; interpolator = AccelerateDecelerateInterpolator(); start()
        }
    }

    private fun startBlobAnimations() {
        val b1Y = ValueAnimator.ofFloat(0f, -30f, 0f).apply { duration = 5500; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; interpolator = AccelerateDecelerateInterpolator(); addUpdateListener { blob1.translationY = it.animatedValue as Float } }
        val b1X = ValueAnimator.ofFloat(0f, 18f,  0f).apply { duration = 7000; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; interpolator = AccelerateDecelerateInterpolator(); addUpdateListener { blob1.translationX = it.animatedValue as Float } }
        val b2Y = ValueAnimator.ofFloat(0f, 24f,  0f).apply { duration = 6200; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; interpolator = AccelerateDecelerateInterpolator(); addUpdateListener { blob2.translationY = it.animatedValue as Float } }
        val b2X = ValueAnimator.ofFloat(0f, -20f, 0f).apply { duration = 8000; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; interpolator = AccelerateDecelerateInterpolator(); addUpdateListener { blob2.translationX = it.animatedValue as Float } }
        val b3Y = ValueAnimator.ofFloat(0f, -18f, 16f, 0f).apply { duration = 9000; repeatCount = ValueAnimator.INFINITE; repeatMode = ValueAnimator.REVERSE; interpolator = AccelerateDecelerateInterpolator(); addUpdateListener { blob3.translationY = it.animatedValue as Float } }
        blobAnimators.addAll(listOf(b1Y, b1X, b2Y, b2X, b3Y))
        blobAnimators.forEach { it.start() }
    }

    private fun playEntranceAnimation() {
        brandSection.translationY = -60f
        loginCard.translationY    = 80f
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(brandSection, "alpha", 0f, 1f).apply { duration = 700; startDelay = 200; interpolator = DecelerateInterpolator(1.5f) },
                ObjectAnimator.ofFloat(brandSection, "translationY", -60f, 0f).apply { duration = 700; startDelay = 200; interpolator = DecelerateInterpolator(1.5f) },
                ObjectAnimator.ofFloat(loginCard, "alpha", 0f, 1f).apply { duration = 800; startDelay = 450; interpolator = DecelerateInterpolator(2f) },
                ObjectAnimator.ofFloat(loginCard, "translationY", 80f, 0f).apply { duration = 800; startDelay = 450; interpolator = DecelerateInterpolator(2f) },
                ObjectAnimator.ofFloat(signUpSection, "alpha", 0f, 1f).apply { duration = 600; startDelay = 900; interpolator = DecelerateInterpolator() }
            )
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        blobAnimators.forEach { it.cancel() }
        blobAnimators.clear()
    }
}