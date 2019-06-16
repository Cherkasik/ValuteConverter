package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class StartActivity extends AppCompatActivity {
    private Spinner spinnerFrom;
    private DatabaseDAO databaseDAO;
    private Spinner spinnerTo;
    private EditText inputText;
    private TextView resText;
    private String today;
    private RequestCB requestCB = new RequestCB();

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
        requestCB.getValutes(this, spinnerFrom, spinnerTo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    }

    public void onHistoryMenuClicked(MenuItem item) {
        Intent intent = new Intent(StartActivity.this, HistoryActivity.class);
        startActivity(intent);
    }

    public void onExchangeMenuClicked(MenuItem item){
        Intent intent = new Intent(StartActivity.this, ExchangeRateActivity.class);
        startActivity(intent);
    }

    private String getDate() {
        DatePicker datePicker = findViewById(R.id.calendar);
        String day = String.valueOf(datePicker.getDayOfMonth());
        String month = String.valueOf(datePicker.getMonth() + 1);
        String year = String.valueOf(datePicker.getYear());
        if (Integer.valueOf(day) < 10) day = "0" + day;
        if (Integer.valueOf(month) < 10) month = "0" + month;
        return year + "/" + month + "/" + day;
    }

    public void convert(View view) {
        String curDate = getDate();
        String curFrom = spinnerFrom.getSelectedItem().toString();
        String curTo = spinnerTo.getSelectedItem().toString();
        String curValue = inputText.getText().toString();
        Float inputVal;
        if (curValue.equals("")) {
            requestCB.error("input. Actually it is empty", this);
            return;
        } else {
            inputVal = Float.valueOf(curValue);
        }
        if (!curTo.equals(curFrom)) {

            requestCB.convertRequest(this, today, curFrom, curTo, inputVal, curDate, resText, databaseDAO);
        } else {
            resText.setText(curValue);
            HistoryObject historyObject = new HistoryObject(curFrom, curTo, inputVal, inputVal, curDate);
            databaseDAO.addHistory(historyObject);
        }
    }
}
