package com.example.opencart.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.opencart.R
import com.example.opencart.databinding.ActivityAddEditAddressBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.utils.Constants
import com.myshoppal.models.Address

class AddEditAddressActivity : BaseActivity() {
    private var aaBinding: ActivityAddEditAddressBinding? =null
    private var mAddressDetails: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aaBinding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(aaBinding?.root)
        setupActionBar(aaBinding!!.toolbarAddEditAddressActivity)


        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails =
                intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {

                aaBinding?.tvTitle?.text = resources.getString(R.string.title_edit_address)
                aaBinding?.btnSubmitAddress?.text = resources.getString(R.string.btn_lbl_update)

                aaBinding?.etFullName?.setText(mAddressDetails?.name)
                aaBinding?.etPhoneNumber?.setText(mAddressDetails?.mobileNumber)
                aaBinding?.etAddress?.setText(mAddressDetails?.address)
                aaBinding?.etZipCode?.setText(mAddressDetails?.zipCode)
                aaBinding?.etAdditionalNote?.setText(mAddressDetails?.additionalNote)

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        aaBinding?.rbHome?.isChecked = true
                    }
                    Constants.OFFICE -> {
                        aaBinding?.rbOffice?.isChecked = true
                    }
                    else -> {
                        aaBinding?.rbOther?.isChecked = true
                        aaBinding?.tilOtherDetails?.visibility = View.VISIBLE
                        aaBinding?.etOtherDetails?.setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }



        aaBinding?.rgType?.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                aaBinding?.tilOtherDetails?.visibility = View.VISIBLE
            } else {
                aaBinding?.tilOtherDetails?.visibility = View.GONE
            }
        }

        aaBinding?.btnSubmitAddress?.setOnClickListener {
            saveAddressToFirestore()
        }
    }

    private fun validateData(): Boolean {
        return when {

            TextUtils.isEmpty(aaBinding?.etFullName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }

            TextUtils.isEmpty(aaBinding?.etPhoneNumber?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.error_mobile_number),
                    true
                )
                false
            }

            TextUtils.isEmpty(aaBinding?.etAddress?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }

            TextUtils.isEmpty(aaBinding?.etZipCode?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }

            aaBinding!!.rbOther.isChecked && TextUtils.isEmpty(
                aaBinding?.etZipCode?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun saveAddressToFirestore() {

        // Here we get the text from editText and trim the space
        val fullName: String = aaBinding?.etFullName?.text.toString().trim { it <= ' ' }
        val phoneNumber: String = aaBinding?.etPhoneNumber?.text.toString().trim { it <= ' ' }
        val address: String = aaBinding?.etAddress?.text.toString().trim { it <= ' ' }
        val zipCode: String = aaBinding?.etZipCode?.text.toString().trim { it <= ' ' }
        val additionalNote: String = aaBinding?.etAdditionalNote?.text.toString().trim { it <= ' ' }
        val otherDetails: String = aaBinding?.etOtherDetails?.text.toString().trim { it <= ' ' }

        if (validateData()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                aaBinding!!.rbHome.isChecked -> {
                    Constants.HOME
                }
                aaBinding!!.rbOffice.isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserId(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddress(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
            FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.address_added_success_msg)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

//        setResult(RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        aaBinding = null
    }

}