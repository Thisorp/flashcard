package com.kewwi.quickmem.ui.fragments.home;

public class Book {
    private int imageResId;
    private String title;
    private String url;

    public Book(int imageResId, String title, String url) {
        this.imageResId = imageResId;
        this.title = title;
        this.url = url;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
