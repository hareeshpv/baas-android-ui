package com.payu.baas.coreUI.view.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.view.callback.ImageLoadingListener

class CustomDataBinding {

    companion object {
        @JvmStatic
        @BindingAdapter(
            value = ["icon_name","error_image"],
            requireAll = false
        )
        fun loadImage(
            imageView: ImageView, url: String?, errorImage : Int?
        ) {
            UtilsUI.loadUrl(imageView, url,errorImage,object : ImageLoadingListener {
                override fun loadingFailed() {

                }
            })
        }
    }
}