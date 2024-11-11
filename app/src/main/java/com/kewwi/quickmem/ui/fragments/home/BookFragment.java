package com.kewwi.quickmem.ui.fragments.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.kewwi.quickmem.R;

import java.util.ArrayList;

public class BookFragment extends Fragment {

    private ListView bookListView;
    private ArrayList<Book> books;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tạo danh sách các sách với ảnh và tiêu đề
        books = new ArrayList<>();
        books.add(new Book(R.drawable.alice, "ALICE IN WONDERLAND", "https://www.adobe.com/be_en/active-use/pdf/Alice_in_Wonderland.pdf"));
        books.add(new Book(R.drawable.gonewiththewind, "GONE WITH THE WIND", "https://ebook-mecca.com/online/gonewiththewind.pdf"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bk, container, false);
        bookListView = view.findViewById(R.id.book_list_view);

        BookAdapter adapter = new BookAdapter(getContext(), books);
        bookListView.setAdapter(adapter);

        // Xử lý sự kiện click vào item để mở link
        bookListView.setOnItemClickListener((parent, view1, position, id) -> {
            String url = books.get(position).getUrl();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        return view;
    }
}
