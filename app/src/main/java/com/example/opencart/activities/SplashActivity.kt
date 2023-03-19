package com.example.opencart.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opencart.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var spBinding : ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        spBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(spBinding?.root)

        val runnable = Runnable {
            // Start the second activity after a 3-second delay
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
//        findViewById<View>(android.R.id.content).postDelayed(runnable, 3000) // Delay in milliseconds (3 seconds = 3000 milliseconds)
        spBinding?.root?.postDelayed(runnable, 3000)



        /*
        Deprecated Code
        Handler().postDelayed({
            // Start the second activity after a 3-second delay
            startActivity(Intent(this@SplashActivity,MainActivity::class.java))
            finish()
        }, 3000) // Delay in milliseconds (3 seconds = 3000 milliseconds)
         */
        /*
        Code for changing font
        val typeface : Typeface = Typeface.createFromAsset(assets, "Montserrat-Bold.ttf")
        spBinding?.openCart?.typeface =
         */

    }

    override fun onPause() {
        super.onPause()
        // Cancel the delayed runnable if the activity is paused or stopped
        spBinding?.root?.removeCallbacks(null)
    }
    override fun onDestroy() {
        super.onDestroy()
        spBinding = null
    }
}