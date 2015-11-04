package com.test.rss;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.test.rss.saxrssreader.RssFeed;
import com.test.rss.saxrssreader.RssItem;
import com.test.rss.saxrssreader.RssReader;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by d.barkalov on 04.11.2015.
 */
public class LoadRssService extends IntentService {

    public static final String LOAD_RSS_ACTION = "com.test.rss.LoadRssService.LOAD_RSS_ACTION";

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
                List<RssItem> items = loadRss(url);
                for (final RssItem rssItem : items) {
                    Log.d("LoadRssService", rssItem.getTitle());
                }

                if (items != null && items.size() > 0) {
                    final RssItem firsItem = items.get(0);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            RssWidgetProvider.updateAppWidget(
                                    LoadRssService.this.getApplicationContext(),
                                    appWidgetManager,
                                    id,
                                    firsItem.getTitle(),
                                    android.text.Html.fromHtml(firsItem.getDescription()).toString()
                            );
                        }
                    });
                }

            } catch (IOException e) {
                Log.e("LoadRssService", e.toString());
            } catch (SAXException e) {
                Log.e("LoadRssService", e.toString());
            }
        }
    }

    private List<RssItem> loadRss(String urlStr) throws IOException, SAXException {
        Log.d("LoadRssService", "loadRss " + urlStr);
        URL url = new URL(urlStr);
        RssFeed feed = RssReader.read(url);
        return feed.getRssItems();
    }


}
