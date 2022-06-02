package com.payu.baas.coreUI.view.viewExtension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.payu.baas.coreUI.R

@SuppressLint("AppCompatCustomView")
class CustomPasscodeEditText : EditText {
    private var onEditorActionListener: TextView.OnEditorActionListener? = null

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setFontAndBgColor()
        setCustomFont(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontAndBgColor()
        setCustomFont(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        setFontAndBgColor()
        setCustomFont(context, attrs)
    }

    private fun setCustomFont(context: Context, attrs: AttributeSet) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PTextView)
        val fontPath = array.getString(R.styleable.PTextView_custFont)
        setCustomFont(context, fontPath)
        array.recycle()
    }

    fun setCustomFont(context: Context?, fontPath: String?): Boolean {
        val typeface: Typeface? = FontCache.
        getTypeface(context!!, fontPath!!)
        setTypeface(typeface)
        return true
    }
    private fun setFontAndBgColor() {
        this.setTextColor(
            ContextCompat.getColor(
                this.context, R.color.text_color
            )
        )
        this.setHintTextColor(
            ContextCompat.getColor(
                this.context, R.color.text_color
            )
        )
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.setTextCursorDrawable(getResources().getDrawable(R.color.text_color,null))
        }*/
        this.setBackground(getResources().getDrawable(R.drawable.passcode_background,resources.newTheme()))
    }
    override fun setOnEditorActionListener(l: TextView.OnEditorActionListener?) {
        onEditorActionListener = l
        super.setOnEditorActionListener(l)
    }

    fun getOnEditorActionListener(): TextView.OnEditorActionListener? {
        return onEditorActionListener
    }
}