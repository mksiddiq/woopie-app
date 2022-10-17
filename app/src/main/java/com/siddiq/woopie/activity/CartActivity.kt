package com.siddiq.woopie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Index
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.siddiq.woopie.R
import com.siddiq.woopie.adapter.CartRecyclerAdapter
import com.siddiq.woopie.database.OrderDatabase
import com.siddiq.woopie.database.OrderEntity
import com.siddiq.woopie.database.RestaurantDatabase
import com.siddiq.woopie.database.RestaurantEntity
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.AssertionError

class CartActivity : AppCompatActivity() {
    lateinit var progressBarCart: ProgressBar
    lateinit var progressLayoutCart: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerViewCart: RecyclerView
    lateinit var recyclerViewAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button
    lateinit var txtRestaurantNameCart: TextView
    lateinit var sharedPreferences: SharedPreferences

    var dbOrderList = listOf<OrderEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        supportActionBar?.title = "My Cart"

        progressBarCart = findViewById(R.id.progressBarCart)
        progressLayoutCart = findViewById(R.id.progressLayoutCart)
        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerViewCart = findViewById(R.id.recyclerViewCart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtRestaurantNameCart = findViewById(R.id.txtRestaurantNameCart)
        sharedPreferences = getSharedPreferences(getString(R.string.woopie_shared_preferences),
            Context.MODE_PRIVATE)

        val restaurantCartName = sharedPreferences.getString("restaurant_name", "Restaurant Name Example")

        txtRestaurantNameCart.text = restaurantCartName

        dbOrderList = RetrieveOrders(this@CartActivity).execute().get()

        if (dbOrderList != null) {
            progressLayoutCart.visibility = View.GONE
            recyclerViewAdapter = CartRecyclerAdapter(this@CartActivity, dbOrderList)
            recyclerViewCart.layoutManager = layoutManager
            recyclerViewCart.adapter = recyclerViewAdapter
        }

        val price = GetOrderTotal(this@CartActivity).execute().get()

        btnPlaceOrder.text = "place order (Total Rs. $price)"

        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"
        val jsonParams = JSONObject()

        val userId = sharedPreferences.getString("user_id", null)
        val resId = sharedPreferences.getString("restaurant_id", null)

        jsonParams.put("user_id", userId)

        jsonParams.put("restaurant_id", resId)

        jsonParams.put("total_cost", price.toString())

        val foodArray = JSONArray()
        for (i in 0 until dbOrderList.size) {
            val foodId = JSONObject()
            foodId.put("food_item_id", dbOrderList[i].order_id)
            foodArray.put(i, foodId)
        }
        jsonParams.put("food", foodArray)

        btnPlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val intent =
                                        Intent(this@CartActivity, ConfirmationActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this@CartActivity,
                                        "Some error occurred, order has not been placed",
                                        Toast.LENGTH_SHORT).show()

                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this@CartActivity,
                                    "Some unexpected error occurred",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this@CartActivity,
                                "Some error occurred",
                                Toast.LENGTH_SHORT).show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "json/application"
                            headers["token"] = "5ed071d6b72b56"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit App") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }

        }

    }

    class RetrieveOrders(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()
            return db.orderDao().getAllOrders()
        }
    }

    class GetOrderTotal(val context: Context) : AsyncTask<Void, Void, Int>() {
        override fun doInBackground(vararg p0: Void?): Int {
            val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()
            return db.orderDao().getOrderPrice()

        }
    }

}

