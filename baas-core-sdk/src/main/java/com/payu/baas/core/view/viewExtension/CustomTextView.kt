package com.payu.baas.core.view.viewExtension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.payu.baas.core.R


@SuppressLint("AppCompatCustomView")
class CustomTextView : TextView {
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

    private fun setCustomFont(context: Context?, fontPath: String?): Boolean {
        val typeface: Typeface? = FontCache.getTypeface(context!!,
            fontPath!!
        )
        setTypeface(typeface)
        return true
    }
    private fun setFontAndBgColor() {
//        this.setTextColor(resources.getColor(R.color.grey))
//        this.setGravity(Gravity.CENTER)
//        this.setTextSize(getResources().getDimension(R.dimen.button_text_size));
//        this.setBackground(null)
    }
}