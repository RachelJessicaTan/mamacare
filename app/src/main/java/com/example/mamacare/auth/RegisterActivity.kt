package com.example.mamacare.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.mamacare.MainActivity
import com.example.mamacare.R
import com.example.mamacare.ui.applySystemBarsPadding
import com.example.mamacare.ui.clearErrorOnInput
import com.example.mamacare.ui.enableLightEdgeToEdge
import com.example.mamacare.ui.showError
import com.example.mamacare.ui.textValue
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var termsCheckbox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContentView(R.layout.activity_register)
        findViewById<View>(R.id.scroll).applySystemBarsPadding(top = false)

        nameLayout = findViewById(R.id.name_layout)
        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.password_layout)
        termsCheckbox = findViewById(R.id.terms_checkbox)
        listOf(nameLayout, emailLayout, passwordLayout).forEach { it.clearErrorOnInput() }

        findViewById<View>(R.id.register_button).setOnClickListener { attemptRegister() }
        findViewById<View>(R.id.google_button).setOnClickListener {
            Snackbar.make(it, R.string.coming_soon, Snackbar.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.login_link).setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val name = nameLayout.textValue
        val email = emailLayout.textValue
        val password = passwordLayout.textValue

        nameLayout.showError(if (name.isEmpty()) getString(R.string.error_name_empty) else null)
        emailLayout.showError(
            when {
                email.isEmpty() -> getString(R.string.error_email_empty)
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalid)
                else -> null
            }
        )
        passwordLayout.showError(
            when {
                password.isEmpty() -> getString(R.string.error_password_empty)
                password.length < MIN_PASSWORD_LENGTH -> getString(R.string.error_password_short)
                else -> null
            }
        )

        val firstInvalid = listOf(nameLayout, emailLayout, passwordLayout).firstOrNull { it.error != null }
        if (firstInvalid != null) {
            firstInvalid.editText?.requestFocus()
            return
        }
        if (!termsCheckbox.isChecked) {
            Snackbar.make(termsCheckbox, R.string.error_terms, Snackbar.LENGTH_SHORT).show()
            return
        }

        // TODO: kirim data pendaftaran ke backend. Untuk sementara langsung masuk ke beranda.
        startActivity(
            MainActivity.intent(this, name = name.substringBefore(' '))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
