package com.example.mamacare.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.EditorInfo
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

class LoginActivity : AppCompatActivity() {

    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContentView(R.layout.activity_login)
        // Judul sudah berada jauh di bawah status bar (sesuai Figma), jadi hanya bagian bawah yang perlu inset.
        findViewById<View>(R.id.scroll).applySystemBarsPadding(top = false)

        emailLayout = findViewById(R.id.email_layout)
        passwordLayout = findViewById(R.id.password_layout)
        emailLayout.clearErrorOnInput()
        passwordLayout.clearErrorOnInput()

        passwordLayout.editText?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                true
            } else {
                false
            }
        }
        findViewById<View>(R.id.login_button).setOnClickListener { attemptLogin() }
        findViewById<View>(R.id.forgot_password).setOnClickListener(::showComingSoon)
        findViewById<View>(R.id.google_button).setOnClickListener(::showComingSoon)
        findViewById<View>(R.id.register_link).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = emailLayout.textValue
        emailLayout.showError(
            when {
                email.isEmpty() -> getString(R.string.error_email_empty)
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> getString(R.string.error_email_invalid)
                else -> null
            }
        )
        passwordLayout.showError(
            if (passwordLayout.textValue.isEmpty()) getString(R.string.error_password_empty) else null
        )
        if (emailLayout.error != null || passwordLayout.error != null) return

        // TODO: kirim email & kata sandi ke backend. Untuk sementara langsung masuk ke beranda.
        startActivity(
            MainActivity.intent(this)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun showComingSoon(anchor: View) {
        Snackbar.make(anchor, R.string.coming_soon, Snackbar.LENGTH_SHORT).show()
    }
}
