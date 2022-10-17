package com.siddiq.woopie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.siddiq.woopie.R
import com.siddiq.woopie.fragment.HomeFragment
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignUp)
        etPassword = findViewById(R.id.etPassword)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        sharedPreferences = getSharedPreferences(getString(R.string.woopie_shared_preferences), Context.MODE_PRIVATE)


        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if(isLoggedIn){
            val intent = Intent(this@LoginActivity, NavigationDrawerActivity::class.java)
            startActivity(intent)
            finish()
        }

        val queue = Volley.newRequestQueue(this@LoginActivity)
        val url = "http://13.235.250.119/v2/login/fetch_result"



        btnLogin.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobileNumber)
            jsonParams.put("password", password)

            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
            if (etMobileNumber.text.isEmpty() || etPassword.text.isEmpty()) {
                Toast.makeText(this@LoginActivity,
                    "Please enter all the credentials!",
                    Toast.LENGTH_SHORT).show()
            } else {
                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(Request.Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    if (success) {
                                        val userDetails = data.getJSONObject("data")
                                        val userName = userDetails.getString("name")
                                        val userEmail = userDetails.getString("email")
                                        val userMobileNumber =
                                            userDetails.getString("mobile_number")
                                        val userAddress = userDetails.getString("address")
                                        val userId = userDetails.getString("user_id")
                                        sharedPreferences.edit().putString("user_id", userId)
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_name", userName)
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_email", userEmail)
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_mobile_number", userMobileNumber)
                                            .apply()
                                        sharedPreferences.edit()
                                            .putString("user_address", userAddress)
                                            .apply()
                                        val intent =
                                            Intent(this@LoginActivity,
                                                NavigationDrawerActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this@LoginActivity,
                                            "Invalid credentials/ register first!",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(this@LoginActivity,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "5ed071d6b72b56"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                } else{
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit App") { text, listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }


        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        txtSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }

        supportActionBar?.title = "Login"
    }
}