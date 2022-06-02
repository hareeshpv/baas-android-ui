package com.payu.baas.coreUI.view.ui.activity.advance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ItemAdvanceWeeklyTransactionDetailBinding
import com.payu.baas.coreUI.model.entities.model.GetPassBookTransactionDetailHeader
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.util.enums.CardType
import com.payu.baas.coreUI.util.enums.TransactionStatus
import com.payu.baas.coreUI.view.callback.ImageLoadingListener

class AdvanceWeeklyTransactionDetailsAdapter internal constructor(
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<AdvanceWeeklyTransactionDetailsAdapter.ViewHolder>() {

    val list: ArrayList<GetPassBookTransactionDetails> = arrayListOf()

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
            DataBindingUtil.inflate<ItemAdvanceWeeklyTransactionDetailBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_advance_weekly_transaction_detail,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClicked(list[holder.layoutPosition])
        }

        holder.binding.tvMode.text = list[position].label

        holder.binding.tvTransactionDate.text = list[position].date


        holder.binding.ivTransactionTypeIcon.visibility = View.GONE
        holder.binding.tvShortName.visibility = View.GONE
//        if(list[position].iconUrl.isNullOrEmpty()){
        if (list[position].transactionTypeIcon.isNullOrEmpty()) {
            holder.binding.tvShortName.visibility = View.VISIBLE
            holder.binding.tvShortName.text = UtilsUI.getShortName(list[position].label)
        } else {
            holder.binding.ivTransactionTypeIcon.visibility = View.VISIBLE
            //Write code for loading image
            UtilsUI.loadUrl(
                holder.binding.ivTransactionTypeIcon,
                list[position].transactionTypeIcon, null,
                object : ImageLoadingListener {
                    override fun loadingFailed() {
                        holder.binding.ivTransactionTypeIcon.visibility = View.GONE
                        holder.binding.tvShortName.visibility = View.VISIBLE
                        holder.binding.tvShortName.text = UtilsUI.getShortName(list[position].label)
                    }

                }
            )
        }

        when (list[position].debitCreditIndicator?.uppercase()) {
            CardType.CREDIT.toString() -> {
                holder.binding.tvTransactionAmount.text = "+".plus(" ")
                    .plus(holder.binding.tvTransactionAmount.context.getString(R.string.currency_symbol))
                    .plus(" ")
                    .plus(
                        list[position].amount?.toDouble()?.let { UtilsUI.convertAmountFormat(it) })
            }
            CardType.DEBIT.toString() -> {
                holder.binding.tvTransactionAmount.text = "-".plus(" ")
                    .plus(holder.binding.tvTransactionAmount.context.getString(R.string.currency_symbol))
                    .plus(" ")
                    .plus(
                        list[position].amount?.toDouble()?.let { UtilsUI.convertAmountFormat(it) })
            }
        }

        holder.binding.llTransactionStatusSuccess.visibility = View.GONE
        holder.binding.tvTransactionStatus.visibility = View.GONE
        when (list[position].txnStatus?.uppercase()) {
            TransactionStatus.SUCCESS.toString() -> {
                holder.binding.llTransactionStatusSuccess.visibility = View.VISIBLE

                when (list[position].debitCreditIndicator?.uppercase()) {
                    CardType.CREDIT.toString() -> {
                        holder.binding.tvTransactionAmount.setTextColor(
                            ContextCompat.getColor(
                                holder.binding.tvTransactionAmount.context,
                                R.color.income
                            )
                        )
                        holder.binding.tvTransactionType.text = "To"
                        holder.binding.ivTransactionType.setImageDrawable(
                            ContextCompat.getDrawable(
                                holder.binding.ivTransactionType.context,
                                R.drawable.icon_wallet
                            )
                        )
                    }
                    CardType.DEBIT.toString() -> {
                        holder.binding.tvTransactionAmount.setTextColor(
                            ContextCompat.getColor(
                                holder.binding.tvTransactionAmount.context,
                                R.color.expense
                            )
                        )
                        holder.binding.tvTransactionType.text = "From"
                        holder.binding.ivTransactionType.setImageDrawable(
                            ContextCompat.getDrawable(
                                holder.binding.ivTransactionType.context,
                                R.drawable.icon_advance_allowed
                            )
                        )
                    }

                }
            }
            TransactionStatus.FAILED.toString() -> {
                holder.binding.tvTransactionStatus.visibility = View.VISIBLE
                holder.binding.tvTransactionAmount.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.tvTransactionAmount.context,
                        R.color.subtext_color
                    )
                )

                holder.binding.tvTransactionStatus.text = list[position].status
                holder.binding.tvTransactionStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.tvTransactionStatus.context,
                        R.color.transaction_failed
                    )
                )
            }
            TransactionStatus.PENDING.toString() -> {
                holder.binding.tvTransactionStatus.visibility = View.VISIBLE
                holder.binding.tvTransactionAmount.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.tvTransactionAmount.context,
                        R.color.subtext_color
                    )
                )

                holder.binding.tvTransactionStatus.text = list[position].txnStatus
                holder.binding.tvTransactionStatus.setTextColor(
                    ContextCompat.getColor(
                        holder.binding.tvTransactionStatus.context,
                        R.color.transaction_pending
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemAdvanceWeeklyTransactionDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun addList(itemList: ArrayList<GetPassBookTransactionDetails>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        if (!list.isNullOrEmpty()) {
            list.clear()
            notifyDataSetChanged()
        }
    }

    @JvmName("getList1")
    fun getList(): ArrayList<GetPassBookTransactionDetails> {
        return list
    }
}