package com.example.opencart.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.opencart.activities.UserProfileActivity

object Constants {
    const val USERS : String = "users"
    const val SHARED_PREFERENCES_NAME = "OpenCart-Preferences"
    const val LOGGED_IN_USERNAME = "Logged-in-username"
    const val EXTRA_USER_DETAILS = "user_details"
    const val READ_STORAGE_PERMISSION_CODE = 1

    const val MALE: String = "Male"
    const val FEMALE: String = "Female"

    // Firebase database field names
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    const val IMAGE: String = "image"
    const val PROFILE_COMPLETED = "profileCompleted"

    const val USER_PROFILE_IMAGE:String = "User_Profile_Image"
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