package com.android.mealme.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.mealme.R
import com.android.mealme.data.model.RestaurantReview


class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    private var reviews: List<RestaurantReview> = emptyList()

    override fun getItemCount(): Int = reviews.size
    override fun getItemViewType(position: Int): Int = R.layout.item_review

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviews[position]
        val itemView = holder.itemView

        itemView.findViewById<RatingBar>(R.id.item_review_score).rating = review.score.toFloat()
        itemView.findViewById<TextView>(R.id.item_review_message).text = review.message
    }

    fun setReviews(reviews: List<RestaurantReview>){
        this.reviews = reviews
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {}

    companion object {
        fun init(list: RecyclerView, context: Context): ReviewsAdapter {
            val _adapter = ReviewsAdapter()
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = _adapter
            }

            return _adapter
        }
    }
}