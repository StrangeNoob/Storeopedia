package com.example.hackathon.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.hackathon.R
import com.example.hackathon.fragments.CustomerHomeFragment
import com.example.hackathon.fragments.CustomerProfileFragment
import com.example.hackathon.fragments.CustomerShopFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class CustomerDashboardActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.customer_home -> {
                val CustomerHomeFragment = CustomerHomeFragment.newInstance()
                openFragment(CustomerHomeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.customer_shop -> {
                val CustomerShopFragment = CustomerShopFragment.newInstance()
                openFragment(CustomerShopFragment)
                return@OnNavigationItemSelectedListener true
            }

            R.id.customer_profile -> {
                val CustomerProfileFragment = CustomerProfileFragment.newInstance()
                openFragment(CustomerProfileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_dashboard)
        // toolbar = supportActionBar!!
        openFragment(CustomerHomeFragment.newInstance())
        val bottomNavigation: BottomNavigationView = findViewById(R.id.customer_nav)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}
