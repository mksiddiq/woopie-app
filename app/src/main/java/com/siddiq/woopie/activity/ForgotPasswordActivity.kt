package com.siddiq.woopie.activity

import android.app.AlertDialog
import android.content.Intent
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
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumberFP: EditText
    lateinit var etEmailFP: EditText
    lateinit var btnNext: Button
    lateinit var progressBarFP: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        supportActionBar?.title = "Forgot Password"

        etMobileNumberFP = findViewById(R.id.etMobileNumberFP)
        etEmailFP = findViewById(R.id.etEmailFP)
        btnNext = findViewById(R.id.btnNext)
        progressBarFP = findViewById(R.id.progressBarFP)

        progressBarFP.visibility = View.GONE

        val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

        btnNext.setOnClickListener {
            if (etMobileNumberFP.text.isEmpty() || etEmailFP.text.isEmpty()) {
                Toast.makeText(this@ForgotPasswordActivity,
                    "Please enter all the credentials first",
                    Toast.LENGTH_SHORT).show()
            } else {
                //btnNext.visibility = View.GONE
                progressBarFP.visibility = View.VISIBLE

                val mobileNumber = etMobileNumberFP.text.toString()
                val email = etEmailFP.text.toString()

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("email", email)

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Request.Method.POST,
                            url,
                            jsonParams,
                            Response.Listener {
                                try {
                                    progressBarFP.visibility = View.GONE
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")
                                    val firstTry = data.getBoolean("first_try")
                                    if (success) {
                                        if (firstTry) {
                                            val intent = Intent(this@ForgotPasswordActivity,
                                                OTPActivity::class.java)
                                            intent.putExtra("mobileNumber", mobileNumber)
                                            Toast.makeText(this@ForgotPasswordActivity,
                                                "OTP sent to registered email",
                                                Toast.LENGTH_SHORT).show()
                                            startActivity(intent)
                                        } else {
                                            val intent = Intent(this@ForgotPasswordActivity,
                                                OTPActivity::class.java)
                                            intent.putExtra("mobileNumber", mobileNumber)
                                            Toast.makeText(this@ForgotPasswordActivity,
                                                "Please use the previously sent OTP to reset password",
                                                Toast.LENGTH_SHORT).show()
                                            startActivity(intent)
                                        }
                                    } else {
                                        Toast.makeText(this@ForgotPasswordActivity,
                                            "User not found",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(this@ForgotPasswordActivity,
                                        "Some unexpected error occurred",
                                        Toast.LENGTH_SHORT).show()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(this@ForgotPasswordActivity,
                                    "Some error occurred!",
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
                } else {
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit App") { text, listener ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

        }
    }
}