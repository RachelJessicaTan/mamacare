package com.example.mamacare

import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.example.mamacare.auth.LoginActivity
import com.example.mamacare.model.PregnancyProgress
import com.example.mamacare.ui.applySystemBarsPadding
import com.example.mamacare.ui.enableLightEdgeToEdge
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var trimesterChips: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableLightEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.scroll).applySystemBarsPadding(bottom = false)
        findViewById<View>(R.id.bottom_bar).applySystemBarsPadding(top = false)

        val name = intent.getStringExtra(EXTRA_NAME) ?: getString(R.string.default_name)
        bindHeader(name)
        bindPregnancy(PregnancyProgress(SAMPLE_WEEK), animate = savedInstanceState == null)
        applyBabyDecorationEffects()
        bindNavigation()
    }

    private fun bindHeader(name: String) {
        val greeting = when (LocalTime.now().hour) {
            in 4..11 -> R.string.greeting_morning
            in 12..17 -> R.string.greeting_afternoon
            else -> R.string.greeting_evening
        }
        findViewById<TextView>(R.id.today).text = LocalDate.now().format(DATE_FORMAT).uppercase(Locale.ENGLISH)
        findViewById<TextView>(R.id.greeting).text = getString(greeting, name)
    }

    private fun bindPregnancy(progress: PregnancyProgress, animate: Boolean) {
        val trimesterName = getString(TRIMESTER_NAMES[progress.trimester - 1])
        findViewById<TextView>(R.id.week_number).text = progress.week.toString()
        findViewById<TextView>(R.id.week_title).text = getString(R.string.week_title, progress.week)
        findViewById<TextView>(R.id.week_subtitle).text =
            getString(R.string.week_subtitle, trimesterName, progress.weeksToGo)
        findViewById<CircularProgressIndicator>(R.id.week_ring).setProgressCompat(progress.week, animate)

        trimesterChips = listOf(R.id.chip_first, R.id.chip_second, R.id.chip_third).map { findViewById(it) }
        trimesterChips.forEachIndexed { index, chip -> chip.setOnClickListener { selectTrimester(index) } }
        selectTrimester(progress.trimester - 1)
    }

    private fun selectTrimester(index: Int) {
        trimesterChips.forEachIndexed { i, chip -> chip.isSelected = i == index }
    }

    /**
     * Figma memberi dekorasi kartu bayi blend "overlay" dan drop shadow (turun 4dp, blur 2, hitam 25%).
     * VectorDrawable tidak mendukung keduanya, jadi diterapkan di sini.
     */
    private fun applyBabyDecorationEffects() {
        findViewById<View>(R.id.baby_deco).setLayerType(
            View.LAYER_TYPE_HARDWARE,
            Paint().apply { blendMode = BlendMode.OVERLAY },
        )
        val blurRadius = SHADOW_BLUR_RADIUS_DP * resources.displayMetrics.density
        findViewById<View>(R.id.baby_deco_shadow).setRenderEffect(
            RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.DECAL)
        )
    }

    private fun bindNavigation() {
        listOf(
            R.id.notification_button,
            R.id.week_card,
            R.id.see_all,
            R.id.tip_body,
            R.id.tip_daily,
            R.id.nav_baby,
            R.id.nav_community,
        ).forEach { id -> findViewById<View>(id).setOnClickListener { showComingSoon() } }

        findViewById<View>(R.id.nav_home).apply {
            isSelected = true
            setOnClickListener { findViewById<NestedScrollView>(R.id.scroll).smoothScrollTo(0, 0) }
        }
        findViewById<View>(R.id.nav_profile).setOnClickListener { confirmLogout() }
    }

    private fun showComingSoon() {
        Snackbar.make(findViewById(R.id.main), R.string.coming_soon, Snackbar.LENGTH_SHORT)
            .setAnchorView(R.id.bottom_bar)
            .show()
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.logout_action) { _, _ ->
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            .show()
    }

    companion object {
        private const val EXTRA_NAME = "name"

        // Data contoh sampai backend tersedia, sama dengan desain Figma.
        private const val SAMPLE_WEEK = 18

        // Sigma blur Figma 2 setara radius ±2.6 pada RenderEffect.
        private const val SHADOW_BLUR_RADIUS_DP = 2.6f

        private val TRIMESTER_NAMES = listOf(R.string.trimester_first, R.string.trimester_second, R.string.trimester_third)
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)

        fun intent(context: Context, name: String? = null): Intent =
            Intent(context, MainActivity::class.java).apply {
                name?.let { putExtra(EXTRA_NAME, it) }
            }
    }
}
