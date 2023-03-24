package com.example.opencart.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opencart.databinding.ActivityMainBinding
import com.example.opencart.utils.Constants

class MainActivity : AppCompatActivity(){
private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        val preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        val username = preferences.getString(Constants.LOGGED_IN_USERNAME, "")
        binding?.tvGreetings?.text = "Hello! ${username}"
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}