package com.payu.baas.coreUI.view.ui.activity.passbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ItemPassbookFilterTypeBinding
import com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource
import com.payu.baas.coreUI.model.entities.model.TypeModel
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.UtilsUI

class PassBookFilterTypeSelectionAdapter internal constructor(
    private val mList: ArrayList<TypeModel>
) :
    RecyclerView.Adapter<PassBookFilterTypeSelectionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemPassbookFilterTypeBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_passbook_filter_type,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.viewUnSelected.background = UtilsUI.drawRound(
            SessionManagerUI.getInstance(holder.binding.tvType.context).accentColor,
            ContextCompat.getColor(holder.binding.tvType.context, android.R.color.transparent),
            2
        )

        holder.binding.viewSelected.background = UtilsUI.drawRound(
            SessionManagerUI.getInstance(holder.binding.tvType.context).accentColor,
            SessionManagerUI.getInstance(holder.binding.tvType.context).accentColor,
            0
        )

        UtilsUI.applyColorToText(
            holder.binding.tvType,
            SessionManagerUI.getInstance(holder.binding.tvType.context).accentColor
        )

        holder.binding.model = mList[holder.layoutPosition]

        holder.itemView.setOnClickListener {
            if (!mList[holder.layoutPosition].selected) {
                for (model in mList) {
                    model.selected = false
                }
                mList[holder.layoutPosition].selected = true
                notifyDataSetChanged()
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: ItemPassbookFilterTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun getList(): ArrayList<TypeModel> {
        return mList
    }

    fun addList(itemList: ArrayList<TypeModel>) {
        mList.clear()
        mList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        if (!mList.isNullOrEmpty()) {
            mList.clear()
            notifyDataSetChanged()
        }
    }

    fun markCustomOptionSelected() {
        for (model in mList) {
            model.selected = model.code == com.payu.baas.coreUI.model.datasource.PassBookFilterDataSource.CUSTOM_TIME_PERIOD
        }
        notifyDataSetChanged()
    }
}