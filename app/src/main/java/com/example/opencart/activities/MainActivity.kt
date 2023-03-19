package com.example.opencart.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opencart.R
import com.example.opencart.databinding.ActivityMainBinding
import com.example.opencart.databinding.ActivitySplashBinding

class MainActivity : AppCompatActivity(){
private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}