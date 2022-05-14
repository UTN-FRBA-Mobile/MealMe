package com.android.mealme.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.mealme.data.adapter.RestaurantAdapter
import com.android.mealme.data.model.ResponseApiModel
import com.android.mealme.data.service.RestaurantService
import com.android.mealme.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var _binding: FragmentHomeBinding? = null
    private lateinit var restaurantAdapter: RestaurantAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val service = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create()) // Para parsear automágicamente el json
        .baseUrl("https://run.mocky.io/v3/eaa7e2aa-8bd6-4954-b7cb-0b166862c529/")
        .build()
        .create(RestaurantService::class.java) // la interfaz que diseñaron antes


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        TEXTO INICIAL DE PRUEBA
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restaurantAdapter = RestaurantAdapter(listener)
        binding.list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = restaurantAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onStart() {
        super.onStart()
        // Simulamos un request
        binding.list.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        service.listRestaurants().enqueue(object:
            Callback<ResponseApiModel> {
            override fun onResponse(call: Call<ResponseApiModel>, response: Response<ResponseApiModel>) {
                // pasar los tweets al tweetAdapter
                if (response.isSuccessful){
                    response.body()?.restaurants?.let { restaurantAdapter.setRestaurants(it) }
                    binding.list.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    //TEXTO INICIAL DE PRUEBA
//                    binding.textHome.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ResponseApiModel>, error: Throwable) {
                Toast.makeText(activity, "No tweets founds!", Toast.LENGTH_SHORT).show()
            }
        })

        //Handler().postDelayed({
        //
        //    binding.list.visibility = View.VISIBLE
        //    binding.progressBar.visibility = View.GONE
        //}, 1000)
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainFragment.
         */
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}