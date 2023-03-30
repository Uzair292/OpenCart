package com.example.opencart.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.opencart.R
import com.example.opencart.databinding.ItemCartLayoutBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.CartItem
import com.example.opencart.ui.activities.CartListActivity
import com.example.opencart.ui.activities.GlideLoader
import com.example.opencart.utils.Constants

class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>
) : RecyclerView.Adapter<CartItemsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image, holder.binding.ivCartItemImage)

        holder.binding.tvCartItemTitle.text = model.title
        holder.binding.tvCartItemPrice.text = "$${model.price}"
        holder.binding.tvCartQuantity.text = model.cart_quantity

        if (model.cart_quantity == "0") {
            holder.binding.ibRemoveCartItem.visibility = View.GONE
            holder.binding.ibAddCartItem.visibility = View.GONE

            holder.binding.tvCartQuantity.text =
                context.resources.getString(R.string.lbl_text_out_of_stock)

            holder.binding.tvCartQuantity.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorSnackBarError
                )
            )
        } else {
            holder.binding.ibRemoveCartItem.visibility = View.VISIBLE
            holder.binding.ibAddCartItem.visibility = View.VISIBLE

            holder.binding.tvCartQuantity.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorSecondaryText
                )
            )
        }

        holder.binding.ibDeleteCartItem.setOnClickListener {
            when(context){
                is CartListActivity -> {
//                    context.showProgressDialog(context.resources.getString(R.string.please_wait))
                }
            }
            FirestoreClass().removeItemFromCart(context,model.id)
        }

        holder.binding.ibRemoveCartItem.setOnClickListener {

            if (model.cart_quantity == "1") {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.resources.getString(R.string.delete_dialog_title))
                builder.setMessage(context.resources.getString(R.string.delete_dialog_message))
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton(context.resources.getString(R.string.yes)) { dialogInterface, _ ->
//                    context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    FirestoreClass().removeItemFromCart(context,model.id )
                    dialogInterface.dismiss()
                }
                builder.setNegativeButton(context.resources.getString(R.string.no)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            } else {

                val cartQuantity: Int = model.cart_quantity.toInt()
                val itemHashMap = HashMap<String, Any>()
                itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()
                if (context is CartListActivity) {
                    context.showProgressDialog(context.resources.getString(R.string.please_wait))
                }
                FirestoreClass().updateMyCart(context, model.id, itemHashMap)
            }
        }

        holder.binding.ibAddCartItem.setOnClickListener {
            val cartQuantity: Int = model.cart_quantity.toInt()
            if (cartQuantity < model.stock_quantity.toInt()) {
                val itemHashMap = HashMap<String, Any>()
                itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()
                if (context is CartListActivity) {
                    context.showProgressDialog(context.resources.getString(R.string.please_wait))
                }
                FirestoreClass().updateMyCart(context, model.id, itemHashMap)
            } else {
                if (context is CartListActivity) {
                    context.showErrorSnackBar(
                        context.resources.getString(
                            R.string.msg_for_available_stock,
                            model.stock_quantity
                        ),
                        true
                    )
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
