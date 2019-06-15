package com.cherkasik.ilya.valuteconverter;

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

    protected void onCreate(Bundle savedInstanceState) {
        databaseDAO = DatabaseDAO.getsInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        linearLayout = findViewById(R.id.history);
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
        }
    }

    private void displayHistoryElements(){
        List<HistoryObject> historyObjects;
        historyObjects = databaseDAO.getHistory();
        for (HistoryObject historyObject : historyObjects) {
            TextView textView = new TextView(this);
            textView.setText(String.format(Locale.US,
                    "From %s\n To %s\n Based on %s\n Input value: %f\n Result: %f",
                    historyObject.conv_from, historyObject.conv_to,
                    historyObject.date, historyObject.num, historyObject.res));
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(textView);
        }
    }
}
