package com.nikhilchauhan.friendlycontact

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.google.common.base.Enums.getField
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_phone_number_login.*
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PhoneNumberLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhoneNumberLoginFragment : Fragment() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    lateinit var verificationCode: String

    // [END declare_auth]

    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_number_login, container, false)
    }

    companion object {
        private const val TAG = "PhoneAuthActivity"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = Firebase.auth


        text_input_phone_1.setEndIconOnClickListener {

            val currentUser = auth.currentUser
            login()

            if (currentUser != null) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_phoneNumberLoginFragment_to_mobileRegisterFragment)

            }
        }



        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            // [START sign_in_with_phone]
            private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            val user = task.result?.user
                            updateUI(user)
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                            // Update UI
                        }
                    }
            }

            // [END sign_in_with_phone]
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                verificationCode=verificationId
                resendToken = token
                Log.d(TAG, "onCodeSent: " + verificationId)
                val currentUser = auth.currentUser

                if (currentUser != null) {
                    updateUI(currentUser)
                }
                otp_et.let {
                    if (it.text.toString().isNotEmpty()) {
                        verifyPhoneNumberWithCode(verificationId, otp_et?.text.toString())

                    }

                }
            }
        }
        // [END phone_auth_callbacks]

        otp_signin_button.setOnClickListener {
            val currentUser = auth.currentUser
//            login()

            if (currentUser != null) {
                Navigation.findNavController(it)
                    .navigate(R.id.action_phoneNumberLoginFragment_to_mobileRegisterFragment)

            }

            otp_et.let {
                if (it.text.toString().isNotEmpty()) {
                    if(this::verificationCode.isInitialized) {
                        verifyPhoneNumberWithCode(verificationCode, otp_et?.text.toString())
                    }

                }

            }
        }


    }

    private fun login() {

        var number = phone_number_et.text.toString().trim()

        if (!number.isEmpty()) {
            number = "+91" + number
            startPhoneNumberVerification(number)
        } else {
            Toast.makeText(context, "Enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        Toast.makeText(context, "Verification successful " + credential, Toast.LENGTH_SHORT).show()

    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END start_phone_auth]
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser? = auth.currentUser) {
        Toast.makeText(context, "Verification successful " + user?.metadata, Toast.LENGTH_SHORT)
            .show()

    }

    override fun onDestroy() {
        super.onDestroy()
        signOut()
    }

    private fun signOut() {
        // [START auth_sign_out]
        Firebase.auth.signOut()
        // [END auth_sign_out]
    }


}