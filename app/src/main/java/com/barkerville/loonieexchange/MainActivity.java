package com.barkerville.loonieexchange;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity {

    private static final String CAD_AMOUNT = "CANADIAN_AMOUNT";
    private static final String EXCHANGE_RATE = "EXCHANGE_RATE";  // key -value pairs for temporary storage
    private static final String USD_AMOUNT = "AMERICAN_AMOUNT";

    private double cadAmount;       //User enters the CAD amount to convert to USD
    public double exCadUsd;        //variable that stores the exchange rate (conversion) of CAD to USD
    private double usdAmount;       //converted amount in USD

    EditText cadAmountEt;       //Three edit text widgets
    EditText exCadUsdEt;         // that will hold the three
    EditText usdAmountEt;           // respective decimal values
    Button changeConversion;

    NumberFormat currency = NumberFormat.getCurrencyInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("LoonieExchange Converter");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebServiceTask webServiceTask = new WebServiceTask();
        webServiceTask.execute();

        if (savedInstanceState == null) {
            cadAmount = 0.0;
            exCadUsd = 0.0;             //if starting the app from a closed state
            usdAmount = 0.0;
        } else {
            cadAmount = savedInstanceState.getDouble(CAD_AMOUNT);
            exCadUsd = savedInstanceState.getDouble(EXCHANGE_RATE);   // if starting from a paused state
            usdAmount = savedInstanceState.getDouble(USD_AMOUNT);
        }

        cadAmountEt = (EditText) findViewById(R.id.revUsdEditText);    //casting the edit text fields to
        exCadUsdEt = (EditText) findViewById(R.id.revExchangeEditText);
        usdAmountEt = (EditText) findViewById(R.id.revCadEditText);
        changeConversion = (Button)findViewById(R.id.newActivityButton);

        changeConversion.setOnClickListener(new View.OnClickListener() {

            @Override
        public void onClick(View view){
                startActivity(new Intent(MainActivity.this, ReverseLoonieExchange.class));
            }
        });


        cadAmountEt.addTextChangedListener(cadAmountListener);






    }


    private TextWatcher cadAmountListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            try {

                cadAmount = Double.parseDouble(s.toString());

            } catch (NumberFormatException e) {

                cadAmount = 0.0;

            }
            updateUSDAmount();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void updateUSDAmount() {

        exCadUsd = Double.parseDouble(String.valueOf(exCadUsdEt.getText().toString()));
        usdAmount = cadAmount * (exCadUsd);
        usdAmountEt.setText(currency.format(usdAmount));
       // usdAmountEt.setText(String.format("%.02f", usdAmount));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private class WebServiceTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String exCadUsdS) {
            super.onPostExecute(exCadUsdS);
            exCadUsdEt.setText(exCadUsdS);
        }

        @Override
        protected String doInBackground(String... params) {
            String exCadUsdS = "0.00";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://api.fixer.io/latest?base=CAD");
                urlConnection = (HttpURLConnection) url.openConnection();
                exCadUsdS = getExUsdCad(urlConnection.getInputStream());
            } catch (IOException e) {
                Log.e("Main Activity", "Error", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return exCadUsdS;
        }

        protected String getExUsdCad(InputStream in) {
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader bufferedReader = null;

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
               }
                //JSON needs to be parsed here
                JSONObject reader = new JSONObject(stringBuilder.toString());
                JSONObject rate = reader.getJSONObject("rates");
                String exRate = rate.getString("USD");
                Log.i("Returned data", stringBuilder.toString());
                return exRate;
            } catch (Exception e) {
                Log.e("MainActivity", "Error", e);
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return "Unable to get exchange rate. Please try again later.";

        }


    }
}
