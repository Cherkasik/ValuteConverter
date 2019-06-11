package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Locale;

public class StartActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);
    // TODO: addItemOnSpinner(id_spinner, array items)
    // TODO: listenCalendarDate()
    // TODO: Convert(date, val1, val2): return res
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_start, menu);
    return true;
  }

  public void onSettingsMenuClicked(MenuItem item) {
    Intent intent = new Intent(StartActivity.this, HistoryActivity.class);
    startActivity(intent);
  }

  public void convert(View view) {
    DatePicker datePicker = findViewById(R.id.calendar);
    int day = datePicker.getDayOfMonth();
    int month = datePicker.getMonth() + 1;
    int year = datePicker.getYear();
    TextView textView = findViewById(R.id.result);
    textView.setText(String.format(Locale.US, "%d %d %d", day, month, year));
  }
}
