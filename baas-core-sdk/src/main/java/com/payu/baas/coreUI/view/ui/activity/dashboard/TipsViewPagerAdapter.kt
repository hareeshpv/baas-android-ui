package com.payu.baas.coreUI.view.ui.activity.dashboard

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class TipsViewPagerAdapter : PagerAdapter() {
    abstract fun getItem(position: Int): View
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mItemView = getItem(position)
        container.addView(mItemView)
        return mItemView
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}