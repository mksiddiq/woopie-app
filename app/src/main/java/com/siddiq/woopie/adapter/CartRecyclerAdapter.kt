package com.siddiq.woopie.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siddiq.woopie.R
import com.siddiq.woopie.database.OrderEntity
import org.w3c.dom.Text

class CartRecyclerAdapter(val context: Context, val orderList: List<OrderEntity>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtDishNameCart: TextView = view.findViewById(R.id.txtDishNameCart)
        val txtDishPriceCart: TextView = view.findViewById(R.id.txtDishPriceCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val order = orderList[position]

        holder.txtDishNameCart.text = order.orderName
        holder.txtDishPriceCart.text = "Rs. " + order.orderPrice
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

}