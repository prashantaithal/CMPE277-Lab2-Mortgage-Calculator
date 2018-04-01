package com.example.shruthinarayan.lab2.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shruthinarayan.lab2.Data;
import com.example.shruthinarayan.lab2.R;
import com.example.shruthinarayan.lab2.databases.MortgageInformationHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText addressText;
    private ScrollView scrollViewer;
    private Spinner termsSpinner;
    private TextView answerText;
    private ConstraintLayout saveMainCL;
    private EditText cityText;
    private EditText zipCode;
    private Spinner stateSpinner;
    private Data data;
    private Spinner propertyType;
    private EditText downPayment;
    private EditText propertyAmount;
    private EditText rateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Button calculateButton;
        Button saveButton;
        TextView homeText = findViewById(R.id.textview_savehome);
        saveMainCL = findViewById(R.id.linearlayout_saveform);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        rateText = findViewById(R.id.editText_rate);
        termsSpinner = findViewById(R.id.spinner_terms);
        answerText = findViewById(R.id.textview_answer);
        calculateButton = findViewById(R.id.button_calculate);
        saveButton = findViewById(R.id.button_save);
        scrollViewer = findViewById(R.id.scrollview);
        propertyType = findViewById(R.id.spinner_propertytype);
        addressText = findViewById(R.id.editText_address);
        cityText = findViewById(R.id.editText_city);
        downPayment = findViewById(R.id.editText_downpayment);
        stateSpinner = findViewById(R.id.spinner_states);
        zipCode = findViewById(R.id.editText_zipcode);
        propertyAmount = findViewById(R.id.editText_propertyprice);

        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        editInfo();

        data = new Data();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String propertyprice = propertyAmount.getText().toString();
                String downStr = downPayment.getText().toString();
                String rateStr = rateText.getText().toString();
                String termsStr = termsSpinner.getSelectedItem().toString();

                checkTextFields(propertyprice, downStr, rateStr, termsStr);
            }
        };


        calculateButton.setOnClickListener(listener);

        View.OnClickListener saveButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMainCL.setVisibility(View.VISIBLE);
                scrollViewer.scrollBy(0, 1000);
            }
        };
        homeText.setOnClickListener(saveButtonListener);

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] fields = new String[10];
                fields[0] = propertyType.getSelectedItem().toString();
                fields[1] = addressText.getText().toString();
                fields[2] = cityText.getText().toString();
                fields[3] = stateSpinner.getSelectedItem().toString();
                fields[4] = zipCode.getText().toString();
                fields[5] = propertyAmount.getText().toString();
                fields[6] = downPayment.getText().toString();
                fields[7] = rateText.getText().toString();
                fields[8] = termsSpinner.getSelectedItem().toString();
                fields[9] = answerText.getText().toString();

                if (TextUtils.isEmpty(fields[1]) || TextUtils.isEmpty(fields[2]) || TextUtils.isEmpty(fields[4])) {
                    CharSequence info = "Address fields should not be empty";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toaster = Toast.makeText(getApplicationContext(), info, duration);
                    toaster.show();
                } else {
                    data.setData(fields);

                    String full_address = data.getFullAddress();

                    List<Address> temp = null;
                    Address location = null;

                    ConnectivityManager connectivityManager
                            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Geocoder address_checker = new Geocoder(getApplicationContext());
                        try {
                            temp = address_checker.getFromLocationName(full_address, 1);
                            if (temp == null || temp.isEmpty() || data.getFullAddress().equalsIgnoreCase("")) {
                                Context context = getApplicationContext();
                                CharSequence text = "Couldn't verify address.! Please correct Error in Address";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(context, text, duration);
                                toast.show();
                            } else {
                                location = temp.get(0);
                                MortgageInformationHelper mDbHelper = new MortgageInformationHelper(getApplicationContext());

                                try {
                                    if (getIntent().getStringExtra("home") != null) {
                                        updateAddress(fields, mDbHelper);
                                    } else {
                                        saveAddress(mDbHelper);
                                    }
                                } catch (Exception e) {
                                    Log.e("Exception ", "e");
                                    System.out.println("Exception " + e);
                                }
                            }
                        } catch (IOException ioe) {
                            Context context = getApplicationContext();
                            CharSequence text = "Invalid Address! Please correct it";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Couldn't verify Address.! Please connect to Internet";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            }
        };
        saveButton.setOnClickListener(listener2);

        View.OnClickListener homeDetailsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMainCL.setVisibility(View.VISIBLE);
                scrollViewer.scrollBy(0, 500);
            }
        };
        homeText.setOnClickListener(homeDetailsListener);
    }

    private void saveAddress(MortgageInformationHelper mDbHelper) throws Exception {
        mDbHelper.insertHome(data);
        Context context = getApplicationContext();
        CharSequence text = "Address Successfully Saved.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void updateAddress(String[] fields, MortgageInformationHelper mDbHelper) {
        fields[0] = propertyType.getSelectedItem().toString();
        fields[1] = addressText.getText().toString();
        fields[2] = cityText.getText().toString();
        fields[3] = stateSpinner.getSelectedItem().toString();
        fields[4] = zipCode.getText().toString();
        fields[5] = propertyAmount.getText().toString();
        fields[6] = downPayment.getText().toString();
        fields[7] = rateText.getText().toString();
        fields[8] = termsSpinner.getSelectedItem().toString();
        fields[9] = answerText.getText().toString();

        mDbHelper.updateHome(fields, fields[1]);
        Context context = getApplicationContext();
        CharSequence text = "Address Updated Successfully.";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void checkTextFields(String propertyPrice, String downStr, String rateStr, String termsStr) {
        float downPayment = 0;
        float ratePercent = Float.parseFloat(rateStr);
        ;
        float propertyPriceFloat = Float.parseFloat(propertyPrice);
        int terms = Integer.parseInt(termsStr);
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;

        if (TextUtils.isEmpty(propertyPrice)) {
            CharSequence text = "Property Price should not be empty";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        if (TextUtils.isEmpty(rateStr)) {
            CharSequence text = "Rate cannot be empty";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            if (TextUtils.isEmpty(downStr)) {
                this.downPayment.setText(downPayment + "");
            } else
                downPayment = Float.parseFloat(this.downPayment.getText().toString());

            float principal = propertyPriceFloat - downPayment;
            ratePercent = ratePercent / 1200;
            terms = terms * 12;
            int installment = (int) (((ratePercent * principal) / (1 - Math.pow(1 + ratePercent, terms * -1))));
            answerText.setText(installment + "");
        }
    }

    private void editInfo() {
        if (getIntent().getStringExtra("edit") != null) {
            int position = 0;
            Gson gson = new Gson();
            String jsonString = getIntent().getStringExtra("edit");
            String[] temp_type = getResources().getStringArray(R.array.propertytype);
            Data home = gson.fromJson(jsonString, Data.class);

            for (int i = 0; i < temp_type.length; i++)
                if (temp_type[i].equals(home.getState()))
                    position = i;

            addressText.setText(home.getAddress());
            propertyType.setSelection(position);
            cityText.setText(home.getCity());

            position = 0;
            String[] temp_states = getResources().getStringArray(R.array.states);
            for (int i = 0; i < temp_states.length; i++)
                if (temp_states[i].equals(home.getState()))
                    position = i;
            stateSpinner.setSelection(position);
            zipCode.setText(home.getZip());

            rateText.setText(home.getRate());
            propertyAmount.setText(home.getPropertyprice());
            downPayment.setText(home.getDownpayment());
            answerText.setText(home.getEMI());

            position = 0;
            String[] temp_terms = getResources().getStringArray(R.array.terms);
            for (int i = 0; i < temp_terms.length; i++)
                if (temp_terms[i].equals(home.getTerms()))
                    position = i;

            answerText.setText(home.getEMI());
            termsSpinner.setSelection(position);


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
        MenuItem reset_action;
        getMenuInflater().inflate(R.menu.main, menu);
        reset_action = menu.findItem(R.id.reset);
        reset_action.setTitle("Start New Calculation");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset) {
            propertyType.setSelection(0);
            addressText.setText("");
            propertyAmount.setText("");
            downPayment.setText("");
            rateText.setText("");
            cityText.setText("");
            stateSpinner.setSelection(0);
            zipCode.setText("");
            termsSpinner.setSelection(0);
            answerText.setText("");
            saveMainCL.setVisibility(View.INVISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.calc_mortgage) {
            rateText.setText("");
            addressText.setText("");
            cityText.setText("");
            stateSpinner.setSelection(0);
            termsSpinner.setSelection(0);
            answerText.setText("");
            propertyType.setSelection(0);
            zipCode.setText("");
            propertyAmount.setText("");
            downPayment.setText("");
            saveMainCL.setVisibility(View.INVISIBLE);
        } else if (id == R.id.show_in_map) {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}