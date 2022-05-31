package com.payu.baas.core.view.ui.activity.dashboard

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import com.payu.baas.core.R
import com.payu.baas.core.model.model.TipDetails
import com.payu.baas.core.view.callback.ItemClickListener
import kotlinx.android.synthetic.main.adapter_tips.view.*

class TipsAdapter(
    private val listener: ItemClickListener,
    private val context: Context
) : TipsViewPagerAdapter() {

    private var list: ArrayList<TipDetails> = arrayListOf()


    override fun getItem(position: Int): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.adapter_tips, null)
//        view.tvTip.movementMethod = ScrollingMovementMethod()

        view.tvTip.text = list[position].tipText
        return view
    }

    override fun getCount(): Int {
        return list.size
    }

    fun setList(itemList: ArrayList<TipDetails>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }

}