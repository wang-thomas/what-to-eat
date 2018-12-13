package com.example.thomas.whattoeat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioButton
import kotlinx.android. synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findRestaurant.setOnClickListener{
            val intent = Intent(this, RestaurantSuggestions::class.java)
            intent.putExtra("time", findViewById<RadioButton>(rg1.checkedRadioButtonId).text)
            intent.putExtra("price", findViewById<RadioButton>(rg2.checkedRadioButtonId).text)
            intent.putExtra("foodtruck", findViewById<RadioButton>(rg3.checkedRadioButtonId).text)
            intent.putExtra("feeling", findViewById<RadioButton>(rg4.checkedRadioButtonId).text)
            startActivity(intent)
        }
    }



}
