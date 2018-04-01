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

import com.example.shruthinarayan.lab2.databases.MortgageInformationHelper;
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

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private HashMap<Marker, Data> popUpInfo = new HashMap<Marker, Data>();
    private ArrayList<Data> homeList = new ArrayList<Data>();

    private TextView typeText;
    private TextView streetAddress;
    private TextView cityText;
    private TextView priceText;
    private TextView downpaymentText;
    private TextView rateText;
    private TextView termsText;
    private TextView installmentText;
    private Button deleteBtn;
    private Button edit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        DrawerLayout drawerLyt = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLyt, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLyt.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        MortgageInformationHelper dbHelper = new MortgageInformationHelper(getApplicationContext());



        mapFragment.getMapAsync(this);

        homeList = dbHelper.obtainMortgageDatabase();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        List<Address> temp = null;
        Address location = null;
        LatLng latLng = null;
        Geocoder geoCoder = new Geocoder(getApplicationContext());
        if (homeList.size() == 0)
            defaultMapView(map);
        else {
            displayHomes(map, geoCoder);

            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {


                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }


                @Override
                public View getInfoContents(final Marker marker) {

                    Data home;
                    final Dialog dialog = new Dialog(MapActivity.this);
                    dialog.setContentView(R.layout.details);
                    dialog.setTitle("Home");

                    typeText = dialog.findViewById(R.id.type);
                    streetAddress = dialog.findViewById(R.id.address);
                    cityText = dialog.findViewById(R.id.city);
                    priceText = dialog.findViewById(R.id.price);
                    downpaymentText = dialog.findViewById(R.id.downpayment);
                    rateText = dialog.findViewById(R.id.rate);
                    termsText = dialog.findViewById(R.id.terms);
                    installmentText = dialog.findViewById(R.id.installment);
                    edit = dialog.findViewById(R.id.edit);
                    deleteBtn = dialog.findViewById(R.id.delete);

                    home = popUpInfo.get(marker);
                    typeText.setText(home.getType());
                    streetAddress.setText(home.getAddress());
                    cityText.setText(home.getCity());
                    priceText.setText(home.getPropertyprice());
                    downpaymentText.setText(home.getDownpayment());
                    rateText.setText(home.getRate());
                    termsText.setText(home.getTerms());
                    installmentText.setText(home.getMonthlyPayments());
                    // if button is clicked, close the custom dialog
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //     com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper mDbHelper = new com.example.shruthinarayan.lab2.Databases.MortgageInformationHelper(getApplicationContext());
                            Data homeToEdit = popUpInfo.get(marker);
                            Gson gson = new Gson();
                            String j = gson.toJson(homeToEdit);
                            Intent intent = new Intent(MapActivity.this, MainActivity.class);
                            intent.putExtra("edit", j);
                            startActivity(intent);


                        }
                    });
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MortgageInformationHelper mDbHelper = new MortgageInformationHelper(getApplicationContext());
                            Data homeToRemove = popUpInfo.get(marker);
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

    private void displayHomes(GoogleMap map, Geocoder geoCoder) {
        List<Address> temp;
        Address location;
        LatLng latLng;
        System.out.println("homeList size : " + homeList.size());
        System.out.println("Address of firest : " + homeList.get(0).getFullAddress());
        for (int i = 0; i < homeList.size(); i++) {
            Marker marker;
            System.out.println("Inside for loop");
            try {
                Data home = homeList.get(i);
                String full_address = home.getFullAddress();
                System.out.println("Address : " + full_address);
                temp = geoCoder.getFromLocationName(full_address, 1);
                location = temp.get(0);
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                marker = map.addMarker(new MarkerOptions()
                        .position(latLng));

                popUpInfo.put(marker, home);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));

            } catch (Exception ee) {
                System.out.println("Got Ex");
                Context context = getApplicationContext();
                CharSequence text = "Couldn't locate a Home..! Make Sure you're connected to Internet";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }
    }

    private void defaultMapView(GoogleMap map) {
        List<Address> temp;
        Address location;
        Geocoder america = new Geocoder(getApplicationContext());
        try {
            temp = america.getFromLocationName("United States of America", 1);
            location = temp.get(0);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5.0f));
        } catch (IOException e) {
            Log.e("Exception", "0 result found");
        }

        Context context = getApplicationContext();
        CharSequence text = "No Saved Homes..!!";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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
        MenuItem reset_action;
        getMenuInflater().inflate(R.menu.main, menu);
        reset_action = menu.findItem(R.id.reset);
        reset_action.setTitle("Clear All Saved Homes");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.reset) {

            MortgageInformationHelper mDbHelper = new MortgageInformationHelper(getApplicationContext());
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
        if (id == R.id.calc_mortgage) {
            startActivity(new Intent(MapActivity.this, MainActivity.class));
        }
//        else if (id == R.id.showhomes_inmap) {
//
//        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
