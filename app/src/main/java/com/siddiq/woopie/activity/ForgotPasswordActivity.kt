package com.siddiq.woopie.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.siddiq.woopie.R

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.title = "Forgot Password"
    }
}