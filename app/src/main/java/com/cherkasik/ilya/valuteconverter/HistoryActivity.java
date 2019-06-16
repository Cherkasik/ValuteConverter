package com.cherkasik.ilya.valuteconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private static DatabaseDAO databaseDAO;
    private TextView[] textViews;

    protected void onCreate(Bundle savedInstanceState) {
        databaseDAO = DatabaseDAO.getsInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        linearLayout = findViewById(R.id.history);
        textViews = new TextView[10];
        displayHistoryElements();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    public void onSettingsMenuClicked(MenuItem item) {
        if (databaseDAO.getHistory().size() > 0){
            databaseDAO.deleteHistory();
            linearLayout.removeAllViews();
            displayHistoryElements();
        }
    }

    public void onHistoryExchangeMenuClicked(MenuItem item){
        Intent intent = new Intent(HistoryActivity.this, ExchangeRateActivity.class);
        startActivity(intent);
    }

    public void onHistoryConverterMenuClicked(MenuItem item){
        Intent intent = new Intent(HistoryActivity.this, StartActivity.class);
        startActivity(intent);
    }

    private void displayHistoryElements(){
        List<HistoryObject> historyObjects;
        historyObjects = databaseDAO.getHistory();
        for (int i = 0; i < historyObjects.size(); i++) {
            textViews[i] = new TextView(this);
            textViews[i].setText(String.format(Locale.US,
                    "From: %s\nTo: %s\nBased on: %s\nInput value: %f\nResult: %f\n",
                    historyObjects.get(i).conv_from, historyObjects.get(i).conv_to,
                    historyObjects.get(i).date, historyObjects.get(i).num, historyObjects.get(i).res));
            textViews[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textViews[i].setTextSize(20);
            linearLayout.addView(textViews[i]);
        }
    }
}
