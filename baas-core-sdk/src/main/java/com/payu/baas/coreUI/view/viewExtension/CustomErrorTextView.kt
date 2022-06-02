package com.payu.baas.coreUI.view.viewExtension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.text.TextPaint
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView

import androidx.annotation.RequiresApi
import com.payu.baas.coreUI.R

@SuppressLint("AppCompatCustomView")
class CustomErrorTextView  : TextView {
    constructor(context: Context?) : super(context) {}
    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setFontAndBgColor()
        setCustomFont(context!!, attrs!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontAndBgColor()
        setCustomFont(context!!, attrs!!)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setFontAndBgColor()
        setCustomFont(context!!, attrs!!)
    }

    private fun setCustomFont(context: Context, attrs: AttributeSet) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PTextView)
        val fontPath: String? = array.getString(R.styleable.PTextView_custFont)
        setCustomFont(context, fontPath)
        array.recycle()
    }

    fun setCustomFont(context: Context?, fontPath: String?): Boolean {
        val typeface: Typeface? = FontCache.getTypeface(context!!,
            fontPath!!
        )
        setTypeface(typeface)
        return true
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setFontAndBgColor() {
        val paint: TextPaint = this.getPaint()
        this.setTextColor(resources.getColor(R.color.red_light,resources.newTheme()))

        val textShader: Shader = LinearGradient(
            0f, 0f, paint.measureText(this.getText().toString()),
            this.getTextSize(), intArrayOf(
                Color.parseColor("#E83F94"), Color.parseColor("#F54E5E")
            ), floatArrayOf(0f,1f), Shader.TileMode.CLAMP
        )

        paint.setShader(textShader)
        this.setGravity(Gravity.CENTER)
        this.setTextSize(12.0f);
        this.setBackground(null)
    }
}