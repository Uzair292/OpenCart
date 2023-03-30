package com.example.opencart.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.opencart.R
import com.example.opencart.databinding.ActivitySettingsBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.User
import com.example.opencart.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private var setBinding : ActivitySettingsBinding? = null
    private lateinit var mUserProfileDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(setBinding?.root)
        setupActionBar(setBinding!!.toolbarSettingsActivity)
        setBinding?.tvEdit?.setOnClickListener(this@SettingsActivity)
        setBinding?.btnLogout?.setOnClickListener(this@SettingsActivity)
        setBinding?.llAddress?.setOnClickListener(this@SettingsActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserProfileDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {

                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }
    fun userDetailsSuccess(user: User) {

        mUserProfileDetails = user

        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, setBinding!!.ivUserPhoto)

        setBinding?.tvName?.text = "${user.firstName} ${user.lastName}"
        setBinding?.tvGender?.text = user.gender
        setBinding?.tvEmail?.text = user.email
        setBinding?.tvMobileNumber?.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }
}