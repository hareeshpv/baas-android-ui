package com.payu.baas.coreUI.view.viewExtension

import android.content.Context
import android.graphics.Typeface
import java.lang.Exception
import java.util.*

object FontCache {
    private val fontCache: Hashtable<String, Typeface?> = Hashtable()
    fun getTypeface(context: Context, fontPath: String): Typeface? {
        var typeface = fontCache[fontPath]
        if (typeface == null) {
            typeface = try {
                Typeface.createFromAsset(context.assets, fontPath)
            } catch (e: Exception) {
                return null
            }
            fontCache[fontPath] = typeface
        }
        return typeface
    }
}