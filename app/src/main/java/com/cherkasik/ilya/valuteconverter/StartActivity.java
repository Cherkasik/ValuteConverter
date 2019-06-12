 package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {
  private SQLiteDatabase db;
  private Spinner spinnerFrom;
  private Spinner spinnerTo;
  private RequestQueue mQueue;

  private void error(String name){
    Toast toast = Toast.makeText(getApplicationContext(), "Something wrong with " + name, Toast.LENGTH_SHORT);
    toast.show();
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
    db = create_db();
    spinnerFrom = findViewById(R.id.spinnerFrom);
    spinnerTo = findViewById(R.id.spinnerTo);
    getValutes();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_start, menu);
    return true;
  }

  //TODO: move to DAO
  private SQLiteDatabase create_db(){
    SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);
    db.execSQL("CREATE TABLE IF NOT EXISTS history (conv_from TEXT, conv_to TEXT, num FLOAT, res FLOAT, date DATE)");
    return db;
  }

  //TODO: move to DAO
  public void add_to_history(SQLiteDatabase db, String conv_from, String conv_to, float num, float res, String date){
    //TODO: check if there are 10 items already
    db.execSQL("INSERT INTO history VALUES ("+ conv_from + ", " + conv_to + ", " + num + ", " + res + ", " + date);
  }

  private void addItemsOnSpinner(List<String> list){
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerFrom.setAdapter(dataAdapter);
    spinnerTo.setAdapter(dataAdapter);
  }

  private void getValutes() {
    mQueue = Volley.newRequestQueue(this);
    String url = "https://www.cbr-xml-daily.ru/daily_json.js";
    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          List<String> list = new ArrayList<>();
          JSONObject jsonObject = response.getJSONObject("Valute");
          Iterator<String> keys = jsonObject.keys();
          while (keys.hasNext()){
            String key = keys.next();
            JSONObject valute = jsonObject.getJSONObject(key);
            list.add(valute.getString("Name"));
          }
          addItemsOnSpinner(list);

        } catch (JSONException e) {
          error("json response");
        }
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError e) {
        error("request");
      }
    });
    mQueue.add(request);
  }

  public void onSettingsMenuClicked(MenuItem item) {
    Intent intent = new Intent(StartActivity.this, HistoryActivity.class);
    startActivity(intent);
  }

  private String getDate(){
    DatePicker datePicker = findViewById(R.id.calendar);
    int day = datePicker.getDayOfMonth();
    int month = datePicker.getMonth() + 1;
    int year = datePicker.getYear();
    return String.format(Locale.US, "%d/%d/%d", day, month, year);
  }

  public void convert(View view) {
    TextView textView = findViewById(R.id.result);
    textView.setText(getDate());
    //TODO: Convert(date, val1, val2): return res
    //TODO: add_to_history(db, ...);
  }
}
