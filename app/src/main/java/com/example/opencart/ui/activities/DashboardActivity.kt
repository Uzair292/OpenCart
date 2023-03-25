package com.example.opencart.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.opencart.R
import com.example.opencart.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private var dashBinding: ActivityDashboardBinding? = null
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dashBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(dashBinding?.root)

        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )
//
//        val navController = findNavController(dashBinding!!.navHostFragment)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_products,
                R.id.navigation_dashboard,
                R.id.navigation_orders
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        dashBinding?.navView?.setupWithNavController(navController)


        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController, appBarConfiguration)
        dashBinding?.navView?.setupWithNavController(navController)












        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce){
                    finish()
                }
                else{
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                    this@DashboardActivity,
                    resources.getString(R.string.please_press_back_again_to_exit),
                    Toast.LENGTH_SHORT
                    ).show()
                    Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }

        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()

    }

}