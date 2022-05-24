package com.test.coronaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphTextView;

public class MainActivity extends AppCompatActivity {



    /* Declaring all the necessary variables which we will be using*/

    public static TextView Tcases, Tdeaths, Trecover;
    public String Cases, Death, Recover;

    public SwipeRefreshLayout refreshSwipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        /*Intialising all the variables from the resource file*/

        refreshSwipe = findViewById(R.id.refreshSwipe);

        Tcases = findViewById(R.id.TCases);
        Tdeaths = findViewById(R.id.TDeaths);
        Trecover = findViewById(R.id.TRecover);


        loadingData();

        refreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshSwipe.setRefreshing(false);
                /* Fetching the data from the api */
                loadingData();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.alldata);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.alldata:
                        return true;
                    case R.id.country:
                        startActivity(new Intent(getApplicationContext(), CountrySearchActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });


    }

    private void loadingData() {

        final RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                "https://coronavirus-19-api.herokuapp.com/all", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("myapp", "The response is" + response.getString("cases") + " " + response.getString("deaths"));
                    Death = response.getString("deaths");
                    Cases = response.getString("cases");
                    Recover = response.getString("recovered");
                    Tcases.setText(Cases);
                    Tdeaths.setText(Death);
                    Trecover.setText(Recover);
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
