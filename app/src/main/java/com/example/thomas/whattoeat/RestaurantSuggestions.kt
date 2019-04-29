package com.example.thomas.whattoeat

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.android.volley.*
import kotlinx.android.synthetic.main.activity_restaurant_suggestions.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class RestaurantSuggestions : AppCompatActivity() {
    val apiKey = "28e3ab7d80efc5187dd610d7b239886d"
    val phillyID = "287"
    lateinit var myintent : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_suggestions)
        myintent = Intent(this, MapsActivity::class.java)
        val bdle = intent.extras

        fab.setOnClickListener{
            startActivity(myintent)
        }

        val establishment = preferredEstab(bdle.get("time").toString(), bdle.get("foodtruck").toString() == "Yes")
        val cuisine = preferredCuisine(bdle.get("price").toString(), bdle.get("feeling").toString(), bdle.get("foodtruck").toString() == "Yes")
        val category = preferredCat(bdle.get("time").toString(), bdle.get("price").toString(), bdle.get("feeling").toString())

        //will be generated using a type of establishment, cuisine, category
        requestWithHeaders(establishment, cuisine, category)

    }

    private fun requestWithHeaders(estab : Int, cuis : Int, cat : Int) {
        val start = Random().nextInt(5) + 1
        val queue = Volley.newRequestQueue(this)
        val url = "https://developers.zomato.com/api/v2.1/search?entity_id=$phillyID&entity_type=city&start=$start&count=5&cuisines=$cuis&establishment_type=$estab&category=$cat&sort=rating&order=desc\n"
        val getRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val arr = response.get("restaurants") as JSONArray
                populate(title1, cuisine1, rating1, address1, ratingWord1, locality1, website1, arr[0])
            },
            Response.ErrorListener {
                System.out.print("ERROR!")
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Accept", "application/json")
                headers.put("user-key", apiKey)
                return headers
            }
        }
        queue.add(getRequest)
    }

    //my algorithms or rules
    //the numbers correspond to the id of each category - it was listed on the Zomato API
    private fun preferredEstab(time : String, foodTruck : Boolean) : Int {
        if (foodTruck){
            return 81
        }
        if (time == "30 Minutes") {
            return 21
        } else if (time == "Not in a hurry!") {
            return 18
        } else {
            return 16
        }
    }

    //my set rules
    private fun preferredCuisine(price : String, feeling : String, foodTruck: Boolean) : Int {
        if (foodTruck) {
            return 0
        }

        if (feeling == "Adventurous" && price == "Under 10") {
            return 70
        } else if (feeling == "Comfort Food" && price == "Under 10") {
            return 304
        } else if (price == "Under $25") {
            return 25
        } else {
            return 45
        }
    }

    //my set rules
    private fun preferredCat(time: String, price: String, feeling: String) : Int {
        if (time == "30 Minutes") {
            return 5
        }
        if (price == "Under $10") {
            return 6
        } else if (feeling == "Surprise Me") {
            return 14
        } else if (price == "Eat Fancy") {
            return 3
        } else if (time == "60 Minutes" && price == "Under $25") {
            return 9
        } else {
            return 10
        }
    }

    private fun populate(title : TextView, cuisine : TextView, rating : TextView, address : TextView, ratingWord : TextView, locality : TextView, website : TextView, jsonobj : Any) {
        val jobj = jsonobj as JSONObject
        val info = jobj.get("restaurant") as JSONObject
        title.text = info.getString("name")
        cuisine.text = "Rating: " + info.getString("cuisines")
        val locationInfo = info.get("location") as JSONObject
        address.text = locationInfo.getString("address")
        val rateInfo = info.get("user_rating") as JSONObject
        rating.text = rateInfo.getString("aggregate_rating")
        ratingWord.text = "Key Word: " + rateInfo.getString("rating_text")
        locality.text = locationInfo.getString("locality")
        website.text = info.getString("url")

        myintent.putExtra("lat", locationInfo.getDouble("latitude"))
        myintent.putExtra("lng", locationInfo.getDouble("longitude"))
        myintent.putExtra("name", info.getString("name"))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.getItemId()) {
            R.id.reset -> {
                val firstIntent = Intent(this, MainActivity::class.java)
                startActivity(firstIntent)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
