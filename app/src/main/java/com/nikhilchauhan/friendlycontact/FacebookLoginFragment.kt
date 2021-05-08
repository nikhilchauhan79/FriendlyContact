package com.nikhilchauhan.friendlycontact

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_facebook_login.*
import kotlinx.android.synthetic.main.fragment_login.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FacebookLoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FacebookLoginFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var textView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var accessTokenTracker: AccessTokenTracker
    private lateinit var accessToken:AccessToken
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var callbackManager: CallbackManager
    private lateinit var buttonFacebookLogin: LoginButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase Auth
        auth = Firebase.auth
//        accessToken = AccessToken.getCurrentAccessToken();


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_login, container, false)
    }




    private fun updateUI(currentUser: FirebaseUser?) {

        if(currentUser!=null) {
            Toast.makeText(
                context,
                "Successfully signed in" + currentUser.displayName?.toString(),
                Toast.LENGTH_SHORT
            ).show()

            Log.d(TAG, "updateUI: "+"Name: "+ currentUser.displayName+currentUser.phoneNumber+currentUser.uid+currentUser.metadata?.toString())
            val str="Name: "+ currentUser.displayName+currentUser.phoneNumber+currentUser.uid+currentUser.metadata?.toString()
            text_view.text=str
        }

    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser!=null) {
            updateUI(currentUser)
        }
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()

        auth.removeAuthStateListener(authStateListener)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

//        facebook_login_button.setReadPermissions("email", "public_profile")
        facebook_login_button.setFragment(this)
//        facebook_login_button.setReadPermissions("email", "public_profile")
        facebook_login_button.setPermissions("email", "public_profile")
        facebook_login_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })
        // ...

        authStateListener=FirebaseAuth.AuthStateListener {
            val user:FirebaseUser?=it.currentUser
            updateUI(user)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d(TAG, "updateUI: "+"Name: "+ user?.displayName+user?.phoneNumber+user?.uid+user?.metadata?.toString())

                    updateUI(user)


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        context, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(null)
                }
            }
    }

    companion object {
        private const val TAG = "FacebookLogin"
    }
}