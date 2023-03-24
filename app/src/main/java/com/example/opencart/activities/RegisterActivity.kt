package com.example.opencart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.opencart.R
import com.example.opencart.databinding.ActivityRegisterBinding
import com.example.opencart.firebase.FirestoreClass
import com.example.opencart.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    private var rgBinding: ActivityRegisterBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rgBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(rgBinding?.root)
        setupActionBar()
        rgBinding?.btnRegister?.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(rgBinding?.toolbarRegisterActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
        }
        rgBinding?.toolbarRegisterActivity?.setNavigationOnClickListener { onBackPressed() }
    }
    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(rgBinding?.etFirstName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(rgBinding?.etLastName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(rgBinding?.etEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(rgBinding?.etPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(rgBinding?.etConfirmPassword?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            rgBinding?.etPassword?.text.toString()
                .trim { it <= ' ' } != rgBinding?.etConfirmPassword?.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }
            !rgBinding!!.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }
            else -> {
//                showErrorSnackBar(resources.getString(R.string.register_successfully), false)
                true
            }
        }


    }
    private fun registerUser() {

        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {

            // Show the progress dialog
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = rgBinding?.etEmail?.text.toString().trim { it <= ' ' }
            val password: String = rgBinding?.etPassword?.text.toString().trim { it <= ' ' }

            // Create an instance and create a register a user with email and password.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val user = User(
                                firebaseUser.uid,
                                rgBinding?.etFirstName?.text.toString().trim { it <= ' ' },
                                rgBinding?.etLastName?.text.toString().trim { it <= ' ' },
                                rgBinding?.etEmail?.text.toString().trim { it <= ' ' }
                            )

                            // Pass the required values in the constructor.
                            FirestoreClass().registerUser(this@RegisterActivity, user)
//                            FirebaseAuth.getInstance().signOut()
//                            finish()

                        } else {
                            // If the registering is not successful then show error message.
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    fun userRegistrationSuccess() {

        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_successfully),
            Toast.LENGTH_SHORT
        ).show()
    }
    }