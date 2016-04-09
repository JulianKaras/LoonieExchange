package com.barkerville.loonieexchange;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

public class ReverseLoonieExchange extends AppCompatActivity {

    private static final String CAD_AMOUNT = "CANADIAN_AMOUNT";
    private static final String REV_EXCHANGE_RATE = "EXCHANGE_RATE";  // key -value pairs for temporary storage
    private static final String USD_AMOUNT = "AMERICAN_AMOUNT";

    private double usdAmount;
    private double exUsdCad;//User enters the CAD amount to convert to USD//variable that stores the exchange rate (conversion) of CAD to USD
    private double cadAmount;       //converted amount in USD

    EditText usdAmountEt;       //Three edit text widgets
    EditText exUsdCadEt;         // that will hold the three
    EditText cadAmountEt;
    Button endActivity;

    NumberFormat currency = NumberFormat.getCurrencyInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reverse_loonie_exchange);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("LoonieExchange Converter");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebServiceTask2 webServiceTask2 = new WebServiceTask2();
        webServiceTask2.execute();

        if (savedInstanceState == null) {
            usdAmount = 0.0;
            exUsdCad = 0.0;             //if starting the app from a closed state
            cadAmount = 0.0;
        } else {
            usdAmount = savedInstanceState.getDouble(CAD_AMOUNT);
            exUsdCad = savedInstanceState.getDouble(REV_EXCHANGE_RATE);   // if starting from a paused state
            cadAmount = savedInstanceState.getDouble(USD_AMOUNT);
        }

        usdAmountEt = (EditText) findViewById(R.id.usdEditText);    //casting the edit text fields to
        exUsdCadEt = (EditText) findViewById(R.id.exEditText);
        cadAmountEt = (EditText) findViewById(R.id.cadEditText);
        endActivity = (Button) findViewById(R.id.newActivity2);

        endActivity.setOnClickListener(new View.OnClickListener() {

            @Override
        public void onClick(View view) {
                finish();
            }
        });

        usdAmountEt.addTextChangedListener(usdAmountListener);

    }

        private TextWatcher usdAmountListener = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {

                    usdAmount = (Double.parseDouble(s.toString()));

                } catch (NumberFormatException e) {

                    usdAmount = 0.0;

                }
                updateCADAmount();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

    public void updateCADAmount() {


        exUsdCad = Double.parseDouble(String.valueOf(exUsdCadEt.getText().toString()));
       // exUsdCad = 1 / exUsdCad;  // gets Canadian to American rate from reciprocal
        cadAmount = usdAmount * (exUsdCad);

        cadAmountEt.setText(currency.format(cadAmount));

    }

    private class WebServiceTask2 extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String exCadUsdS) {
            super.onPostExecute(exCadUsdS);
            exUsdCadEt.setText(exCadUsdS);
        }

        @Override
        protected String doInBackground(String... params) {
            String exCadUsdS = "0.00";
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("http://api.fixer.io/latest?base=USD");
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
                String exRate = rate.getString("CAD");
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
