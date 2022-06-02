package com.payu.baas.coreUI.view.viewExtension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
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
import com.payu.baas.coreUI.model.storage.SessionManagerUI

@SuppressLint("AppCompatCustomView")
class CustomGradientTextView : TextView {
    constructor(context: Context?) : super(context) {}

    @RequiresApi(Build.VERSION_CODES.M)
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
        setCustomFont(context!!, attrs!!)
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

    private fun setCustomFont(context: Context, attrs: AttributeSet) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.PTextView)
        val fontPath: String? = array.getString(R.styleable.PTextView_custFont)
        setCustomFont(context, fontPath)
        array.recycle()
    }

    fun setCustomFont(context: Context?, fontPath: String?): Boolean {
        val typeface: Typeface? = FontCache.getTypeface(
            context!!,
            fontPath!!
        )
        setTypeface(typeface)
        return true
    }

    private fun setFontAndBgColor() {
        val paint: TextPaint = this.paint
        this.setTextColor(SessionManagerUI.getInstance(this.context).primaryColor)
        val textShader: Shader = LinearGradient(
            0f, 0f, this.paint.measureText(this.text.toString()),
            this.textSize, intArrayOf(
                SessionManagerUI.getInstance(this.context).primaryColor,
                SessionManagerUI.getInstance(this.context).primaryDarkColor,
            ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )
//        var mWidth = this.getWidth()
//        var mHeight = this.getHeight()
//        val mSize = Point(mWidth, mHeight)
//        val mRandom = Random()
//        val textShader: Shader = RadialGradient(
//            mRandom.nextInt(mSize.x).toFloat(),
//            mRandom.nextInt(mSize.y).toFloat(),
//            mRandom.nextInt(mSize.x).toFloat(),
//            intArrayOf(
//                Color.parseColor("#8DCB40"), Color.parseColor("#0FC99C")
//            ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP // Shader tiling mode
//        )
        this.getPaint().setShader(textShader)
        this.setGravity(Gravity.CENTER)
    }
}