package com.example.opencart.ui.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.opencart.R
import com.example.opencart.databinding.ActivityAddProductBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.Product
import com.example.opencart.utils.Constants

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private var apBinding: ActivityAddProductBinding? = null

    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // permission is granted, launch the image picker
                pickImage.launch("image/*")
            } else {
                // permission is denied, show a message or handle the error
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { imageUri ->
                // handle the selected image URI here
//                apBinding?.ivProductImage?.setImageURI(imageUri)
                mSelectedImageFileUri = imageUri
                GlideLoader(this).loadUserPicture(imageUri, apBinding!!.ivProductImage)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apBinding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(apBinding?.root)
        setupActionBar(apBinding!!.toolbarAddProductActivity)

        // set a click listener on a button to launch the image picker again
        apBinding?.ivAddUpdateProduct?.setOnClickListener(this)
        apBinding?.btnSubmitAddProduct?.setOnClickListener(this)

    }

    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.error_message_product_image), true)
                false
            }

            TextUtils.isEmpty(apBinding?.etProductTitle?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_message_product_title), true)
                false
            }

            TextUtils.isEmpty(apBinding?.etProductPrice?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_message_product_price), true)
                false
            }

            TextUtils.isEmpty(
                apBinding?.etProductDescription?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.error_message_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(apBinding?.etProductQuantity?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.error_message_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        apBinding = null
    }

    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductImage() {

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(
            this@AddProductActivity,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }

    private fun uploadProductDetails() {

        // Get the logged in username from the SharedPreferences that we have stored at a time of login.
        val username =
            this.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val product = Product(
            FirestoreClass().getCurrentUserId(),
            username,
            apBinding?.etProductTitle?.text.toString().trim { it <= ' ' },
            apBinding?.etProductPrice?.text.toString().trim { it <= ' ' },
            apBinding?.etProductDescription?.text.toString().trim { it <= ' ' },
            apBinding?.etProductQuantity?.text.toString().trim { it <= ' ' },
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    fun productUploadSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_upload_success_message),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.iv_add_update_product -> {
                    // check if the permission is granted, if not, request for it
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // permission is already granted, launch the image picker
                        pickImage.launch("image/*")
                    } else {
                        // permission is not granted, request for it
                        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
                R.id.btn_submit_add_product -> {
                    if (validateProductDetails()) {
                        uploadProductImage()
                    }
                }
            }
        }
    }
}