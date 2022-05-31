package com.payu.baas.core.view.ui.activity.advance

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.core.R
import com.payu.baas.core.databinding.ItemAdvanceTransactionsBinding
import com.payu.baas.core.model.model.AdvanceUsageHistory
import com.payu.baas.core.util.UtilsUI

class AdvanceTransactionsAdapter internal constructor(
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<AdvanceTransactionsAdapter.ViewHolder>() {

    private var mList: ArrayList<AdvanceUsageHistory> = arrayListOf()

    //create an interface for onClickListener
    // so that we can handle data most effectively in HomeFragment.kt
    interface ItemClickListener {
        fun onItemClicked(model: AdvanceUsageHistory)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemAdvanceTransactionsBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_advance_transactions,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]
        holder.binding.tvDateRange.text = model.displayStartDate.plus('-').plus(model.displayEndDate)

        holder.binding.tvTotalAdvanceUsed.visibility = View.GONE
        holder.binding.tvTotalAdvanceUsedAmount.visibility = View.GONE
        model.advanceUsed?.let {
            holder.binding.tvTotalAdvanceUsed.visibility = View.VISIBLE
            holder.binding.tvTotalAdvanceUsedAmount.visibility = View.VISIBLE
            holder.binding.tvTotalAdvanceUsedAmount.text =
                holder.binding.tvTotalAdvanceUsedAmount.context
                    .getString(R.string.currency_symbol).plus(" ").plus(it)
//            holder.binding.tvTotalAdvanceUsedAmount.text=
//                holder.binding.tvTotalAdvanceUsedAmount.context.getString(R.string.currency_symbol).plus(" ").plus(UtilsUI.convertAmountFormat(it.toDouble()))
        }

        holder.binding.tvFeesCharged.visibility = View.GONE
        holder.binding.tvFeesChargedAmount.visibility = View.GONE
        model.feesCharged?.let {
            holder.binding.tvFeesCharged.visibility = View.VISIBLE
            holder.binding.tvFeesChargedAmount.visibility = View.VISIBLE
            holder.binding.tvFeesChargedAmount.text = holder.binding.tvFeesChargedAmount.context
                .getString(R.string.currency_symbol).plus(" ").plus(it)
//            holder.binding.tvFeesChargedAmount.text=
//                holder.binding.tvFeesChargedAmount.context.getString(R.string.currency_symbol).plus(" ").plus(UtilsUI.convertAmountFormat(it.toDouble()))
        }

        holder.binding.tvLatePaymentFees.visibility = View.GONE
        holder.binding.tvLatePaymentFeesAmount.visibility = View.GONE
        if (!model.latePaymentFees.isNullOrEmpty() && !model.latePaymentFees.equals("0"))
            model.latePaymentFees?.let {
                holder.binding.tvLatePaymentFees.visibility = View.VISIBLE
                holder.binding.tvLatePaymentFeesAmount.visibility = View.VISIBLE
                holder.binding.tvLatePaymentFeesAmount.text = holder.binding.tvLatePaymentFeesAmount.context
                    .getString(R.string.currency_symbol).plus(" ").plus(it)
//            holder.binding.tvLatePaymentFeesAmount.text=
//                holder.binding.tvLatePaymentFeesAmount.context.getString(R.string.currency_symbol).plus(" ").plus(UtilsUI.convertAmountFormat(it.toDouble()))
            }

        holder.binding.tvPaidBack.visibility = View.GONE
        holder.binding.tvPaidBackAmount.visibility = View.GONE
        model.paidBack?.let {
            holder.binding.tvPaidBack.visibility = View.VISIBLE
            holder.binding.tvPaidBackAmount.visibility = View.VISIBLE
            holder.binding.tvPaidBackAmount.text = holder.binding.tvPaidBackAmount.context
                .getString(R.string.currency_symbol).plus(" ").plus(it)
//            holder.binding.tvPaidBackAmount.text=
//                holder.binding.tvPaidBackAmount.context.getString(R.string.currency_symbol).plus(" ").plus(UtilsUI.convertAmountFormat(it.toDouble()))

            //Here we apply gradient color
            val paint: TextPaint = holder.binding.tvPaidBackAmount.paint
            val width: Float = paint.measureText(holder.binding.tvPaidBackAmount.text.toString())

            val textShader: Shader = LinearGradient(
                0f, 0f, width, holder.binding.tvPaidBackAmount.textSize, intArrayOf(
                    Color.parseColor("#E83F94"),
                    Color.parseColor("#F54E5E")
                ), null, Shader.TileMode.CLAMP
            )
            holder.binding.tvPaidBackAmount.paint.shader = textShader

        }

        holder.binding.tvOverdueBalance.visibility = View.GONE
        holder.binding.tvOverdueBalanceAmount.visibility = View.GONE
        if (!model.overdueBalance.isNullOrEmpty() && !model.overdueBalance.equals("0"))
        model.overdueBalance?.let {
            holder.binding.tvOverdueBalance.visibility = View.VISIBLE
            holder.binding.tvOverdueBalanceAmount.visibility = View.VISIBLE
            holder.binding.tvOverdueBalanceAmount.text =
                holder.binding.tvOverdueBalanceAmount.context.getString(R.string.currency_symbol)
                    .plus(" ").plus(it)
//            holder.binding.tvOverdueBalanceAmount.text=
//                holder.binding.tvOverdueBalanceAmount.context.getString(R.string.currency_symbol).plus(" ").plus(UtilsUI.convertAmountFormat(it.toDouble()))
        }

        holder.binding.tvSeeTransactionDetail.setOnClickListener {
            listener.onItemClicked(mList[holder.layoutPosition])
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(val binding: ItemAdvanceTransactionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun addList(itemList: ArrayList<AdvanceUsageHistory>?) {
        mList.clear()
        itemList?.let {
            mList.addAll(it)
        }
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        if (!mList.isNullOrEmpty()) {
            mList.clear()
            notifyDataSetChanged()
        }
    }
}