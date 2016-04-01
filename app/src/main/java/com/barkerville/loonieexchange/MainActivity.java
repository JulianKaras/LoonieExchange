package com.barkerville.loonieexchange;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String CAD_AMOUNT = "CANADIAN_AMOUNT";
    private static final String EXCHANGE_RATE = "EXCHANGE_RATE";  // key -value pairs for temporary storage
    private static final String USD_AMOUNT = "AMERICAN_AMOUNT";

    private double cadAmount;       //User enters the CAD amount to convert to USD
    private double exCadUsd;        //variable that stores the exchange rate (conversion) of CAD to USD
    private double usdAmount;       //converted amount in USD

    EditText cadAmountEt;       //Three edit text widgets
    EditText exCadUsdEt;         // that will hold the three
    EditText usdAmountEt;           // respective decimal values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null) {
            cadAmount = 0.0;
            exCadUsd = 0.0;             //if starting the app from a closed state
            usdAmount = 0.0;
        } else {
            cadAmount = savedInstanceState.getDouble(CAD_AMOUNT);
            exCadUsd = savedInstanceState.getDouble(EXCHANGE_RATE);   // if starting from a paused state
            usdAmount = savedInstanceState.getDouble(USD_AMOUNT);
        }

        cadAmountEt = (EditText)findViewById(R.id.cadEditText);    //casting the edit text fields to objects
        exCadUsdEt = (EditText)findViewById(R.id.exchangeEditText);
        usdAmountEt = (EditText)findViewById(R.id.usdEditText);



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
}
