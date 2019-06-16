package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ExchangeRateActivity extends AppCompatActivity {
    private String today;
    private DatePicker datePicker;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange_rate);
        datePicker = findViewById(R.id.calendarEx);
        today = getDate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.exchange_rate_menu, menu);
        return true;
    }

    public void onExchangeHistoryMenuClicked(MenuItem item){
        Intent intent = new Intent(ExchangeRateActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onExchangeConverterMenuClicked(MenuItem item){
        Intent intent = new Intent(ExchangeRateActivity.this, StartActivity.class);
        startActivity(intent);
    }

    private String getDate() {
        String day = String.valueOf(datePicker.getDayOfMonth());
        String month = String.valueOf(datePicker.getMonth() + 1);
        String year = String.valueOf(datePicker.getYear());
        if (Integer.valueOf(day) < 10) day = "0" + day;
        if (Integer.valueOf(month) < 10) month = "0" + month;
        return year + "/" + month + "/" + day;
    }

    public void buttonClicked(View view){
        String curDate = getDate();
        LinearLayout linearLayout = findViewById(R.id.exchangeRate);
        linearLayout.removeAllViews();
        RequestCB requestCB = new RequestCB();
        requestCB.getExchangeRates(this, linearLayout, curDate, today);
    }
}
