package com.payu.baas.coreUI.view.ui.snackbaar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.android.material.snackbar.ContentViewCallback
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.view.callback.SnackBarListener

class SimpleCustomSnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), ContentViewCallback {
    lateinit var tvMsg: TextView
    lateinit var imLeft: ImageView
    lateinit var layRoot: RelativeLayout
    lateinit var tic_black_background_icon: ImageView
    lateinit var tvUndo: TextView
    lateinit var tvReTry: TextView
    var listener: SnackBarListener? = null

    init {
        View.inflate(context, R.layout.custom_snackbar_view, this)
        clipToPadding = false
        this.tvMsg = findViewById(R.id.snack_title_message)
        this.imLeft = findViewById(R.id.snack_icon)
        this.layRoot = findViewById(R.id.snack_relative)
        this.tic_black_background_icon = findViewById(R.id.tic_black_background_icon)
        this.tvUndo = findViewById(R.id.tvUndo)
        this.tvReTry = findViewById(R.id.tvReTry)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(500)
        }
        animatorSet.start()


    }

    override fun animateContentOut(delay: Int, duration: Int) {

    }
}