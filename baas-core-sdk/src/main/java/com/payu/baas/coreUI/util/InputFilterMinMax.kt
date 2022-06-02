package com.payu.baas.coreUI.util

import android.text.InputFilter
import android.text.Spanned

class InputFilterMinMax : InputFilter {
    private var min: Double = 0.0
    private var max: Double = 0.0

    constructor(min: Double, max: Double) {
        this.min = min
        this.max = max
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            val input = (dest.toString() + source.toString()).toDouble()
            if(input.toString().split(".")[1].length>2)
                return ""
            else if (isInRange(min, max, input))
                return null
        } catch (nfe: NumberFormatException) {
        }
        return ""
    }

    private fun isInRange(a: Double, b: Double, c: Double): Boolean {
        return (c in a..b)
    }
}