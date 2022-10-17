package com.siddiq.woopie.adapter

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.siddiq.woopie.R
import com.siddiq.woopie.database.OrderDatabase
import com.siddiq.woopie.database.OrderEntity
import com.siddiq.woopie.model.Dish
import android.app.Activity as Activity1
import com.siddiq.woopie.activity.RestaurantDetailsActivity as RestaurantDetailsActivity1
import com.siddiq.woopie.activity.RestaurantDetailsActivity as RestaurantDetailsActivity2

class RestaurantDetailsRecyclerAdapter(val context: Context, val dishList: List<Dish>, val button: Button) :
    RecyclerView.Adapter<RestaurantDetailsRecyclerAdapter.RestaurantDetailsViewHolder>() {
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnProceedToCart: Button

    class RestaurantDetailsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtSerialNumber: TextView = view.findViewById(R.id.txtSerialNumber)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantDetailsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_res_details_single_row, parent, false)

        return RestaurantDetailsViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantDetailsViewHolder, position: Int) {
        val dish = dishList[position]
        val number = position + 1
        sharedPreferences =
            context.getSharedPreferences(context.resources.getString(R.string.woopie_shared_preferences),
                Context.MODE_PRIVATE)

        holder.txtDishName.text = dish.dishName
        holder.txtDishPrice.text = "Rs. " + dish.dishPrice
        holder.txtSerialNumber.text = "$number"

        val orderId = dish.dishId
        val orderEntity = OrderEntity(
            orderId.toInt() as Int,
            dish.dishName,
            dish.dishPrice
        )
        btnProceedToCart = button
        holder.btnAdd.setOnClickListener {

            if (!DBAsyncTaskDetails(context, orderEntity, 1).execute().get()) {
                val async = DBAsyncTaskDetails(context, orderEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Order added to cart!", Toast.LENGTH_SHORT)
                        .show()
                    val btnColor = ContextCompat.getColor(context, R.color.app_purple_color)
                    holder.btnAdd.setBackgroundColor(btnColor)
                    holder.btnAdd.text = "remove"
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val async = DBAsyncTaskDetails(context, orderEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Order deleted to cart!", Toast.LENGTH_SHORT)
                        .show()
                    val btnColor = ContextCompat.getColor(context, R.color.app_background_color)
                    holder.btnAdd.setBackgroundColor(btnColor)
                    holder.btnAdd.text = "Add"
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            val dbOrderList = CheckDbEmpty(context).execute().get()
            if(dbOrderList.isEmpty()){
                //sharedPreferences.edit().putBoolean("isOrderListEmpty", true).apply()
                btnProceedToCart.visibility = View.GONE

            } else {
                //sharedPreferences.edit().putBoolean("isOrderListEmpty", false).apply()
                btnProceedToCart.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return dishList.size
    }
}

class DBAsyncTaskDetails(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {

    /*
    Mode 1 -> Check the DB if a book is added to favourites or not
    Mode 2 -> Save the book into DB as favourites
    Mode 3 -> Remove a book into DB as favourites
    * */

    val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        when (mode) {
            1 -> {
                //Check the DB if a book is added to favourites or not
                val order: OrderEntity? =
                    db.orderDao().getOrderById(orderEntity.order_id.toString())
                db.close()
                return order != null

            }
            2 -> {
                //Save the book into DB as favourites
                db.orderDao().insertOrder(orderEntity)
                db.close()
                return true

            }
            3 -> {
                //Remove a book into DB as favourites
                db.orderDao().deleteOrder(orderEntity)
                db.close()
                return true

            }
        }

        return false

    }

}

class CheckDbEmpty(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>() {
    override fun doInBackground(vararg p0: Void?): List<OrderEntity> {
        val db = Room.databaseBuilder(context, OrderDatabase::class.java, "orders-db").build()
        val list = db.orderDao().getAllOrders()
        return list
    }
}

