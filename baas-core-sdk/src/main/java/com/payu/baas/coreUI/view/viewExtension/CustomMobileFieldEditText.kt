package com.payu.baas.coreUI.view.viewExtension

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.payu.baas.coreUI.R
import java.lang.reflect.Field

@SuppressLint("AppCompatCustomView")
class CustomMobileFieldEditText  : EditText {
    private var onEditorActionListener: OnEditorActionListener? = null

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setFontAndBgColor()
        setCustomFont(context, attrs)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontAndBgColor()
        setCustomFont(context, attrs)
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
        FontCache.
        getTypeface(context!!, fontPath!!)
        setTypeface(typeface)
        return true
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
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
//        this.setGravity(Gravity.CENTER)
//        this.setTextSize(getResources().getDimension(R.dimen.button_text_size));
        this.setBackground(getResources().getDrawable(R.drawable.mobile_background,resources.newTheme()))
        this.highlightColor = ContextCompat.getColor(
            this.context, R.color.highlight_text_color
        )
        //this.setTextCursorDrawable(getResources().getDrawable(R.drawable.bg_cursor,null))
    }
    override fun setOnEditorActionListener(l: OnEditorActionListener?) {
        onEditorActionListener = l
        super.setOnEditorActionListener(l)
    }

    fun getOnEditorActionListener(): OnEditorActionListener? {
        return onEditorActionListener
    }
}