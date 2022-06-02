package com.payu.baas.coreUI.view.ui.activity.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.databinding.AdapterOffersBinding
import com.payu.baas.core.model.model.OfferDetails
import com.payu.baas.core.storage.SessionManager
import com.payu.baas.coreUI.view.callback.ItemClickListener

class OffersAdapter internal constructor(
    private val listener: ItemClickListener,
    private val context: Context
) :
    RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    private var list: ArrayList<OfferDetails> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<AdapterOffersBinding>(
                LayoutInflater.from(parent.context),
                R.layout.adapter_offers,
                parent, false
            )
        );
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(SessionManager.getInstance(context).s3BucketUrl + list[position].name)
            .error(R.drawable.delivery_boy)
            .into(holder.binding.ivOffer)

        holder.itemView.setOnClickListener {
            listener.onItemClicked(list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: AdapterOffersBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    fun setList(itemList: ArrayList<OfferDetails>) {
        list.clear()
        list.addAll(itemList)
        notifyDataSetChanged()
    }
}