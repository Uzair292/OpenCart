package com.example.opencart.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.opencart.ui.activities.UserProfileActivity

object Constants {
    const val USERS : String = "users"
    const val PRODUCTS : String = "products"
    const val CART_ITEMS : String = "cart_item"
    const val ADDRESSES : String = "addresses"

    const val SHARED_PREFERENCES_NAME = "OpenCart-Preferences"
    const val LOGGED_IN_USERNAME = "Logged-in-username"
    const val EXTRA_USER_DETAILS = "user_details"
    const val READ_STORAGE_PERMISSION_CODE = 1

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"

    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"

    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE = "profileCompleted"

    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
    const val PRODUCT_IMAGE: String = "Product_Image"

    const val USER_ID: String = "user_id"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"

    const val PRODUCT_ID: String = "product_id"

    const val CART_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    const val EXTRA_ADDRESS_DETAILS: String = "address_details"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    /*
    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }
     */

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}