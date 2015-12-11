package com.test.rss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

/**
 * Created by d.barkalov on 03.11.2015.
 */
public class RssWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "RssWidgetProvider";
    private static final String ACTION_PREV = "com.test.rss.RssWidgetProvider.PREV";
    private static final String ACTION_NEXT = "com.test.rss.RssWidgetProvider.NEXT";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.rss_widget);
            rv.setRemoteAdapter(R.id.stack_view, intent);
            rv.setOnClickPendingIntent(R.id.next, getNextIntent(appWidgetIds[i], context));
            rv.setOnClickPendingIntent(R.id.prev, getPrevIntent(appWidgetIds[i], context));
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_NEXT) || intent.getAction().equals(ACTION_PREV)) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.rss_widget);
            if (intent.getAction().equals(ACTION_NEXT)) {
                rv.showNext(R.id.stack_view);
            } else if (intent.getAction().equals(ACTION_PREV)) {
                rv.showPrevious(R.id.stack_view);
            }
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int widgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            mgr.updateAppWidget(widgetId, rv);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 1, getLoadIntent(context));
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(getLoadIntent(context));
    }

    private PendingIntent getLoadIntent(Context context) {
        Intent intent = new Intent(context, LoadRssService.class);
        intent.setAction(LoadRssService.LOAD_RSS_ACTION);
        return PendingIntent.getService(context, 0, intent, 0);
    }

    private static PendingIntent getPrevIntent(int appWidgetId, Context context) {
        Intent intent = new Intent(context, RssWidgetProvider.class);
        intent.setAction(ACTION_PREV);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getNextIntent(int appWidgetId, Context context) {
        Intent intent = new Intent(context, RssWidgetProvider.class);
        intent.setAction(ACTION_NEXT);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        return PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
