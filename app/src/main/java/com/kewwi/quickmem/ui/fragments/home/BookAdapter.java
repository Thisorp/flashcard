package com.kewwi.quickmem.ui.fragments.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.kewwi.quickmem.R;

import java.util.ArrayList;

public class BookAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Book> books;

    public BookAdapter(Context context, ArrayList<Book> books) {
        this.context = context;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_book, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.item_image);
        TextView textView = convertView.findViewById(R.id.item_text);

        Book book = books.get(position);
        imageView.setImageResource(book.getImageResId()); // Sử dụng ảnh từ resource ID
        textView.setText(book.getTitle());

        return convertView;
    }
}
