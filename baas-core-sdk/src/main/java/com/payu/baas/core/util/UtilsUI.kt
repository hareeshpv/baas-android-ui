package com.payu.baas.core.util

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.widget.CompoundButtonCompat
import com.bumptech.glide.request.RequestOptions
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.core.R
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.core.view.callback.ImageLoadingListener
import com.payu.baas.core.view.ui.snackbaar.SimpleCustomSnackbar
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


object UtilsUI {

    internal fun init(context: Context, isProduction: Boolean? = true) {
    }

    fun convertAmountFormat(amount: Double): String {
        return DecimalFormat("#,###.##").format(amount)
    }

    fun getShortName(name: String?): String {
        var shortName = ""
        if (name != null) {
            name.trim()
            if (name.isNotEmpty()) {
                if (name.contains(" ")) {
                    val split = name.split(" ")
                    shortName = if (split.size > 1) {
                        (split[0].trim().substring(0, 1)
                            .plus(split[1].trim().substring(0, 1))).uppercase()
                    } else {
                        if (split[0].length > 1) {
                            split[0].trim().substring(0, 2).uppercase()
                        } else {
                            split[0].trim().uppercase()
                        }
                    }
                } else {
                    shortName = if (name.length > 1) {
                        name.trim().substring(0, 2).uppercase()
                    } else {
                        name.uppercase()
                    }
                }
            } else {
                shortName = "NA"
            }
        }
        return shortName
    }

    fun getScreenHeight(activity: Activity): Int {
        val outMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = activity.display
            display?.getRealMetrics(outMetrics)
            outMetrics.heightPixels
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)

            outMetrics.heightPixels
        }
    }

    fun getScreenWidth(activity: Activity): Int {
        val outMetrics = DisplayMetrics()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = activity.display
            display?.getRealMetrics(outMetrics)
            outMetrics.widthPixels
        } else {
            @Suppress("DEPRECATION")
            val display = activity.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)

            outMetrics.widthPixels
        }
    }

    //PopupWindow display method
    fun showPopupWindow(activity: Activity, anchor: View, message: String) {

        PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(anchor.context)
            contentView = inflater.inflate(R.layout.layout_pop_up, null).apply {
                findViewById<TextView>(R.id.tvTooltipText).text = message
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
        }.also { popupWindow ->
            popupWindow.setBackgroundDrawable(null)
            // Absolute location of the anchor view
            val location = IntArray(2).apply {
                anchor.getLocationOnScreen(this)
            }
            val size = Size(
                popupWindow.contentView.measuredWidth,
                popupWindow.contentView.measuredHeight
            )
            popupWindow.showAtLocation(
                anchor,
                Gravity.TOP or Gravity.START,
                location[0] - (size.width - anchor.width) / 2,
                location[1] - size.height
            )
        }
    }

    fun showPopupWindow(anchor: View) {
        PopupWindow(anchor.context).apply {
            isOutsideTouchable = true
            val inflater = LayoutInflater.from(anchor.context)
            contentView = inflater.inflate(R.layout.layout_pop_up, null).apply {
                measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
            }
        }.also { popupWindow ->
            popupWindow.setBackgroundDrawable(null)
            // Absolute location of the anchor view
            val location = IntArray(2).apply {
                anchor.getLocationOnScreen(this)
            }
            val size = Size(
                popupWindow.contentView.measuredWidth,
                popupWindow.contentView.measuredHeight
            )
            popupWindow.showAtLocation(
                anchor,
                Gravity.TOP or Gravity.START,
                location[0] - (size.width - anchor.width) / 2,
                location[1] - size.height
            )
        }
    }

    // extension function to convert dp to equivalent pixels
    fun dpToPixels(context: Context, margin: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, margin.toFloat(), context.resources.displayMetrics
        ).toInt()
    }

    fun loadUrl(
        imageView: ImageView, imageName: String?, errorImage: Int?,
        imageLoadingListener: ImageLoadingListener
    ) {

        imageName?.let {
            if (it.contains(".svg")
                || it.contains(".SVG")
            ) {
                //imageView.scaleType = ImageView.ScaleType.CENTER
                GlideToVectorYou
                    .init()
                    .with(imageView.context)
                    .withListener(object : GlideToVectorYouListener {
                        override fun onLoadFailed() {
                            imageLoadingListener.loadingFailed()
                        }

                        override fun onResourceReady() {
                        }

                    })
                    //.setPlaceHolder(R.drawable.ic_auth_selfie, R.drawable.delivery_boy)
                    .load(
                        Uri.parse(SessionManager.getInstance(imageView.context).s3BucketUrl + imageName),
                        imageView
                    ).apply { RequestOptions() }
            } else {
                imageView.setImageBitmap(null)
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                var image: Bitmap? = null
                executor.execute {
                    val imageURL =
                        SessionManager.getInstance(imageView.context).s3BucketUrl + imageName
                    try {
                        val in_ = java.net.URL(imageURL).openStream()
                        image = BitmapFactory.decodeStream(in_)
                        handler.post {
                            imageView.setImageBitmap(image)
                        }
                    } catch (e: Exception) {
                        //default.png
                        imageLoadingListener.loadingFailed()
                    }
                }

            }
        }
    }

    fun applyGradientThemeColor(textView: TextView) {
        val paint: TextPaint = textView.paint
        val width: Float = paint.measureText(textView.text.toString())

        val textShader: Shader = LinearGradient(
            0f, 0f, width, textView.textSize, intArrayOf(
                SessionManagerUI.getInstance(textView.context).primaryColor,
                SessionManagerUI.getInstance(textView.context).primaryDarkColor
            ), null, Shader.TileMode.CLAMP
        )
        textView.paint.shader = textShader
    }

    fun applyColorToCheckBox(
        cb: CheckBox?
    ) {
        cb?.let {
            if (Build.VERSION.SDK_INT < 21) {
                CompoundButtonCompat.setButtonTintList(
                    it,
                    ColorStateList.valueOf(SessionManagerUI.getInstance(cb.context).accentColor)
                )//Use android.support.v4.widget.CompoundButtonCompat when necessary else
            } else {
                it.buttonTintList =
                    ColorStateList.valueOf(SessionManagerUI.getInstance(cb.context).accentColor)//setButtonTintList is accessible directly on API>19
            }
        }
    }


    fun gradientRound(layout: LinearLayout) {
        val g = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                SessionManagerUI.getInstance(layout.context).primaryColor,  //#fff this is the start color of gradient
                SessionManagerUI.getInstance(layout.context).primaryDarkColor
            )
        ) //#97712F this is the end color of gradient

        g.gradientType = GradientDrawable.LINEAR_GRADIENT // making it circular gradient

        g.cornerRadius = 100f // radius of the circle

        layout.background = g
    }

    //Apply color which we got from Server
    fun applyColorToIcon(
        view: ImageView?,
        color: Int
    ) {
        view?.setColorFilter(
            color
        )
    }

    fun applyColorToText(
        view: View?,
        color: Int
    ) {
        view?.let {
            when (it) {
                is TextView -> {
                    it.setTextColor(color)
                }
                is EditText -> {
                    it.setTextColor(color)
                }
                is Button -> {
                    it.setTextColor(color)
                }
            }
        }
    }

    fun applyColorAsHint(
        view: View?,
        color: Int
    ) {
        view.let {
            if (it is EditText) {
                it.setHintTextColor(color)
            }
        }
    }


    fun drawRound(
        borderColor: Int,
        solidColor: Int,
        stroke: Int,
    ): GradientDrawable? {
        return drawDrawable(
            GradientDrawable.OVAL,
            solidColor,
            borderColor,
            stroke,
            50f
        )
    }

    fun drawDrawable(
        drawableShape: Int,
        solidColor: Int,
        borderColor: Int,
        stroke: Int,
        cornerRadi: Float,
        opacity: Int = -1
    ): GradientDrawable? {
        val shape = GradientDrawable()
        shape.shape = drawableShape
        shape.setColor(solidColor)
        shape.cornerRadius = dpToPx(cornerRadi)
        shape.setStroke(dpToPx(stroke), borderColor)
        return shape
    }

    fun dpToPx(dp: Float): Float {
        return (dp * Resources.getSystem().displayMetrics.density)
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun updateDateFormat(dateInString: String): String {
        var newDate = dateInString
        val formatter = SimpleDateFormat(BaaSConstantsUI.ddMMyyyy, Locale.getDefault())
        try {
            val date = formatter.parse(dateInString)
            var spf = SimpleDateFormat(BaaSConstantsUI.yyyyMMdd, Locale.getDefault())
            newDate = spf.format(date);
        } catch (e: ParseException) {
        }
        return newDate
    }

    fun createLink(
        targetTextView: TextView, completeString: String,
        partToClick: String, clickableAction: ClickableSpan?
    ): TextView? {
        val spannableString = SpannableString(completeString)

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        val startPosition = completeString.indexOf(partToClick)
        val endPosition = completeString.lastIndexOf(partToClick) + partToClick.length
        spannableString.setSpan(
            clickableAction, startPosition, endPosition,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        targetTextView.text = spannableString
        targetTextView.movementMethod = LinkMovementMethod.getInstance()
        return targetTextView
    }

    fun camelCase(inputString: String): String {
        var result = ""
        if (inputString.isEmpty()) {
            return result
        }
        val firstChar: Char = inputString[0]
        val firstCharToUpperCase = Character.toUpperCase(firstChar)
        result += firstCharToUpperCase
        for (i in 1..inputString.length) {
            val currentChar: Char = inputString[i]
            val previousChar: Char = inputString[i - 1]

            result = if (previousChar == ' ') {
                val currentCharToUpperCase = Character.toUpperCase(currentChar)
                result + currentCharToUpperCase
            } else {
                val currentCharToLowerCase = Character.toLowerCase(currentChar)
                result + currentCharToLowerCase
            }
        }
        return result

    }

    fun toCamelCase(init: String?): String? {
        if (init == null) return null
        val ret = StringBuilder(init.length)
        for (word in init.split(" ".toRegex()).toTypedArray()) {
            if (word.isNotEmpty()) {
                ret.append(Character.toUpperCase(word[0]))
                ret.append(word.substring(1).lowercase())
            }
            if (ret.length != init.length) ret.append(" ")
        }
        return ret.toString()
    }


    fun showSnackbarOnSwitchAction(view: View, message: String, isSuccess: Boolean) {
        SimpleCustomSnackbar.showSwitchMsg.make(
            view,
            message,
            Snackbar.LENGTH_LONG,
            isSuccess
        )?.show()
    }

    fun setGradientColorToLinks(ds: TextPaint, text: String) {
        val textShader: Shader = LinearGradient(
            0f, 0f, ds.measureText(text),
            text.length.toFloat(), intArrayOf(
                Color.parseColor("#B2CC25"), Color.parseColor("#0FC99C")
            ), floatArrayOf(0f, 1f), Shader.TileMode.CLAMP
        )

        ds.setShader(textShader)
    }

}