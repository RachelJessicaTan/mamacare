package com.example.mamacare.ui

import android.graphics.Color
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout

/** Edge-to-edge dengan ikon status bar & navigation bar gelap, karena semua layar berlatar terang. */
fun ComponentActivity.enableLightEdgeToEdge() {
    enableEdgeToEdge(
        statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
    )
}

/**
 * Menambah padding sebesar status bar, navigation bar, dan keyboard supaya konten
 * tidak tertutup saat edge-to-edge aktif.
 */
fun View.applySystemBarsPadding(top: Boolean = true, bottom: Boolean = true) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
        view.setPadding(
            initialLeft + bars.left,
            initialTop + if (top) bars.top else 0,
            initialRight + bars.right,
            initialBottom + if (bottom) bars.bottom else 0,
        )
        insets
    }
}

val TextInputLayout.textValue: String
    get() = editText?.text?.toString()?.trim().orEmpty()

/** Menampilkan pesan error, atau menyembunyikannya (beserta ruangnya) jika [message] null. */
fun TextInputLayout.showError(message: String?) {
    error = message
    isErrorEnabled = message != null
}

fun TextInputLayout.clearErrorOnInput() {
    editText?.doAfterTextChanged { if (error != null) showError(null) }
}
