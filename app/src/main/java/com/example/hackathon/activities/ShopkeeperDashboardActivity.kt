package com.example.hackathon.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.hackathon.R
import com.example.hackathon.fragments.ShopkeeperHomeFragment
import com.example.hackathon.fragments.ShopkeeperProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShopkeeperDashboardActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.shopkeeper_home -> {
                val ShopkeeperHomeFragment = ShopkeeperHomeFragment.newInstance()
                openFragment(ShopkeeperHomeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.shopkeeper_profile -> {
                val ShopKeeperProfileFragment = ShopkeeperProfileFragment.newInstance()
                openFragment(ShopKeeperProfileFragment)
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
        setContentView(R.layout.activity_shopkeeper_dashboard)
        // toolbar = supportActionBar!!
        openFragment(ShopkeeperHomeFragment.newInstance())
        val bottomNavigation: BottomNavigationView = findViewById(R.id.shopkeeper_nav)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
