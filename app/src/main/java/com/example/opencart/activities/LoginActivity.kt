package com.example.opencart.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.example.opencart.R
import com.example.opencart.databinding.ActivityLoginBinding
import com.example.opencart.databinding.ActivitySplashBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.User
import com.example.opencart.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(), View.OnClickListener {
    private var lgBinding : ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lgBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(lgBinding?.root)

        lgBinding?.tvRegister?.setOnClickListener(this)
        lgBinding?.tvForgotPassword?.setOnClickListener(this)
        lgBinding?.btnLogin?.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        lgBinding = null
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.tv_forgot_password -> {

                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(lgBinding?.etEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(lgBinding?.etPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }


    private fun logInRegisteredUser() {

        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = lgBinding?.etEmail?.text.toString().trim { it <= ' ' }
            val password = lgBinding?.etPassword?.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)

                    } else {
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userLoggedInSuccess(user: User) {

        // Hide the progress dialog.
        hideProgressDialog()

        // Print the user details in the log as of now.
        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        }
        finish()
    }

}