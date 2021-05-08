package com.nikhilchauhan.friendlycontact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log

class MainActivity : AppCompatActivity() {
    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        toggle=ActionBarDrawerToggle(this,drawer_layout,R.string.open,R.string.close)

        drawer_layout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled=true
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        try {
            val info = packageManager.getPackageInfo(
                "com.nikhilchauhan.friendlycontact",
                PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {

        } catch (e: NoSuchAlgorithmException) {

        }

//        nav_view.setNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.nav_cuisine ->
//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.nav_host_fragment,FragmentHome())
//                        addToBackStack(null)
//                        commit()
//                    }
//
//                R.id.nav_diet -> supportFragmentManager.beginTransaction().apply {
//                    replace(R.id.nav_host_fragment,GoogleLoginFragment())
//                    addToBackStack(null)
//                    commit()
//                }
//                R.id.nav_equipment ->supportFragmentManager.beginTransaction().apply {
//                    replace(R.id.nav_host_fragment,RegisterFragment())
//                    addToBackStack(null)
//                    commit()
//                }
//                R.id.nav_type -> Toast.makeText(applicationContext,"Type clicked", Toast.LENGTH_SHORT).show()
//            }
//            drawer_layout.closeDrawer(GravityCompat.START)
//            true
//        }


        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_search -> {
                    // Respond to navigation item 1 click
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.nav_host_fragment, PhoneNumberLoginFragment())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }
                R.id.nav_favorites -> {
                    // Respond to navigation item 2 click
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.nav_host_fragment, LoginFragment())
                        addToBackStack(null)
                        commit()
                    }
                    true
                }

                R.id.nav_home -> {
                    // Respond to navigation item 2 click
                    true
                }

                R.id.nav_favorites -> {
                    // Respond to navigation item 2 click
                    true
                }

                R.id.nav_favorites -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            drawer_layout.openDrawer(Gravity.LEFT)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}