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
        books.add(new Book(R.drawable.genesis, "GENESIS - BOOK 1", "https://ebook-mecca.com/online/Genesis%20-%20Nicolette%20Fuller.pdf"));
        books.add(new Book(R.drawable.alien, "ALIEN CRADLE", "https://ebook-mecca.com/online/Alien%20Cradle%20-%20Jeff%20Inlo.pdf"));
        books.add(new Book(R.drawable.td, "THE DREAM IN THE WITCH HOUSE", "https://ebook-mecca.com/online/The%20Dreams%20in%20the%20Witch-Hous%20-%20H.P.%20Lovecraft.pdf"));
        books.add(new Book(R.drawable.theoddofhappily, "THE ODDS OF HAPPILY EVER AFTER", "https://continuous.epub.pub/epub/6732b39d5a53857c1bde7e55"));
        books.add(new Book(R.drawable.stalker, "STALKER", "https://continuous.epub.pub/epub/6731a5ef5a53857c1bde7e27"));
        books.add(new Book(R.drawable.takeher, "TAKE HER TO THE GRAVE", "https://continuous.epub.pub/epub/673027165a53857c1bde7e0a"));
        books.add(new Book(R.drawable.forever, "FORERVER WITH YOU", "https://continuous.epub.pub/epub/6731a5f35a53857c1bde7e2c"));

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
