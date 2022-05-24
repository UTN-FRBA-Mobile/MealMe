package com.android.mealme.utils

import java.math.RoundingMode
import java.text.DecimalFormat

class numberUtils {
    companion object {
        fun roundOffDecimal(number: Double): Double? {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.CEILING
            return df.format(number).toDouble()
        }
    }
}