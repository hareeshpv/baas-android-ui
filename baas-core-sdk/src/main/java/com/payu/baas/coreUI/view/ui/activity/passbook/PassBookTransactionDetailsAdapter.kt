package com.payu.baas.coreUI.view.ui.activity.passbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.ItemPassbookTransactionDetailsBinding
import com.payu.baas.core.model.responseModels.GetPassBookTransactionDetails
import com.payu.baas.coreUI.model.storage.SessionManagerUI
import com.payu.baas.coreUI.util.UtilsUI
import com.payu.baas.coreUI.util.enums.CardType
import com.payu.baas.coreUI.util.enums.TransactionStatus
import com.payu.baas.coreUI.view.callback.ImageLoadingListener

class PassBookTransactionDetailsAdapter internal constructor(
    private val list: ArrayList<GetPassBookTransactionDetails>,
    private val listener: PassBookTransactionsAdapter.ItemClickListener
) :
    RecyclerView.Adapter<PassBookTransactionDetailsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemPassbookTransactionDetailsBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_passbook_transaction_details,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
            listener.onItemClicked(list[holder.layoutPosition])
        }

        UtilsUI.applyColorToText(
            holder.binding.tvShortName,
            SessionManagerUI.getInstance(holder.binding.tvMode.context).accentColor
        )
        UtilsUI.applyColorToText(
            holder.binding.tvMode,
            SessionManagerUI.getInstance(holder.binding.tvMode.context).accentColor
        )

        holder.binding.tvMode.text = list[position].label

        holder.binding.tvTransactionDate.text = list[position].displayDate


        holder.binding.ivTransactionTypeIcon.visibility = View.GONE
        holder.binding.tvShortName.visibility = View.GONE
        if (list[position].transactionTypeIcon.isNullOrEmpty()) {
            holder.binding.tvShortName.visibility = View.VISIBLE
            holder.binding.tvShortName.text = UtilsUI.getShortName(list[position].label)
        } else {
            holder.binding.ivTransactionTypeIcon.visibility = View.VISIBLE
            //Write code for loading image
            UtilsUI.loadUrl(
                holder.binding.ivTransactionTypeIcon,
                list[position].transactionTypeIcon, null,
                object  : ImageLoadingListener{
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
                list[position].amount?.let { amt ->
                    holder.binding.tvTransactionAmount.text = "+".plus(" ")
                        .plus(holder.binding.tvTransactionAmount.context.getString(R.string.currency_symbol))
                        .plus(" ")
                        .plus(UtilsUI.convertAmountFormat(amt.toDouble()))
                }
            }
            CardType.DEBIT.toString() -> {
                list[position].amount?.let { amt ->
                    holder.binding.tvTransactionAmount.text = "-".plus(" ")
                        .plus(holder.binding.tvTransactionAmount.context.getString(R.string.currency_symbol))
                        .plus(" ")
                        .plus(UtilsUI.convertAmountFormat(amt.toDouble()))
                }
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
                    }
                    CardType.DEBIT.toString() -> {
                        holder.binding.tvTransactionAmount.setTextColor(
                            ContextCompat.getColor(
                                holder.binding.tvTransactionAmount.context,
                                R.color.expense
                            )
                        )
                        holder.binding.tvTransactionType.text = "From"
                    }
                }


                if (list[position].accountTypeIcon.isNullOrEmpty().not()) {

                    holder.binding.ivAccountTypeIcon.visibility=View.VISIBLE
                    //Write code for loading image
                    UtilsUI.loadUrl(
                        holder.binding.ivAccountTypeIcon,
                        list[position].accountTypeIcon, null,
                        object  : ImageLoadingListener{
                            override fun loadingFailed() {
                                holder.binding.ivAccountTypeIcon.visibility=View.GONE
                            }
                        }
                    )
                }else{
                    holder.binding.ivAccountTypeIcon.visibility=View.GONE
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

                holder.binding.tvTransactionStatus.text = list[position].txnStatus
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

    class ViewHolder(val binding: ItemPassbookTransactionDetailsBinding) :
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
}