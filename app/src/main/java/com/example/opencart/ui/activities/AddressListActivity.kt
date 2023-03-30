package com.example.opencart.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opencart.R
import com.example.opencart.databinding.ActivityAddressListBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.ui.adapters.AddressListAdapter
import com.example.opencart.utils.Constants
import com.example.opencart.utils.SwipeToDeleteCallback
import com.example.opencart.utils.SwipeToEditCallback
import com.myshoppal.models.Address

class AddressListActivity : BaseActivity() {
    private var alBinding: ActivityAddressListBinding? =null
    private var mSelectAddress: Boolean = false

    private val addEditAddressActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                getAddressList()
            } else {
                Log.e("Request Cancelled", "To add the address.")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alBinding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(alBinding?.root)
        setupActionBar(alBinding!!.toolbarAddressListActivity)

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if (mSelectAddress){
            alBinding?.tvTitle?.text = resources.getString(R.string.select_address)
        }


        alBinding?.tvAddAddress?.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
//            startActivity(intent)
            addEditAddressActivityResult.launch(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }

     fun getAddressList() {

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this@AddressListActivity)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {

        // Hide the progress dialog
        hideProgressDialog()

        if (addressList.size > 0) {

            alBinding?.rvAddressList?.visibility = View.VISIBLE
            alBinding?.tvNoAddressFound?.visibility = View.GONE

            alBinding?.rvAddressList?.layoutManager = LinearLayoutManager(this@AddressListActivity)
            alBinding?.rvAddressList?.setHasFixedSize(true)
            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)
            alBinding?.rvAddressList?.adapter = addressAdapter

//            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        val adapter = alBinding?.rvAddressList?.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(alBinding?.rvAddressList)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        // Show the progress dialog.
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(alBinding?.rvAddressList)
//            }
        } else {
            alBinding?.rvAddressList?.visibility = View.GONE
            alBinding?.tvNoAddressFound?.visibility = View.VISIBLE
        }
    }


    fun deleteAddressSuccess() {

        // Hide progress dialog.
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.error_message_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()
    }


    override fun onDestroy() {
        super.onDestroy()
        alBinding = null
    }
}