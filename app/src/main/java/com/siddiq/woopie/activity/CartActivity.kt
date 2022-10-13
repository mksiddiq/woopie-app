package com.siddiq.woopie.activity

import android.content.Context
import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Index
import androidx.room.Room
import com.siddiq.woopie.R
import com.siddiq.woopie.adapter.CartRecyclerAdapter
import com.siddiq.woopie.database.OrderDatabase
import com.siddiq.woopie.database.OrderEntity
import com.siddiq.woopie.database.RestaurantDatabase
import com.siddiq.woopie.database.RestaurantEntity
import java.lang.AssertionError

class CartActivity : AppCompatActivity() {
    lateinit var progressBarCart: ProgressBar
    lateinit var progressLayoutCart: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerViewCart: RecyclerView
    lateinit var recyclerViewAdapter: CartRecyclerAdapter
    lateinit var btnPlaceOrder: Button

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

        dbOrderList = RetrieveOrders(this@CartActivity).execute().get()

        if (dbOrderList != null) {
            progressLayoutCart.visibility = View.GONE
            recyclerViewAdapter = CartRecyclerAdapter(this@CartActivity, dbOrderList)
            recyclerViewCart.layoutManager = layoutManager
            recyclerViewCart.adapter = recyclerViewAdapter
        }

        val price = GetOrderTotal(this@CartActivity).execute().get()

        btnPlaceOrder.text = "place order (Total Rs. $price)"

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

