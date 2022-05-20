package com.android.mealme.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
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

        binding.email.editText?.doAfterTextChanged {
            viewModel.email.value = it.toString()
            if(!isValidEmail(it))
                binding.email.error = getResources().getString(R.string.errorEmail)
            else
                binding.email.error = ""

            binding.login.setEnabled(viewModel.enableLogin())
        }
        binding.password.editText?.doAfterTextChanged {
            viewModel.password.value = it.toString()
            binding.login.setEnabled(viewModel.enableLogin())
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

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}