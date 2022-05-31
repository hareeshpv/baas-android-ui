package com.payu.baas.core.view.ui.activity.passbook

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ItemPassbookTransactionsBinding
import com.payu.baas.core.model.entities.model.GetPassBookTransactionDetailHeader
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails
import com.payu.baas.core.model.storage.SessionManagerUI
import com.payu.baas.core.util.UtilsUI

class PassBookTransactionsAdapter internal constructor(
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<PassBookTransactionsAdapter.ViewHolder>() {

    private var mList: ArrayList<GetPassBookTransactionDetailHeader> = arrayListOf()

    //create an interface for onClickListener
    // so that we can handle data most effectively in HomeFragment.kt
    interface ItemClickListener {
        fun onItemClicked(model: GetPassBookTransactionDetails)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemPassbookTransactionsBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_passbook_transactions,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        UtilsUI.applyColorToText(
            holder.binding.tvDate,
            SessionManagerUI.getInstance(holder.binding.tvDate.context).accentColor
        )
        holder.binding.tvDate.text = mList[position].date

        holder.binding.rvTransactionDetail.setHasFixedSize(true)
        val mAdapter =
            PassBookTransactionDetailsAdapter(mList[position].transactionDetails, listener)
        holder.binding.rvTransactionDetail.adapter = mAdapter
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: ItemPassbookTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun addList(itemList: ArrayList<GetPassBookTransactionDetailHeader>) {
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

    fun getList(): ArrayList<GetPassBookTransactionDetailHeader> {
        return mList
    }
}