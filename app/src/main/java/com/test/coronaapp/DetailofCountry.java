package com.test.coronaapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import soup.neumorphism.NeumorphTextView;

public class DetailofCountry extends AppCompatActivity {

    /* Declaring all the necessary variables which we will be using*/

    public String cases, Death, Recover, TodayCases, TodayDeaths, url;
    public static TextView countryCases, countryDeaths, countryRe, toCases, toDeaths;
    public SwipeRefreshLayout refreshSwipe2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.detail_of_country);
        Objects.requireNonNull(getSupportActionBar()).hide();
        /*Intialising all the variables from the resource file*/

        refreshSwipe2 = findViewById(R.id.refreshSwipe2);
        countryCases = findViewById(R.id.CCases);
        countryDeaths = findViewById(R.id.CDeaths);
        countryRe = findViewById(R.id.CRecover);
        toCases = findViewById(R.id.Tc);
        toDeaths = findViewById(R.id.Td);

        getInIntent();

        refreshSwipe2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSwipe2.setRefreshing(false);
                getInIntent();
            }
        });


    }



    /*Getting the intent from the 'CountryWiseListActivity' */

    private void getInIntent() {
        if (getIntent().hasExtra("cname")) {
            String coname = getIntent().getStringExtra("cname");
            setThing(coname);
        }

    }

    private void setThing(String coname) {
        TextView countryname = findViewById(R.id.CountryDatac);
        countryname.setText(coname);

        /*Fetching data from the api */
        final RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(DetailofCountry.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://coronavirus-19-api.herokuapp.com/countries/" + coname, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("myapp", "The response is" + response.getString("cases") + " " + response.getString("deaths"));
                    Death = response.getString("deaths");
                    cases = response.getString("cases");
                    Recover = response.getString("recovered");
                    TodayCases = response.getString("todayCases");
                    TodayDeaths = response.getString("todayDeaths");

                    countryCases.setText(cases);
                    countryDeaths.setText(Death);
                    countryRe.setText(Recover);
                    toCases.setText(TodayCases);
                    toDeaths.setText(TodayDeaths);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("myapp", "something went wrong");

            }
        });

        requestQueue.add(jsonObjectRequest);
    }
}
