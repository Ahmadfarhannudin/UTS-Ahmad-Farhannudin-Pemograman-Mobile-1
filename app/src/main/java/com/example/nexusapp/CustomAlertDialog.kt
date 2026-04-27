package com.example.nexusapp

import android.app.AlertDialog
import android.content.Context

object CustomAlertDialog {

    fun showSuccess(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle("Berhasil")
        builder.setMessage(message)

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}