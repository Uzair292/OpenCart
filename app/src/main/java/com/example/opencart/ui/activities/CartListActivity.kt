package com.example.opencart.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.opencart.R
import com.example.opencart.databinding.ActivityCartListBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.CartItem
import com.example.opencart.models.Product
import com.example.opencart.ui.adapters.CartItemsListAdapter
import com.example.opencart.utils.Constants

class CartListActivity : BaseActivity() {
    private var clBinding: ActivityCartListBinding? = null
    private lateinit var mCartListItems: ArrayList<CartItem>
    private lateinit var mProductsList: ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clBinding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(clBinding?.root)
        setupActionBar(clBinding!!.toolbarCartListActivity)
        clBinding?.btnCheckout?.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }


    }

    private fun getCartItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {

        // Hide progress dialog.
        hideProgressDialog()
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {

                    cart.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0) {
                        cart.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList
        if (mCartListItems.size > 0) {

            clBinding?.rvCartItemsList?.visibility = View.VISIBLE
            clBinding?.llCheckout?.visibility = View.VISIBLE
            clBinding?.tvNoCartItemFound?.visibility = View.GONE

            clBinding?.rvCartItemsList?.layoutManager = LinearLayoutManager(this@CartListActivity)
            clBinding?.rvCartItemsList?.setHasFixedSize(true)

            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems)
            clBinding?.rvCartItemsList?.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {

                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }

            clBinding?.tvSubTotal?.text = "$$subTotal"
            clBinding?.tvShippingCharge?.text = "$10.0"    //change this later

            if (subTotal > 0) {
                clBinding?.llCheckout?.visibility = View.VISIBLE
                val total = subTotal + 10
                clBinding?.tvTotalAmount?.text = "$$total"
            } else {
                clBinding?.llCheckout?.visibility = View.GONE
            }

        } else {
            clBinding?.rvCartItemsList?.visibility = View.GONE
            clBinding?.llCheckout?.visibility = View.GONE
            clBinding?.tvNoCartItemFound?.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()
    }

    fun itemRemovedSuccess() {

//        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    override fun onDestroy() {
        super.onDestroy()
        clBinding = null
    }
}