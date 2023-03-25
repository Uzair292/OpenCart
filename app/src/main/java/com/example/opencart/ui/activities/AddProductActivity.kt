package com.example.opencart.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opencart.databinding.ActivityAddProductBinding

class AddProductActivity : BaseActivity() {
    private var apBinding : ActivityAddProductBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apBinding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(apBinding?.root)
        setupActionBar(apBinding!!.toolbarAddProductActivity)
    }

    override fun onDestroy() {
        super.onDestroy()
        apBinding = null
    }
}