package com.siddiq.woopie.adapter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.siddiq.woopie.R
import com.siddiq.woopie.activity.RestaurantDetailsActivity
import com.siddiq.woopie.database.RestaurantDatabase
import com.siddiq.woopie.database.RestaurantEntity
import com.siddiq.woopie.model.Restaurant
import com.squareup.picasso.Picasso
import java.util.zip.Inflater

class HomeRecyclerAdapter(val context: Context, val itemList: List<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    lateinit var sharedPreferences: SharedPreferences

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantPrice: TextView = view.findViewById(R.id.txtRestaurantPrice)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val btnFavourites: ImageView = view.findViewById(R.id.btnFavourites)
        val imgRestaurantImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantPrice.text = "₹ " + restaurant.restaurantPrice + "/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.restaurant)
            .into(holder.imgRestaurantImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.restaurantId)
            sharedPreferences =
                context.getSharedPreferences(context.resources.getString(R.string.woopie_shared_preferences),
                    Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("restaurant_name", restaurant.restaurantName).apply()
            sharedPreferences.edit().putString("restaurant_id", restaurant.restaurantId).apply()
            context.startActivity(intent)
        }

        val restaurantId = itemList[position].restaurantId

        val restaurantEntity = RestaurantEntity(
            restaurantId.toInt() as Int,
            holder.txtRestaurantName.text.toString(),
            holder.txtRestaurantRating.text.toString(),
            holder.txtRestaurantPrice.text.toString(),
            holder.imgRestaurantImage.toString()
        )
        val isFav = DBAsyncTask(context, restaurantEntity, 1).execute().get()

        if (isFav) {
            holder.btnFavourites.setImageResource(R.drawable.ic_favourite_pink_fill)
        } else {
            holder.btnFavourites.setImageResource(R.drawable.ic_favourite_pink)
        }


        holder.btnFavourites.setOnClickListener {

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant added to favourites!", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnFavourites.setImageResource(R.drawable.ic_favourite_pink_fill)
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context,
                        "Restaurant removed from favourites!",
                        Toast.LENGTH_SHORT)
                        .show()
                    holder.btnFavourites.setImageResource(R.drawable.ic_favourite_pink)
                } else {
                    Toast.makeText(context, "Some error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}

class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
    AsyncTask<Void, Void, Boolean>() {

    /*
    Mode 1 -> Check the DB if a book is added to favourites or not
    Mode 2 -> Save the book into DB as favourites
    Mode 3 -> Remove a book into DB as favourites
    * */

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

    override fun doInBackground(vararg p0: Void?): Boolean {

        when (mode) {
            1 -> {
                //Check the DB if a book is added to favourites or not
                val restaurant: RestaurantEntity? =
                    db.restaurantDao().getRestaurantById(restaurantEntity.restaurant_id.toString())
                db.close()
                return restaurant != null

            }
            2 -> {
                //Save the book into DB as favourites
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true

            }
            3 -> {
                //Remove a book into DB as favourites
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true

            }
        }

        return false

    }

}