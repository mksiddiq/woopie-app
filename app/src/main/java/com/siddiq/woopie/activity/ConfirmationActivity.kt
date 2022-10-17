package com.siddiq.woopie.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.Room
import com.siddiq.woopie.R
import com.siddiq.woopie.database.OrderDatabase

class ConfirmationActivity : AppCompatActivity() {
    lateinit var btnConfirmOK: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        btnConfirmOK = findViewById(R.id.btnConfirmOK)

        RestaurantDetailsActivity.DeleteOrdersFromDatabase(applicationContext).execute().get()

        btnConfirmOK.setOnClickListener {
            val intent = Intent(this@ConfirmationActivity, NavigationDrawerActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
