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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_email_login.*
import kotlinx.android.synthetic.main.fragment_email_login.view.*
import kotlinx.android.synthetic.main.fragment_register.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [EmailLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmailLoginFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_login, container, false)

        view.register_tv_email_login.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_emailLoginFragment_to_registerFragment)

        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth

        email_login_button.setOnClickListener {


            if (email_login_et.text?.trim().toString()
                    .isNotEmpty() || email_login_password.text?.trim().toString()
                    .isNotEmpty()
            ) {
                val signInSuccessful=signInUser(
                    email_login_et.text?.trim().toString(),
                    email_login_password.text?.trim().toString()
                )

                if(signInSuccessful==true){
                    Navigation.findNavController(it).navigate(R.id.action_emailLoginFragment_to_messageFragment)

                }
            }
            if (email_login_et.text?.trim().toString()
                    .isEmpty() || email_login_password.text?.trim().toString().isEmpty()
            ) {


                email_text_input_1.error = "Enter valid email address"
                email_text_input_2.error = "Enter valid password"

                Toast.makeText(context, "Please enter valid email and password", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            email_text_input_2.error = null
            email_text_input_1.error = null
        }

    }

    fun signInUser(email: String, password: String):Boolean {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInUser: " + task.result.toString())
                    Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Sign in unsuccessful", Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "signInUser: unsuccessful " + task.exception.toString())
                    return@addOnCompleteListener
                }
            }
        return true
    }
}


