package com.test.rss;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.test.rss.saxrssreader.RssFeed;
import com.test.rss.saxrssreader.RssItem;
import com.test.rss.saxrssreader.RssReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by d.barkalov on 04.11.2015.
 */
public class LoadRssService extends IntentService {

    public static final String LOAD_RSS_ACTION = "com.test.rss.LoadRssService.LOAD_RSS_ACTION";
    public static final String RSS_PREFS_NAME = "com.test.rss.LoadRssService.Rss.Pref";
    public static final String PREF_PREFIX_KEY = "rss_";

    public LoadRssService() {
        super("LoadRssService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("LoadRssService", "onHandleIntent");
        if (intent.getAction().equals(LOAD_RSS_ACTION)) {
            Log.d("LoadRssService", "LOAD_RSS_ACTION");
        }

        ComponentName widget = new ComponentName(this, RssWidgetProvider.class);
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(widget);

        for (final int id : widgetIds) {
            String url = ConfigureActivity.loadRssUrl(this, id);
            try {
                List<RssItem> items = downloadRss(url);
                for (final RssItem rssItem : items) {
                    Log.d("LoadRssService", rssItem.getTitle());
                }
                //todo use content provider, sqlite ...
                saveRss(id, items);
                //todo check if lastmodify
                appWidgetManager.notifyAppWidgetViewDataChanged(id, R.id.stack_view);
            } catch (IOException e) {
                Log.e("LoadRssService", e.toString());
            } catch (SAXException e) {
                Log.e("LoadRssService", e.toString());
            }
        }
    }

    private void saveRss(int id, List<RssItem> items) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (final RssItem rssItem : items) {
                JSONObject object = new JSONObject();
                object.put("title", rssItem.getTitle());
                object.put("text", android.text.Html.fromHtml(rssItem.getDescription()).toString());
                jsonArray.put(object);
            }
            saveRss(this, id, jsonArray.toString());
        } catch (JSONException e) {
            Log.e("LoadRssService", e.toString());
        }
    }

    static void saveRss(Context context, int widgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(RSS_PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + widgetId, text);
        prefs.commit();
    }

    private List<RssItem> downloadRss(String urlStr) throws IOException, SAXException {
        Log.d("LoadRssService", "downloadRss " + urlStr);
        URL url = new URL(urlStr);
        RssFeed feed = RssReader.read(url);
        return feed.getRssItems();
    }


}
