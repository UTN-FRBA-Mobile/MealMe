package com.android.mealme.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.android.mealme.R
import com.android.mealme.data.model.Restaurant
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class RestaurantHeaderImage @JvmOverloads
constructor(
    context: Context,
    attributeSet: AttributeSet,
    defStyleAtte: Int = 0
) : CollapsingToolbarLayout(context, attributeSet, defStyleAtte) {

    private var rootView: CollapsingToolbarLayout? = null


    init {
        rootView =
            inflate(context, R.layout.restaurant_image_header, this) as CollapsingToolbarLayout
        rootView?.findViewById<ProgressBar>(R.id.restaurant_header_image_loader)?.isVisible = true

    }

    fun setRestaurant(restaurant: Restaurant) {
        val imageView = rootView?.findViewById<ImageView>(R.id.restaurant_header_image_image)
        val loader = rootView?.findViewById<ProgressBar>(R.id.restaurant_header_image_loader)


        Picasso.get().load(restaurant?.logo_photos?.first())
            .into(imageView, object : Callback {
                override fun onSuccess() {
                    loader?.isVisible = false
                }

                override fun onError(e: Exception?) {
                    loader?.isVisible = false
                }
            })
    }


    fun setupForIncludedCollapsible(
        root: View,
        restaurant: Restaurant,
        extraHandler: ExtraConfigurationsHandler? = null
    ): Toolbar {
        val toolbar: Toolbar = root.findViewById<Toolbar>(Constants.TOOLBAR_ID)
//        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        root.findViewById<CollapsingToolbarLayout>(Constants.COLLAPSIBLE_TOOLBAR_ID).title =
            restaurant.name

        setRestaurant(restaurant)

        if (extraHandler != null) {
            if (extraHandler.getMenuId() != null) toolbar.inflateMenu(extraHandler.getMenuId()!!)
            toolbar.setNavigationOnClickListener { extraHandler.onClickBack() }
        }

        return toolbar
    }


    object Constants {
        const val COMPONENT_ID: Int = R.id.restaurant_image_header_image
        const val TOOLBAR_ID: Int = R.id.restaurant_image_toolbar
        const val COLLAPSIBLE_TOOLBAR_ID: Int = R.id.restaurant_image_collapsible_toolbar
        const val APP_BAR_LAYOUT_ID: Int = R.id.restaurant_image_appbar_layour
    }

    interface ExtraConfigurationsHandler {
        fun getMenuId(): Int? = null
        fun onClickBack(): Unit? = null
    }
}