package com.example.movieproject

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main.view.apptitle

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toolbar: Toolbar
    private lateinit var toolbarAppTitle: TextView
    private val fragmentManager: FragmentManager= supportFragmentManager
    private var activeFragment: Fragment = FragmentFeed()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar= findViewById(R.id.toolbar)
        toolbarAppTitle= findViewById<TextView>(R.id.apptitle)

        sharedPreferences =getSharedPreferences( getString(R.string.preference_file), Context.MODE_PRIVATE)

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
                    toolbarAppTitle.text = "Moviedom"
                    toolbar.setBackgroundColor(getColor(R.color.appColor))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.like -> {

                    activeFragment = FavouritesFragment()
                    fragmentManager.beginTransaction().replace(R.id.main_container, activeFragment).commit()
                    toolbarAppTitle.text = "Favourite movies"
                    toolbar.setBackgroundColor(getColor(R.color.appColor))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.account -> {
                    activeFragment = UserInfoFragment()
                    fragmentManager.beginTransaction().replace(R.id.main_container, activeFragment).commit()
                    toolbarAppTitle.text = "User settings"
                    toolbar.setBackgroundColor(getColor(R.color.settings))
                        //"#35373E"

                    return@OnNavigationItemSelectedListener true
                }
            }
                return@OnNavigationItemSelectedListener false
            }

}

