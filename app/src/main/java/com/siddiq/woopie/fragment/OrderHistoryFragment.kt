package com.siddiq.woopie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.siddiq.woopie.R
import com.siddiq.woopie.activity.LoginActivity
import com.siddiq.woopie.adapter.OrderHistoryRecyclerAdapter
import com.siddiq.woopie.model.OrderHistoryDish
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONException
import androidx.recyclerview.widget.RecyclerView.LayoutManager as La

class OrderHistoryFragment : Fragment() {
    lateinit var llHasOrders: LinearLayout
    lateinit var rlHasNoOrders: RelativeLayout
    lateinit var progressLayoutOrderHistory: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    lateinit var orderHistoryRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerViewAdapter: OrderHistoryRecyclerAdapter


    val orderHistoryList = arrayListOf<OrderHistoryDish>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        llHasOrders = view.findViewById(R.id.llHasOrders)
        rlHasNoOrders = view.findViewById(R.id.rlHasNoOrders)
        progressLayoutOrderHistory = view.findViewById(R.id.progressLayoutOrderHistory)
        orderHistoryRecyclerView = view.findViewById(R.id.orderHistoryRecyclerView)
        sharedPreferences =
            (activity as Context).getSharedPreferences(getString(R.string.woopie_shared_preferences),
                Context.MODE_PRIVATE)
        progressLayoutOrderHistory.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val userId = sharedPreferences.getString("user_id", null)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    progressLayoutOrderHistory.visibility = View.GONE
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val resArray = data.getJSONArray("data")
                            if (resArray.length() == 0) {
                                llHasOrders.visibility = View.GONE
                                rlHasNoOrders.visibility = View.VISIBLE
                            } else {
                                for (i in 0 until resArray.length()) {
                                    val orderObject = resArray.getJSONObject(i)
                                    val foodItems = orderObject.getJSONArray("food_items")
                                    val orderDetails = OrderHistoryDish(
                                        orderObject.getInt("order_id"),
                                        orderObject.getString("restaurant_name"),
                                        orderObject.getString("order_placed_at"),
                                        foodItems
                                    )
                                    orderHistoryList.add(orderDetails)
                                    if (orderHistoryList.isEmpty()) {
                                        llHasOrders.visibility = View.GONE
                                        rlHasNoOrders.visibility = View.VISIBLE
                                    } else {
                                        llHasOrders.visibility = View.VISIBLE
                                        rlHasNoOrders.visibility = View.GONE

                                        if (activity != null) {
                                            recyclerViewAdapter =
                                                OrderHistoryRecyclerAdapter(activity as Context,
                                                    orderHistoryList)
                                            layoutManager = LinearLayoutManager(activity as Context)

                                            orderHistoryRecyclerView.adapter = recyclerViewAdapter
                                            orderHistoryRecyclerView.layoutManager = layoutManager
                                        } else {
                                            queue.cancelAll(this::class.java.simpleName)
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(activity as Context,
                                "Some error occurred",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(activity as Context,
                            "Some unexpected error occurred",
                            Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(activity as Context,
                            "Volley error occurred!",
                            Toast.LENGTH_SHORT).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit App") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }

}