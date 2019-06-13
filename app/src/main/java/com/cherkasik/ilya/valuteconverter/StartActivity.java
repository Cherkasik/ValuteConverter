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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

 public class StartActivity extends AppCompatActivity {
  private SQLiteDatabase db;
  private Spinner spinnerFrom;
  private Spinner spinnerTo;
  private RequestQueue mQueue;
  private EditText inputText;
  private String curDate;
  private TextView resText;
  private String curFrom;
  private String curTo;
  private Float inputVal;
  private String today;

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
    inputText = findViewById(R.id.input);
    resText = findViewById(R.id.result);
    today = getDate();
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
          list. add("Российский рубль");
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
    String day = String.valueOf(datePicker.getDayOfMonth());
    String month = String.valueOf(datePicker.getMonth() + 1);
    String year = String.valueOf(datePicker.getYear());
    if (Integer.valueOf(day) < 10) day = "0" + day;
    if (Integer.valueOf(month) < 10) month = "0" + month;
    return year + "/" + month + "/" + day;
  }

  public void convert(View view) throws InterruptedException {
    curDate = getDate();
    curFrom = spinnerFrom.getSelectedItem().toString();
    curTo = spinnerTo.getSelectedItem().toString();
    String curValue = inputText.getText().toString();
    if (curValue.equals("")){
        error("input. Actually it is empty");
        return;
    }
    else{
        inputVal = Float.valueOf(curValue);
    }
    if (!curTo.equals(curFrom)){
        mQueue = Volley.newRequestQueue(this);
        String url;
        if (!curDate.equals(today)) {
            //TODO: something wrong (debug message dkwtd)
            url = "https://www.cbr-xml-daily.ru/archive/" + curDate + "/daily_json.js";
        }
        else {
            url = "https://www.cbr-xml-daily.ru/daily_json.js";
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("Valute");
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()){
                        String key = keys.next();
                        JSONObject valute = jsonObject.getJSONObject(key);
                        if (curFrom.equals(valute.getString("Name"))){
                            inputVal = inputVal / Float.valueOf(valute.getString("Value"));
                        }
                        if (curTo.equals(valute.getString("Name"))){
                            inputVal = inputVal * Float.valueOf(valute.getString("Value"));
                        }
                    }
                    resText.setText(String.valueOf(inputVal));
                } catch (JSONException e) {
                    error("response");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error("date, try another one");
            }
        });
        mQueue.add(request);
    }
    else{
        resText.setText(String.valueOf(inputVal));
    }
    //TODO: add_to_history(db, ...);
  }
}
