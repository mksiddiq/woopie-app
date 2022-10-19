package com.siddiq.woopie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.siddiq.woopie.R
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class OTPActivity : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPasswordOTP: EditText
    lateinit var btnSubmit: Button
    lateinit var progressBarOTP: ProgressBar
    lateinit var sharedPreferences: SharedPreferences
    var mobileNumberIntent: String? = "100"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        supportActionBar?.title = "Reset Password"

        if(intent!=null){
            mobileNumberIntent = intent.getStringExtra("mobileNumber") as String
        }

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPasswordOTP = findViewById(R.id.etConfirmPasswordOTP)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressBarOTP = findViewById(R.id.progressBarOTP)
        sharedPreferences = getSharedPreferences(getString(R.string.woopie_shared_preferences), Context.MODE_PRIVATE)

        progressBarOTP.visibility = View.GONE

        val queue = Volley.newRequestQueue(this@OTPActivity)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        btnSubmit.setOnClickListener {
            if(etOTP.text.isEmpty()||etConfirmPasswordOTP.text.isEmpty()||etNewPassword.text.isEmpty()){
                Toast.makeText(this@OTPActivity,
                    "Please enter all the credentials first",
                    Toast.LENGTH_SHORT)
                    .show()
            } else{
                progressBarOTP.visibility = View.VISIBLE
                val otp = etOTP.text.toString()
                val password = etNewPassword.text.toString()
                val mobileNumber = mobileNumberIntent

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)
                jsonParams.put("otp", otp)

                if (ConnectionManager().checkConnectivity(this@OTPActivity)) {
                    val jsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                progressBarOTP.visibility = View.GONE
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){
                                    val intent =Intent(this@OTPActivity, LoginActivity::class.java)
                                    sharedPreferences.edit().clear().apply()
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this@OTPActivity,
                                        "Password has successfully changed.",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                } else{
                                    Toast.makeText(this@OTPActivity,
                                        "Error, password reset unsuccessful. Please enter correct otp ($it)",
                                        Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this@OTPActivity,
                                    "Some unexpected error occurred!",
                                    Toast.LENGTH_SHORT)
                                    .show()
                                Log.e("CREATION", e.toString())
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this@OTPActivity, "Some error occurred!", Toast.LENGTH_SHORT)
                                .show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "5ed071d6b72b56"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@OTPActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit App") { text, listener ->
                        ActivityCompat.finishAffinity(this@OTPActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

        }
    }
}