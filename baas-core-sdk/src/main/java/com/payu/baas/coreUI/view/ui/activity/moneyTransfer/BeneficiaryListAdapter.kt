package com.payu.baas.coreUI.view.ui.activity.moneyTransfer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ItemBeneficiaryBinding
import com.payu.baas.core.model.model.BeneficiaryModel
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.view.callback.ImageLoadingListener

class BeneficiaryListAdapter internal constructor(
    private val listener: ItemClickListener,
    private var showAllPayee: Boolean = false
) :
    RecyclerView.Adapter<BeneficiaryListAdapter.ViewHolder>() {

    val list: ArrayList<BeneficiaryModel> = arrayListOf()
    var allowSelection: Boolean = false


    //create an interface for onClickListener
    // so that we can handle data most effectively in HomeFragment.kt
    interface ItemClickListener {
        fun onItemClicked(model: BeneficiaryModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemBeneficiaryBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_beneficiary,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClicked(list[holder.layoutPosition])
        }

        UtilsUI.applyColorToCheckBox(holder.binding.chk)
        UtilsUI.applyColorToText(
            holder.binding.tvShortName,
            SessionManagerUI.getInstance(holder.binding.tvShortName.context).accentColor
        )
        UtilsUI.applyColorToText(
            holder.binding.tvBeneficiaryName,
            SessionManagerUI.getInstance(holder.binding.tvBeneficiaryName.context).accentColor
        )

        holder.binding.tvShortName.text = UtilsUI.getShortName(list[position].beneficiaryName)
        holder.binding.tvBeneficiaryName.text =
            UtilsUI.toCamelCase(list[holder.layoutPosition].beneficiaryName)
        holder.binding.tvBankName.text = (list[holder.layoutPosition].bankName ?: "")
            .plus(" A/c XX ")
            .plus(list[holder.layoutPosition].last4Digits)
        if (!list[holder.layoutPosition].icon.isNullOrEmpty())
            UtilsUI.loadUrl(holder.binding.ivBankIcon, list[holder.layoutPosition].icon, null,
                object : ImageLoadingListener {
                    override fun loadingFailed() {

                    }

                })

        holder.binding.chk.setOnCheckedChangeListener(null)
        holder.binding.chk.isChecked = list[holder.layoutPosition].selected

        if (allowSelection) {
            holder.binding.chk.visibility = View.VISIBLE

            holder.binding.chk.setOnCheckedChangeListener { compoundButton, checked ->
                list[holder.layoutPosition].selected = checked
                notifyItemChanged(holder.layoutPosition)
            }
        } else
            holder.binding.chk.visibility = View.GONE

        // set text view layout margin programmatically
        com.payu.baas.coreUI.app.BaasUIApp.ctx?.let {
            if (allowSelection)
                (holder.binding.llShortName.layoutParams as LinearLayout.LayoutParams).apply {
                    marginStart = UtilsUI.dpToPixels(it, 8)
                }
            else
                (holder.binding.llShortName.layoutParams as LinearLayout.LayoutParams).apply {
                    marginStart = UtilsUI.dpToPixels(it, 0)
                }
        }

    }

    override fun getItemCount(): Int {
        return if (!showAllPayee) {
            if (list.size > 3) {
                3
            } else {
                list.size
            }
        } else
            list.size
    }

    class ViewHolder(val binding: ItemBeneficiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun enableMultipleSelection() {
        allowSelection = true
        notifyDataSetChanged()
    }

    fun disableMultipleSelection() {
        allowSelection = false
        list.forEach {
            it.selected = false
        }
        notifyDataSetChanged()
    }

    fun getAllItems(): ArrayList<BeneficiaryModel> {
        return list
    }

    fun addList(itemList: ArrayList<BeneficiaryModel>) {
        val lastItemPosition = list.size
        list.addAll(itemList)
        if (lastItemPosition > 0) {
            notifyItemRangeInserted(lastItemPosition, itemList.size)
        } else {
            notifyDataSetChanged()
        }
    }

    fun deleteItems() {
        if (list.isNotEmpty()) {
            var i = 0
            while (i < list.size) {
                if (list[i].selected) {
                    list.removeAt(i)
                    notifyItemRemoved(i)
                    notifyItemRangeChanged(i, list.size)
                    i--
                }
                i++
            }
        }
    }

    fun clearAdapter() {
        if (!list.isNullOrEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }
    }
}