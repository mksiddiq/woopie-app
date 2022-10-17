package com.siddiq.woopie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siddiq.woopie.R
import com.siddiq.woopie.model.Order
import com.siddiq.woopie.model.OrderHistoryDish

class OrderHistoryFoodItemsAdapter(
    val context: Context,
    val orderHistoryList: List<Order>,
) : RecyclerView.Adapter<OrderHistoryFoodItemsAdapter.OrderHistoryFoodItemsViewHolder>() {

    class OrderHistoryFoodItemsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDishNameOrderHistoryFood: TextView =
            view.findViewById(R.id.txtDishNameOrderHistoryFood)
        val txtDishPriceOrderHistoryFood: TextView =
            view.findViewById(R.id.txtDishPriceOrderHistoryFood)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): OrderHistoryFoodItemsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycelr_order_history_food_item_single_row, parent, false)

        return OrderHistoryFoodItemsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderHistoryFoodItemsViewHolder,
        position: Int,
    ) {
        val orderHistoryList = orderHistoryList[position]

        holder.txtDishNameOrderHistoryFood.text = orderHistoryList.orderName
        holder.txtDishPriceOrderHistoryFood.text = orderHistoryList.orderPrice
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }
}