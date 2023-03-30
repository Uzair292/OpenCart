package com.example.opencart.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.opencart.databinding.ItemDashboardLayoutBinding
import com.example.opencart.models.Product
import com.example.opencart.ui.activities.GlideLoader


open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener : OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDashboardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.bind(model, context)
        }
        holder.itemView.setOnClickListener{
            if (onClickListener!=null){
            onClickListener!!.onClick(position,model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemDashboardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Product, context: Context) {
            GlideLoader(context).loadProductPicture(model.image, binding.ivDashboardItemImage)
            binding.tvDashboardItemTitle.text = model.title
            binding.tvDashboardItemPrice.text = "$${model.price}"
        }

    }

    fun setonClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, product: Product)
    }
}
