package com.example.movieproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar
    private val fragmentManager: FragmentManager= supportFragmentManager
    private var activeFragment: Fragment = FragmentFeed()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar= findViewById(R.id.toolbar)
        sharedPreferences =getSharedPreferences("shared_preference", Context.MODE_PRIVATE)

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener)
        fragmentManager.beginTransaction().add(R.id.main_container, FragmentFeed(), "1").commit()
    }

    private val navigationListener=
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.feed -> {
                    activeFragment = FragmentFeed()
                    fragmentManager.beginTransaction().replace(R.id.main_container, activeFragment)
                        .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
                return@OnNavigationItemSelectedListener false
            }

}

