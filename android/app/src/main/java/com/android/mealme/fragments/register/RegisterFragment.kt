package com.android.mealme.fragments.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.mealme.MainActivity
import com.android.mealme.R
import com.android.mealme.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar
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
        setHasOptionsMenu(true);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.loginButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var email = binding.registerEmail
        var password = binding.registerPassword
        var repeat = binding.repeatPassword
        loader = binding.registerLoader

        var progress = 0
        email.editText?.doAfterTextChanged { _email ->
            viewModel.setEmail(_email.toString())
            if(!isValidEmail(_email))
                email.error = getResources().getString(R.string.errorEmail)
            else
                email.error = ""

        }
        password.editText?.doAfterTextChanged { _password ->
            viewModel.setPassword(_password.toString())
            if(_password.toString().isNullOrEmpty())
                password.error = getResources().getString(R.string.errorPassword)
            else
                password.error = ""
        }
        repeat.editText?.doAfterTextChanged { _repeat ->
            viewModel.setRepeatPassword(_repeat.toString())
            if(!(password.editText?.text.toString().equals(_repeat.toString()))!!)
                repeat.error = getResources().getString(R.string.errorRepeat)
            else
                repeat.error = ""

        }

        viewModel.isLoading.observe(activity as MainActivity, Observer { loading ->
            loader.visibility = if(loading){LinearLayout.VISIBLE} else {LinearLayout.GONE}

        })

        binding.registerActionButton.setOnClickListener { register() }

    }

    fun register() {
        if(viewModel.validatePasswordRepeat()){
            loader.visibility = LinearLayout.VISIBLE
            viewModel.register(activity?.findViewById(R.id.register_layout)!!,
                resources, requireContext())?.addOnSuccessListener {
                findNavController().popBackStack()
            }
        }else{
            Snackbar.make(activity?.findViewById(R.id.busquedaLayout)!!,
                resources.getString(R.string.errorRepeatMsg), Snackbar.LENGTH_LONG)
                .show();
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = RegisterFragment()
    }

    fun isValidEmail(target: CharSequence?): Boolean {
        return if (TextUtils.isEmpty(target)) {
            false
        } else {
            Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_login)
        item?.isVisible = false
    }

}