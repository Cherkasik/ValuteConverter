package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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

}
