package com.siddiq.woopie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.siddiq.woopie.R
import com.siddiq.woopie.model.Dish
import com.siddiq.woopie.model.Order
import com.siddiq.woopie.model.OrderHistoryDish
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryRecyclerAdapter(val context: Context, val orderList: List<OrderHistoryDish>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantNameOrderHistory: TextView =
            view.findViewById(R.id.txtRestaurantNameOrderHistory)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistoryItems: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_history_single_row, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderList = orderList[position]

        holder.txtRestaurantNameOrderHistory.text = orderList.resName
        holder.txtDate.text = formatDate(orderList.orderDate)
        setUpRecycler(holder.recyclerResHistoryItems, orderList)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun setUpRecycler(recyclerResHistory: RecyclerView, orderHistoryList: OrderHistoryDish) {
        val foodItemsList = ArrayList<Order>()
        for (i in 0 until orderHistoryList.foodItem.length()) {
            val foodJson = orderHistoryList.foodItem.getJSONObject(i)
            foodItemsList.add(
                Order(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val cartItemAdapter = OrderHistoryFoodItemsAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        recyclerResHistory.layoutManager = mLayoutManager
        recyclerResHistory.adapter = cartItemAdapter
    }

    private fun formatDate(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date

        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}