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

 public class StartActivity extends AppCompatActivity {
  private Spinner spinnerFrom;
  private DatabaseDAO databaseDAO;
  private Spinner spinnerTo;
  private RequestQueue mQueue;
  private EditText inputText;
  private String curDate;
  private TextView resText;
  private String curFrom;
  private String curTo;
  private Float inputVal;
  private String today;
  private String curValue;


  private void error(String name){
    Toast.makeText(getApplicationContext(), "Something wrong with " + name, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    databaseDAO = DatabaseDAO.getsInstance(this);
    setContentView(R.layout.activity_start);
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
    curValue = inputText.getText().toString();
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
                            inputVal = inputVal * Float.valueOf(valute.getString("Value"));
                        }
                        if (curTo.equals(valute.getString("Name"))){
                            inputVal = inputVal / Float.valueOf(valute.getString("Value"));
                        }
                    }
                    resText.setText(String.valueOf(inputVal));
                    HistoryObject historyObject = new HistoryObject(curFrom, curTo, Float.valueOf(curValue), inputVal, curDate);
                    databaseDAO.addHistory(historyObject);
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
        HistoryObject historyObject = new HistoryObject(curFrom, curTo, Float.valueOf(curValue), inputVal, curDate);
        databaseDAO.addHistory(historyObject);
    }
  }
}
