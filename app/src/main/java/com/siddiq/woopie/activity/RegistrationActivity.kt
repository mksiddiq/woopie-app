package com.siddiq.woopie.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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
import java.net.Inet4Address

class RegistrationActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumberRegistration: EditText
    lateinit var etAddress: EditText
    lateinit var etPasswordRegistration: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.title = "Register Yourself"

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumberRegistration = findViewById(R.id.etMobileNumberRegistration)
        etAddress = findViewById(R.id.etAddress)
        etPasswordRegistration = findViewById(R.id.etPasswordRegistration)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        sharedPreferences = getSharedPreferences(getString(R.string.woopie_shared_preferences),
            Context.MODE_PRIVATE)

        val queue = Volley.newRequestQueue(this@RegistrationActivity)
        val url = "http://13.235.250.119/v2/register/fetch_result/"

        btnRegister.setOnClickListener {
            val jsonParams = JSONObject()
            jsonParams.put("name", etName.text.toString())
            jsonParams.put("mobile_number", etMobileNumberRegistration.text.toString())
            jsonParams.put("password", etPasswordRegistration.text.toString())
            jsonParams.put("address", etAddress.text.toString())
            jsonParams.put("email", etEmail.text.toString())

            if (etName.text.isEmpty() || etEmail.text.isEmpty() || etMobileNumberRegistration.text.isEmpty() || etAddress.text.isEmpty() || etPasswordRegistration.text.isEmpty() || etConfirmPassword.text.isEmpty()) {
                Toast.makeText(this@RegistrationActivity,
                    "Please enter all the fields first",
                    Toast.LENGTH_SHORT).show()
            } else if(etPasswordRegistration.text.toString() != etConfirmPassword.text.toString()){
                Toast.makeText(this@RegistrationActivity,
                    "Please enter the same password!",
                    Toast.LENGTH_SHORT).show()
            } else{
                if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
                    val jsonObjectRequest =
                        object :
                            JsonObjectRequest(Request.Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {
                                    try {
                                        val data = it.getJSONObject("data")
                                        val success = data.getBoolean("success")

                                        if (success) {
                                            Toast.makeText(this@RegistrationActivity,
                                                "Registration successful!",
                                                Toast.LENGTH_SHORT).show()
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
                                            val intent = Intent(this@RegistrationActivity,
                                                NavigationDrawerActivity::class.java)
                                            startActivity(intent)
                                            finish()

                                        } else {
                                            Toast.makeText(this@RegistrationActivity,
                                                "Some error occurred, registration failed!",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: JSONException) {
                                        Toast.makeText(this@RegistrationActivity,
                                            "Some unexpected error occurred",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                },
                                Response.ErrorListener {
                                    Toast.makeText(this@RegistrationActivity,
                                        "Some error occurred",
                                        Toast.LENGTH_SHORT).show()
                                }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/josn"
                                headers["token"] = "5ed071d6b72b56"
                                return headers
                            }
                        }
                    queue.add(jsonObjectRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit App") { text, listener ->
                        ActivityCompat.finishAffinity(this@RegistrationActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

    }
}