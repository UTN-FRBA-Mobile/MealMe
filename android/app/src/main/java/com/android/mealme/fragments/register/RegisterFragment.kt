package com.android.mealme.fragments.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.mealme.MainActivity
import com.android.mealme.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth

    private val viewModel: RegisterViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var loader: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var email = binding.registerEmail
        var password = binding.registerPassword
        loader = binding.registerLoader

        email.addTextChangedListener { _email ->
            viewModel.setEmail(_email.toString())
        }
        password.addTextChangedListener { _password ->
            viewModel.setPassword(_password.toString())
        }

        viewModel.isLoading.observe(activity as MainActivity, Observer { loading ->
            if(loading){
                loader.visibility = LinearLayout.VISIBLE
            }else {
                loader.visibility = LinearLayout.GONE
            }

        })

        binding.registerActionButton.setOnClickListener {
            register()
        }
    }

    fun register() {
        loader.visibility = LinearLayout.VISIBLE
        viewModel.register(activity as MainActivity)?.addOnSuccessListener {
            findNavController().popBackStack()
//            findNavController()?.navigate(R.id.nav_home)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }
}