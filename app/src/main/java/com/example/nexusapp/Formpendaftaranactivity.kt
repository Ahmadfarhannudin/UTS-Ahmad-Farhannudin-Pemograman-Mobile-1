package com.example.nexusapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class FormPendaftaranActivity : AppCompatActivity() {

    private lateinit var tilNama: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilHp: TextInputLayout
    private lateinit var etNama: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etHp: TextInputEditText
    private lateinit var rgJenisKelamin: RadioGroup
    private lateinit var rbLaki: RadioButton
    private lateinit var rbPerempuan: RadioButton
    private lateinit var spinnerSeminar: Spinner
    private lateinit var cbSetuju: CheckBox
    private lateinit var btnSubmit: MaterialButton

    private var userName  = "User"
    private var userEmail = ""

    private val daftarSeminar = listOf(
        "-- Pilih Seminar --",
        "Seminar AI & Machine Learning",
        "Seminar Cybersecurity 2025",
        "Seminar Mobile Development",
        "Seminar Data Science & Analytics",
        "Seminar Cloud Computing",
        "Seminar UI/UX Design Modern",
        "Seminar Blockchain & Web3"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_pendaftaran)
        supportActionBar?.hide()

        // Simpan data user login
        userName  = intent.getStringExtra("USER_NAME")  ?: "User"
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        initViews()
        setupSpinner()
        setupRealTimeValidation()

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Pre-fill dari data login
        if (userEmail.isNotEmpty()) etEmail.setText(userEmail)
        if (userName.isNotEmpty())  etNama.setText(userName.replaceFirstChar { it.uppercase() })

        btnSubmit.setOnClickListener {
            if (validateAll()) showConfirmDialog()
        }
    }

    private fun initViews() {
        tilNama        = findViewById(R.id.tilNama)
        tilEmail       = findViewById(R.id.tilEmail)
        tilHp          = findViewById(R.id.tilHp)
        etNama         = findViewById(R.id.etNama)
        etEmail        = findViewById(R.id.etEmail)
        etHp           = findViewById(R.id.etHp)
        rgJenisKelamin = findViewById(R.id.rgJenisKelamin)
        rbLaki         = findViewById(R.id.rbLaki)
        rbPerempuan    = findViewById(R.id.rbPerempuan)
        spinnerSeminar = findViewById(R.id.spinnerSeminar)
        cbSetuju       = findViewById(R.id.cbSetuju)
        btnSubmit      = findViewById(R.id.btnSubmit)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, daftarSeminar)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerSeminar.adapter = adapter
    }

    private fun setupRealTimeValidation() {
        etNama.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tilNama.error = if (s.toString().trim().isEmpty()) "Nama wajib diisi" else null
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                tilEmail.error = when {
                    email.isEmpty()      -> "Email wajib diisi"
                    !email.contains("@") -> "Email harus mengandung @"
                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
                    else -> null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etHp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hp = s.toString().trim()
                tilHp.error = when {
                    hp.isEmpty()              -> "Nomor HP wajib diisi"
                    !hp.all { it.isDigit() } -> "Hanya boleh angka"
                    !hp.startsWith("08")      -> "Harus diawali dengan 08"
                    hp.length < 10 || hp.length > 13 -> "Panjang 10-13 digit"
                    else -> null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validateAll(): Boolean {
        var isValid = true

        val nama = etNama.text.toString().trim()
        if (nama.isEmpty()) { tilNama.error = "Nama wajib diisi"; isValid = false }
        else tilNama.error = null

        val email = etEmail.text.toString().trim()
        when {
            email.isEmpty()      -> { tilEmail.error = "Email wajib diisi"; isValid = false }
            !email.contains("@") -> { tilEmail.error = "Email harus mengandung @"; isValid = false }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> { tilEmail.error = "Format email tidak valid"; isValid = false }
            else -> tilEmail.error = null
        }

        val hp = etHp.text.toString().trim()
        when {
            hp.isEmpty()              -> { tilHp.error = "Nomor HP wajib diisi"; isValid = false }
            !hp.all { it.isDigit() } -> { tilHp.error = "Hanya boleh angka"; isValid = false }
            !hp.startsWith("08")      -> { tilHp.error = "Harus diawali dengan 08"; isValid = false }
            hp.length < 10 || hp.length > 13 -> { tilHp.error = "Panjang 10-13 digit"; isValid = false }
            else -> tilHp.error = null
        }

        if (rgJenisKelamin.checkedRadioButtonId == -1) {
            Toast.makeText(this, "⚠️ Pilih jenis kelamin!", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (spinnerSeminar.selectedItemPosition == 0) {
            Toast.makeText(this, "⚠️ Pilih seminar terlebih dahulu!", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (!cbSetuju.isChecked) {
            Toast.makeText(this, "⚠️ Centang persetujuan terlebih dahulu!", Toast.LENGTH_LONG).show()
            isValid = false
        }

        return isValid
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setTitle("Konfirmasi Data")
            .setMessage("Apakah data yang Anda isi sudah benar?\n\nNama  : ${etNama.text}\nEmail  : ${etEmail.text}\nHP       : ${etHp.text}")
            .setPositiveButton("Ya, Lanjutkan") { _, _ -> goToHasil() }
            .setNegativeButton("Tidak, Periksa Lagi") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun goToHasil() {
        val jenisKelamin = if (rbLaki.isChecked) "Laki-laki" else "Perempuan"
        Intent(this, HasilPendaftaranActivity::class.java).apply {
            putExtra("NAMA",          etNama.text.toString())
            putExtra("EMAIL",         etEmail.text.toString())
            putExtra("HP",            etHp.text.toString())
            putExtra("JENIS_KELAMIN", jenisKelamin)
            putExtra("SEMINAR",       spinnerSeminar.selectedItem.toString())
            // ← Kirim data user login agar halaman hasil bisa kembali dengan benar
            putExtra("USER_NAME",  userName)
            putExtra("USER_EMAIL", userEmail)
            startActivity(this)
        }
    }
}
