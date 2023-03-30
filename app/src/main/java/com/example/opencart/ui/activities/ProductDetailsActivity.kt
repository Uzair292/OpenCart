package com.example.opencart.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.opencart.R
import com.example.opencart.databinding.ActivityProductDetailsBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.CartItem
import com.example.opencart.models.Product
import com.example.opencart.utils.Constants

class ProductDetailsActivity : BaseActivity(),View.OnClickListener {

    private var pdaBinding: ActivityProductDetailsBinding? = null
    private var mProductID : String = ""
    private var mProductOwnerID : String = ""
    private lateinit var mProductDetails: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pdaBinding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(pdaBinding?.root)
        setupActionBar(pdaBinding!!.toolbarProductDetailsActivity)
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID).toString()
        }
        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductOwnerID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID).toString()
        }
        if (mProductOwnerID== FirestoreClass().getCurrentUserId()){
            pdaBinding?.btnAddToCart?.visibility = View.GONE
            pdaBinding?.btnGoToCart?.visibility = View.GONE
        }
        else{
            pdaBinding?.btnAddToCart?.visibility = View.VISIBLE
        }

        getProductDetails()
        pdaBinding?.btnAddToCart?.setOnClickListener(this)
        pdaBinding?.btnGoToCart?.setOnClickListener(this)
    }


    private fun getProductDetails() {

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductID)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product
//        hideProgressDialog()
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            pdaBinding!!.ivProductDetailImage
        )

        pdaBinding?.tvProductDetailsTitle?.text = product.title
        pdaBinding?.tvProductDetailsPrice?.text = "$${product.price}"
        pdaBinding?.tvProductDetailsDescription?.text = product.description
        pdaBinding?.tvProductDetailsStockQuantity?.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            pdaBinding?.btnAddToCart?.visibility = View.GONE

            pdaBinding?.tvProductDetailsStockQuantity?.text =
                resources.getString(R.string.lbl_text_out_of_stock)

            pdaBinding?.tvProductDetailsStockQuantity?.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailsActivity,
                    R.color.colorSnackBarError
                )
            )
        }else{
            if (FirestoreClass().getCurrentUserId() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductID)
            }
        }
    }

    private fun addToCart() {

        val addToCart = CartItem(
            FirestoreClass().getCurrentUserId(),
            mProductID,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@ProductDetailsActivity, addToCart)

    }

    fun productExistsInCart(){
        hideProgressDialog()
        pdaBinding?.btnAddToCart?.visibility = View.GONE
        pdaBinding?.btnGoToCart?.visibility = View.VISIBLE
    }
    fun  addToCartSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_in_cart),
            Toast.LENGTH_SHORT
        ).show()

        pdaBinding?.btnAddToCart?.visibility = View.GONE
        pdaBinding?.btnGoToCart?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        pdaBinding = null
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_to_cart ->{
                    addToCart()
                }
                R.id.btn_go_to_cart ->{

                }
            }
        }
    }
}

