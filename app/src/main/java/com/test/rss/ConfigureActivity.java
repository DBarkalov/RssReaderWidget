package com.test.rss;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

/**
 * Created by d.barkalov on 03.11.2015.
 */
public class ConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "com.test.rss.widget";
    private static final String PREF_PREFIX_KEY = "rss_url_";

    private int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText mRssUrlText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.configure_activity);
        getSupportActionBar().setTitle(R.string.title);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        mRssUrlText = (EditText) findViewById(R.id.rss_url_text);
        String url = loadRssUrl(this, mWidgetId);
        if (!TextUtils.isEmpty(url)) {
            mRssUrlText.setText(url);
        }

        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveButtonClick();
            }
        });

    }

    private void onSaveButtonClick() {
        if (!TextUtils.isEmpty(mRssUrlText.getText().toString())) {
            saveRssUrl(this, mWidgetId, mRssUrlText.getText().toString());
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        } else {
            //todo show error
        }
    }

    static void saveRssUrl(Context context, int widgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + widgetId, text);
        prefs.commit();
    }

    static String loadRssUrl(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY + widgetId, "https://news.mail.ru/rss/90/");
    }

}
