package com.test.coronaapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class CountryWiseListActivity extends AppCompatActivity {

    public static class Countries {
        /*
        INSTANCE FIELDS
         */


        private String name;
        private String cases;

        /*
        GETTERS AND SETTERS
         */

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCases() {
            return cases;
        }

        public void setCases(String propellant) {
            this.cases = propellant;
        }

        /*
        TOSTRING
         */
        @Override
        public String toString() {
            return name;
        }

    }

    static class FilterHelper extends Filter {
        ArrayList<Countries> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<Countries> currentList, ListViewAdapter adapter, Context c) {
            this.currentList = currentList;
            this.adapter = adapter;
            this.c = c;
        }

        /*
- Perform actual filtering.
 */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                //CHANGE TO UPPER
                constraint = constraint.toString().toUpperCase();

                //HOLD FILTERS WE FIND
                ArrayList<Countries> foundFilters = new ArrayList<>();

                Countries countries = null;

                //ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    countries = currentList.get(i);

                    //SEARCH
                    if (countries.getName().toUpperCase().contains(constraint)) {
                        //ADD IF FOUND
                        foundFilters.add(countries);
                    }
                }

                //SET RESULTS TO FILTER LIST
                filterResults.count = foundFilters.size();
                filterResults.values = foundFilters;
            } else {
                //NO ITEM FOUND.LIST REMAINS INTACT
                filterResults.count = currentList.size();
                filterResults.values = currentList;
            }

            //RETURN RESULTS
            return filterResults;
        }



        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.setCountries((ArrayList<Countries>) filterResults.values);
            adapter.refresh();
        }
    }

    /*
       Our custom adapter class
        */
    public static class ListViewAdapter extends BaseAdapter implements Filterable {

        Context c;
        ArrayList<Countries> countries;
        public ArrayList<Countries> currentList;
        FilterHelper filterHelper;

        public ListViewAdapter(Context c, ArrayList<Countries> countries) {
            this.c = c;
            this.countries = countries;
            this.currentList = countries;
        }

        @Override
        public int getCount() {
            return countries.size();
        }

        @Override
        public Object getItem(int i) {
            return countries.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.model, viewGroup, false);
            }

            TextView txtName = view.findViewById(R.id.textViewCountry);
            TextView txtPropellant = view.findViewById(R.id.textViewCases);


            final Countries s = (Countries) this.getItem(i);

            txtName.setText("Country: " + s.getName());
            txtPropellant.setText("Cases:  " + s.getCases());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(c, s.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(c, DetailofCountry.class);
                    intent.putExtra("cname", s.getName()); //sending the name of the country to the 'DetailofCountry Activity' so that we can parse json object of that particular country. 
                    c.startActivity(intent);
                }
            });

            return view;
        }

        public void setCountries(ArrayList<Countries> filteredCountries) {
            this.countries = filteredCountries;

        }

        @Override
        public Filter getFilter() {
            if (filterHelper == null) {
                filterHelper = new FilterHelper(currentList, this, c);
            }

            return filterHelper;
        }

        public void refresh() {
            notifyDataSetChanged();
        }
    }


    /*
    Our HTTP Client
     */
    public static class JSONDownloader {

        //SAVE/RETRIEVE URLS
        private static final String JSON_DATA_URL = "https://coronavirus-19-api.herokuapp.com/countries";
        //INSTANCE FIELDS
        private final Context c;

        public JSONDownloader(Context c) {
            this.c = c;
        }


        /*
        Fetch JSON Data
         */
        public ArrayList<Countries> result(final ListView myListView) {
            final ArrayList<Countries> downloadedData = new ArrayList<>();


            AndroidNetworking.get(JSON_DATA_URL)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            Countries s;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);


                                    String country = jo.getString("country");
                                    String cases = jo.getString("cases");


                                    s = new Countries();

                                    s.setName(country);
                                    s.setCases(cases);


                                    downloadedData.add(s);
                                }


                            } catch (JSONException e) {

                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();

                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : "+anError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
            return downloadedData;
        }
    }




//    ArrayList<Countries> countries = new ArrayList<>();
//    ListView myListView;
//    ListViewAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.country_wise_list_activity);
//
//        myListView = findViewById(R.id.myListView);
//
//
//
//        final SearchView mySearchView = findViewById(R.id.mySearchView);
//
//
//       mySearchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                adapter.getFilter().filter(s);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//                return false;
//            }
//        });
//
//
//        countries = new JSONDownloader(CountryWiseListActivity.this).result(myListView);
//        adapter = new ListViewAdapter(this, countries);
//        myListView.setAdapter(adapter);
    }
