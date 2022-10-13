package com.siddiq.woopie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
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
import com.siddiq.woopie.adapter.HomeRecyclerAdapter
import com.siddiq.woopie.model.Restaurant
import com.siddiq.woopie.util.ConnectionManager
import org.json.JSONException

class HomeFragment : Fragment() {
    lateinit var homeRecyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerViewAdapter: HomeRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    var restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        homeRecyclerView = view.findViewById(R.id.homeRecyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val restaurantArray = data.getJSONArray("data")
                            for (i in 0 until restaurantArray.length()) {
                                val restaurantJsonObject = restaurantArray.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restaurantInfoList.add(restaurantObject)

                                    recyclerViewAdapter =
                                        HomeRecyclerAdapter(activity as Context, restaurantInfoList)

                                    homeRecyclerView.adapter = recyclerViewAdapter
                                    homeRecyclerView.layoutManager = layoutManager


                            }
                        } else {
                            Toast.makeText(activity as Context,
                                "Some error occurred!",
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                        println(e.message)
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
        } else{
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