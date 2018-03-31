package com.example.shruthinarayan.lab2.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shruthinarayan.lab2.Data;
import com.example.shruthinarayan.lab2.R;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shruthinarayan on 3/27/18.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener
{
    private ArrayList<Data> homelist = new ArrayList<Data>();
    private MenuItem reset_action;
    private List<Address> temp = null;
    private HashMap<Marker,Data> infowindows = new HashMap<Marker, Data>();
    private Address location = null;
    private LatLng p1 = null;

    private TextView type;
    private TextView street_address;
    private TextView city;
    private TextView price;
    private TextView downpayment;
    private TextView rate;
    private TextView terms;
    private TextView installment;
    private Button delete;
    private Button edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper mDbHelper = new com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper(getApplicationContext());

        homelist = mDbHelper.retrieveHomes();
        // Creating a cursor and reading all the rows from Database ...


    }

    @Override
    public void onMapReady(GoogleMap map) {
        Geocoder geoCoder = new Geocoder(getApplicationContext());
        if(homelist.size() == 0)
        {

            Geocoder america = new Geocoder(getApplicationContext());
            try {
                temp = america.getFromLocationName("California, United States of America", 1);
                location = temp.get(0);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5.0f));
            }catch (IOException e){
                Log.e("Exception","0 result found");
            }

            Context context = getApplicationContext();
            CharSequence text = "No Saved Homes..!!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {

            System.out.println("homelist size : " + homelist.size());
            System.out.println("Address of firest : " + homelist.get(0).getFullAddress());
            for (int i = 0; i < homelist.size(); i++) {
                Marker marker;
                System.out.println("Inside for loop");
                try {
                    Data home = homelist.get(i);
                    // Displaying Homes as Markers ...
                    String full_address = home.getFullAddress();
                    System.out.println("Address : " + full_address);
                    temp = geoCoder.getFromLocationName(full_address, 1);
                    location = temp.get(0);
                    p1 = new LatLng(location.getLatitude(), location.getLongitude());
                    marker = map.addMarker(new MarkerOptions()
                            .position(p1));

                    infowindows.put(marker,home);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));

                }catch (Exception ee) {
                    System.out.println("Got Ex");
                    Context context = getApplicationContext();
                    CharSequence text = "Couldn't locate a Home..! Make Sure you're connected to Internet";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }


                @Override
                public View getInfoContents(final Marker marker) {

                    Data home;
                    final Dialog dialog = new Dialog(MapActivity.this);
                    dialog.setContentView(R.layout.infowindow);
                    dialog.setTitle("Home");

                    type = dialog.findViewById(R.id.type);
                    street_address = dialog.findViewById(R.id.address);
                    city = dialog.findViewById(R.id.city);
                    price = dialog.findViewById(R.id.price);
                    downpayment = dialog.findViewById(R.id.downpayment);
                    rate = dialog.findViewById(R.id.rate);
                    terms = dialog.findViewById(R.id.terms);
                    installment = dialog.findViewById(R.id.installment);
                    edit = dialog.findViewById(R.id.edit);
                    delete = dialog.findViewById(R.id.delete);

                    home = infowindows.get(marker);
                    type.setText(home.getType());
                    street_address.setText(home.getAddress());
                    city.setText(home.getCity());
                    price.setText(home.getPropertyprice());
                    downpayment.setText(home.getDownpayment());
                    rate.setText(home.getRate());
                    terms.setText(home.getTerms());
                    installment.setText(home.getMonthlyPayments());
                    // if button is clicked, close the custom dialog
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                       //     com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper mDbHelper = new com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper(getApplicationContext());
                            Data homeToEdit = infowindows.get(marker);
                            Gson gson = new Gson();
                            String j = gson.toJson(homeToEdit);
                            Intent intent = new Intent(MapActivity.this, MainActivity.class);
                            intent.putExtra("home",j);
                            startActivity(intent);


                        }
                    });
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper mDbHelper = new com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper(getApplicationContext());
                            Data homeToRemove = infowindows.get(marker);
                            mDbHelper.deleteHome(homeToRemove.getAddress());
                            marker.remove();
                            dialog.dismiss();
                            Context context = getApplicationContext();
                            CharSequence text = "Home Deleted..!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }
                    });
                    dialog.show();

                    return null;
                }
            });

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        reset_action = menu.findItem(R.id.action_reset);
        reset_action.setTitle("Clear All Saved Homes");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_reset) {

            com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper mDbHelper = new com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            mDbHelper.truncateTable(db);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Context context = getApplicationContext();
            CharSequence text = "All Homes Deleted..!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        // Handle the camera action
        if (id == R.id.calculate_mortgage) {
            startActivity(new Intent(MapActivity.this,MainActivity.class));
        }
//        else if (id == R.id.showhomes_inmap) {
//
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
