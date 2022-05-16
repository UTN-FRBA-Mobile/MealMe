package com.android.mealme.fragments.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.databinding.FragmentLoginBinding
import java.lang.Exception

class LoginFragment : Fragment() {


    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        viewModel.isLoading.observe(activity as MainActivity) { isLoading ->
            binding.loading.visibility = if(isLoading == true) ProgressBar.VISIBLE else ProgressBar.VISIBLE
        }

        binding.username.addTextChangedListener {
            viewModel.email.value = it.toString()
            val passwordValue: String = try { viewModel.password.value!! } catch (e: Exception){ "" }
            if(it.toString().isEmpty() && passwordValue.isEmpty()){
                binding.login.setEnabled(false)
            }else if(it.toString().isNotEmpty()){
                binding.login.setEnabled(true)
            }
        }
        binding.password.addTextChangedListener {
            viewModel.password.value = it.toString()

            val emailValue = try { viewModel.email.value!! } catch (e: Exception){ "" }
            if(it.toString().isEmpty() && emailValue.isEmpty() ){
                binding.login.setEnabled(false)
            }else if(it.toString().isNotEmpty()){
                binding.login.setEnabled(true)
            }
        }

        binding.login.setOnClickListener {
            viewModel.login(activity as MainActivity)?.addOnSuccessListener {
                findNavController().popBackStack()
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_login_to_registerFragment)
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}