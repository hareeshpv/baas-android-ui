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
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.View.TEXT_ALIGNMENT_GRAVITY
import android.widget.Button
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.view.viewExtension.FontCache.getTypeface


@SuppressLint("AppCompatCustomView")
class CustomGradientGreenButton : Button {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setFontAndBgColor()
        setCustomFont(context!!, attrs!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontAndBgColor()
        //        setCustomFont(context, attrs);
    }

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

    private fun setFontAndBgColor() {
        val paint: TextPaint = this.getPaint()
        this.setTextColor(resources.getColor(R.color.green_light))
        val textShader: Shader = LinearGradient(
            0f, 0f, this.getPaint().measureText(this.getText().toString()),
            this.getTextSize(), intArrayOf(
                Color.parseColor("#B2CC25"), Color.parseColor("#0FC99C")
            ), floatArrayOf(0f,1f), Shader.TileMode.CLAMP
        )

        this.getPaint().setShader(textShader)
        this.setGravity(Gravity.CENTER)
//        this.setTypeface(null,Typeface.BOLD)
        this.setBackground(resources.getDrawable(R.drawable.button_black_bg,null))
    }

    private fun setCustomFont(context: Context, attrs: AttributeSet) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PTextView)
        val fontPath: String? = array.getString(R.styleable.PTextView_custFont)
        setCustomFont(context, fontPath)
        array.recycle()
    }

    fun setCustomFont(context: Context?, fontPath: String?): Boolean {
        val typeface: Typeface? = getTypeface(
            context!!,
            fontPath!!
        )
        setTypeface(typeface)
        return true
    }
}