package com.test.rss;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<WidgetItem> mWidgetItems = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        mWidgetItems = loadRss(mAppWidgetId);
    }

    public void onDestroy() {
        mWidgetItems.clear();
    }

    public int getCount() {
        return mWidgetItems.size();
    }

    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.rss_widget_item);
        rv.setTextViewText(R.id.title, mWidgetItems.get(position).getTitle());
        rv.setTextViewText(R.id.text, mWidgetItems.get(position).getText());
        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    private List<WidgetItem> loadRss(int widgetId) {
        List<WidgetItem> list = new ArrayList<>();
        try {
            SharedPreferences prefs = mContext.getSharedPreferences(LoadRssService.RSS_PREFS_NAME, 0);
            String rss = prefs.getString(LoadRssService.PREF_PREFIX_KEY + widgetId, null);
            if (!TextUtils.isEmpty(rss)) {
                JSONArray array = new JSONArray(rss);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    list.add(new WidgetItem(object.getString("title"), object.getString("text")));
                }
            }
        } catch (JSONException e) {
            Log.e("loadRss", e.toString());
        }
        return list;
    }

    public void onDataSetChanged() {
        mWidgetItems = loadRss(mAppWidgetId);
    }
}