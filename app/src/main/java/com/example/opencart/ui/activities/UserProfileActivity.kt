package com.example.opencart.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringDef
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.opencart.R
import com.example.opencart.databinding.ActivityUserProfileBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.User
import com.example.opencart.utils.Constants
import com.google.firebase.storage.FirebaseStorage

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private var upBinding : ActivityUserProfileBinding? = null
    private val resultLauncher by lazy {upBinding!!.ivUserPhoto}
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""
    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
//            resultLauncher?.setImageURI(uri)
            mSelectedImageFileUri = uri
            GlideLoader(this).loadUserPicture(uri,resultLauncher)
        }
    }
    private lateinit var mUserDetails: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        upBinding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(upBinding?.root)
        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
        mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS, User::class.java)!!
//            } else {
//                intent.getParcelableExtra<User>(Constants.EXTRA_USER_DETAILS)
//            }
        }

        if (mUserDetails.profileCompleted == 0) {
            // Update the title of the screen to complete profile.
            upBinding?.tvTitle?.text = resources.getString(R.string.title_complete_profile)

            // Here, the some of the edittext components are disabled because it is added at a time of Registration.
            upBinding?.etFirstName?.isEnabled = false
            upBinding?.etFirstName?.setText(mUserDetails.firstName)

            upBinding?.etLastName?.isEnabled = false
            upBinding?.etLastName?.setText(mUserDetails.lastName)

            upBinding?.etEmail?.isEnabled = false
            upBinding?.etEmail?.setText(mUserDetails.email)
        } else {

            setupActionBar(upBinding!!.toolbarUserProfileActivity)
            upBinding?.tvTitle?.text = resources.getString(R.string.edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, upBinding!!.ivUserPhoto)

            upBinding?.etFirstName?.setText(mUserDetails.firstName)
            upBinding?.etLastName?.setText(mUserDetails.lastName)

            upBinding?.etEmail?.isEnabled = false
            upBinding?.etEmail?.setText(mUserDetails.email)

            if (mUserDetails.mobile != 0L) {
                upBinding?.etMobileNumber?.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE) {
                upBinding?.rbMale?.isChecked = true
            } else {
                upBinding?.rbFemale?.isChecked = true
            }
        }

        upBinding?.ivUserPhoto?.setOnClickListener(this@UserProfileActivity)
        upBinding?.btnSave?.setOnClickListener(this@UserProfileActivity)
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Constants.showImageChooser(this@UserProfileActivity)
                selectImageFromGallery()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        upBinding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_user_photo -> {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    selectImageFromGallery()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_STORAGE_PERMISSION_CODE
                    )
                }
            }
            R.id.btn_save -> {

                if (validateUserProfileDetails()){
                    showProgressDialog(resources.getString(R.string.please_wait))
                    if (mSelectedImageFileUri!=null){
                        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)
                    }
                    else{
                        updateUserProfileDetails()
                    }

                }
            }
        }
    }

    private fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val firstName = upBinding?.etFirstName?.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetails.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = upBinding?.etLastName?.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetails.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val mobileNumber = upBinding?.etMobileNumber?.text.toString().trim { it <= ' ' }
        val gender = if (upBinding!!.rbMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }

        if (mUserDetails.profileCompleted == 0) {
            userHashMap[Constants.COMPLETE_PROFILE] = 1
        }
        FirestoreClass().updateUserProfileData(
            this@UserProfileActivity,
            userHashMap
        )
    }

    private fun validateUserProfileDetails(): Boolean {
         when {
            TextUtils.isEmpty(upBinding?.etMobileNumber?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.error_mobile_number), true)
                return false
            }
            upBinding?.etMobileNumber?.text?.length!! <=10 -> {
                Toast.makeText(
                    this@UserProfileActivity,
                    "please enter valid and complete number",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
//            mSelectedImageFileUri == null -> {}
            else  -> {
                return true
            }
        }
    }

    fun userProfileUpdateSuccess() {

        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.profile_update_success_message),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }

    fun imageUploadSuccess(imageURL: String) {

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}