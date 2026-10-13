package com.example.mamacare.model

/** Posisi kehamilan dalam satuan minggu (1–40). */
data class PregnancyProgress(val week: Int) {

    val trimester: Int
        get() = when {
            week <= 13 -> 1
            week <= 27 -> 2
            else -> 3
        }

    val weeksToGo: Int
        get() = (TOTAL_WEEKS - week).coerceAtLeast(0)

    companion object {
        const val TOTAL_WEEKS = 40
    }
}
