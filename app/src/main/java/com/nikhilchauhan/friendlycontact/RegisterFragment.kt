package com.nikhilchauhan.friendlycontact

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        view.login_text_view.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_emailLoginFragment)

        }

        return view


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth

        register_button.setOnClickListener {

            if (email_address_register.text?.trim().toString()
                    .isNotEmpty() || password_register.text?.trim().toString()
                    .isNotEmpty() || full_name_register.text?.trim().toString().isNotEmpty()
            ) {
                createUser(
                    email_address_register.text?.trim().toString(),
                    password_register.text?.trim().toString()
                )
            }
            if (email_address_register.text?.trim().toString()
                    .isEmpty() || full_name_register.text?.trim().toString()
                    .isEmpty() || password_register.text?.trim().toString()
                    .isEmpty() || confirm_password.text?.trim().toString().isEmpty()
            ) {

                text_input_3.error = "Enter valid password"
                text_input_1.error = "Enter valid name"
                text_input_2.error = "Enter valid email address"
                text_input_4.error = "Enter valid password"
                Toast.makeText(context, "Please enter valid email and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            text_input_3.error = null
            text_input_2.error = null
            text_input_1.error = null
            text_input_4.error = null
        }


    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        Toast.makeText(context, "You are already logged in", Toast.LENGTH_SHORT).show()

    }

    fun createUser(email: String, password: String) {
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("success", "createUser: " + "task successful")
                    Toast.makeText(
                        context, "Register Successful------" + task.exception.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Log.d("failed", "createUser: " + "task failed" + task.exception)
                    Toast.makeText(
                        context,
                        "Register UnSuccessful------" + task.result.toString(),
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }

    }
}