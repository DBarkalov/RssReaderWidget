package com.test.rss;

public class WidgetItem {
    private final String title;
    private final String text;

    public WidgetItem(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }
}
