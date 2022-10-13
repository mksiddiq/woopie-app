package com.siddiq.woopie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.siddiq.woopie.R
import com.siddiq.woopie.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavouritesRecyclerAdapter(val context: Context, val restaurantList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouritesRecyclerAdapter.FavouritesViewHolder>() {

    class FavouritesViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtRestaurantNameFav: TextView = view.findViewById(R.id.txtRestaurantNameFav)
        val txtRestaurantRatingFav: TextView = view.findViewById(R.id.txtRestaurantRatingFav)
        val txtRestaurantPriceFav: TextView = view.findViewById(R.id.txtRestaurantPriceFav)
        val btnFavouritesFav: ImageView = view.findViewById(R.id.btnFavouritesFav)
        val imgRestaurantImageFav: ImageView = view.findViewById(R.id.imgRestaurantImageFav)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favourites_single_row, parent, false)

        return FavouritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val restaurant = restaurantList[position]

        holder.txtRestaurantNameFav.text = restaurant.restaurantName
        holder.txtRestaurantRatingFav.text = restaurant.restaurantRating
        holder.txtRestaurantPriceFav.text = restaurant.restaurantPrice
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurant)
            .into(holder.imgRestaurantImageFav)
        holder.btnFavouritesFav.setImageResource(R.drawable.ic_favourite_pink_fill)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }
}