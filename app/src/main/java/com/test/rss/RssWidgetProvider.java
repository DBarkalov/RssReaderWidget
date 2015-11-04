package com.test.rss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by d.barkalov on 03.11.2015.
 */
public class RssWidgetProvider  extends AppWidgetProvider {

    private static final String TAG = "RssWidgetProvider";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate");
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId, "loading...", "" );
        }
    }


    PendingIntent getLoadIntent(Context context){
        Intent intent = new Intent(context, LoadRssService.class);
        intent.setAction(LoadRssService.LOAD_RSS_ACTION);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 1, getLoadIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        AlarmManager alarm = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(getLoadIntent(context));
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int widgetId, String title, String text) {
        Log.d(TAG, "updateAppWidget");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rss_widget);
        views.setTextViewText(R.id.title, title);
        views.setTextViewText(R.id.text, text);
        views.setOnClickPendingIntent(R.id.next, getNextIntent(context));
        views.setOnClickPendingIntent(R.id.prev, getPrevIntent(context));


        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private static PendingIntent getPrevIntent(Context context) {
        return null;
    }

    private static PendingIntent getNextIntent(Context context) {
        return null;
    }

}
