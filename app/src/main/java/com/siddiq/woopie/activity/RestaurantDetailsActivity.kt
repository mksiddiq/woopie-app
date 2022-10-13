package com.siddiq.woopie.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.siddiq.woopie.R
import com.siddiq.woopie.adapter.RestaurantDetailsRecyclerAdapter
import com.siddiq.woopie.database.OrderDatabase
import com.siddiq.woopie.database.OrderEntity
import com.siddiq.woopie.model.Dish
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONException

class RestaurantDetailsActivity : AppCompatActivity() {
    lateinit var resDetailsRecyclerView: RecyclerView
    lateinit var progressLayoutDetails: RelativeLayout
    lateinit var progressBarDetails: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerViewAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var btnProceedToCart: Button
    //lateinit var sharedPreferences: SharedPreferences

    var restaurantId: String? = "100"

    var dishList = arrayListOf<Dish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        supportActionBar?.title = "Menu"

        progressBarDetails = findViewById(R.id.progressBarDetails)
        progressLayoutDetails = findViewById(R.id.progressLayoutDetails)
        resDetailsRecyclerView = findViewById(R.id.resDetailsRecyclerView)
        btnProceedToCart= findViewById(R.id.btnProceedToCart)


        layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)

        if (intent != null) {
            restaurantId = intent.getStringExtra("restaurant_id")
        } else {
            finish()
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)) {
            val jsonObjectRequest = object :
                JsonObjectRequest(Request.Method.GET, url + restaurantId, null, Response.Listener {
                    try {
                        //btnAddToCart.visibility = View.INVISIBLE
                       /* if(sharedPreferences.getBoolean("btnAddClicked", false)){
                            btnAddToCart.visibility = View.VISIBLE
                        }*/
                        progressLayoutDetails.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val dishArray = data.getJSONArray("data")
                            for (i in 0 until dishArray.length()) {
                                val dishJsonObject = dishArray.getJSONObject(i)
                                val dishObject = Dish(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one"),
                                    dishJsonObject.getString("restaurant_id")
                                )
                                dishList.add(dishObject)

                                recyclerViewAdapter =
                                    RestaurantDetailsRecyclerAdapter(this@RestaurantDetailsActivity,
                                        dishList)

                                resDetailsRecyclerView.adapter = recyclerViewAdapter
                                resDetailsRecyclerView.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(this@RestaurantDetailsActivity,
                                "Some error occurred!",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantDetailsActivity,
                            "Some Unexpected Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {

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
            val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
            }
            dialog.create()
            dialog.show()
        }

        //sharedPreferences = getSharedPreferences("woopie_shared_preferences", Context.MODE_PRIVATE)
        //val isOrderListEmpty = sharedPreferences.getBoolean("isOrderListEmpty", false)

        /*if(isOrderListEmpty){
            btnProceedToCart.visibility = View.GONE
        } else{
            btnProceedToCart.visibility = View.VISIBLE
        }*/

        btnProceedToCart.setOnClickListener {
            val intent = Intent(this@RestaurantDetailsActivity, CartActivity::class.java)
            startActivity(intent)
        }

    }

    class DeleteOrdersFromDatabase(val context: Context): AsyncTask<Void, Void, Unit>(){
        override fun doInBackground(vararg p0: Void?): Unit {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()
            return db.orderDao().deleteAllOrders()
        }

    }


    override fun onBackPressed() {
        DeleteOrdersFromDatabase(this@RestaurantDetailsActivity).execute().get()
        super.onBackPressed()
    }
}