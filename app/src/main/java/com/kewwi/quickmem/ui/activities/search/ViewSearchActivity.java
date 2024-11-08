package com.kewwi.quickmem.ui.activities.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.kewwi.quickmem.adapter.flashcard.SetAllAdapter;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.databinding.ActivityViewSearchBinding;

import java.util.ArrayList;

//quản lý logic và giao diện tìm kiếm trong ứng dụng QuickMem,
// sử dụng SearchView để tìm kiếm FlashCard và hiển thị kết quả tương ứng.
public class ViewSearchActivity extends AppCompatActivity {
    private ActivityViewSearchBinding binding;// Biến binding giúp liên kết các thành phần giao diện với mã logic.
    private ArrayList<FlashCard> flashCards;// Danh sách FlashCard sẽ được hiển thị và tìm kiếm.
    private FlashCardDAO flashCardDAO;// DAO (Data Access Object) để thao tác với cơ sở dữ liệu FlashCard.
    private SetAllAdapter setAllAdapter;// Adapter để hiển thị danh sách FlashCard trong RecyclerView.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo và thiết lập giao diện thông qua lớp binding.
        binding = ActivityViewSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập các thành phần trong Activity.
        setupBackButton();// Nút quay lại.
        setupData();// Thiết lập dữ liệu từ DAO.
        setupSets(); // Thiết lập và hiển thị danh sách FlashCard.
        setupSearchView();// Thiết lập chức năng tìm kiếm.
    }

    // Phương thức thiết lập nút quay lại.
    private void setupBackButton() {
        // Khi người dùng bấm nút quay lại, Activity sẽ đóng lại.
        binding.backIv.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Phương thức thiết lập DAO để thao tác với cơ sở dữ liệu.
    private void setupData() {
        flashCardDAO = new FlashCardDAO(this);
    }

    // Phương thức lấy dữ liệu FlashCard từ DAO và hiển thị trong RecyclerView.
    private void setupSets() {
        // Lấy tất cả các FlashCard công khai từ DAO.
        flashCards = flashCardDAO.getAllFlashCardPublic();

        // Khởi tạo adapter với danh sách FlashCard và thiết lập RecyclerView.
        setAllAdapter = new SetAllAdapter(this, flashCards);
        binding.setsRv.setLayoutManager(new LinearLayoutManager(this));
        binding.setsRv.setAdapter(setAllAdapter);

        // Hiển thị hoặc ẩn phần giao diện dựa trên việc danh sách FlashCard có rỗng hay không.
        binding.setsCl.setVisibility(flashCards.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // Phương thức thiết lập SearchView cho chức năng tìm kiếm.
    private void setupSearchView() {
        // Lắng nghe sự kiện nhập và thay đổi văn bản trong SearchView.
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            // Không thực hiện tìm kiếm khi bấm "Enter".
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Gọi phương thức xử lý khi văn bản trong SearchView thay đổi.
                handleSearchQuery(newText);
                return true;
            }
        });
    }

    // Phương thức xử lý tìm kiếm khi người dùng nhập văn bản.
    private void handleSearchQuery(String newText) {
        // Danh sách tạm để chứa các FlashCard phù hợp với từ khóa tìm kiếm.
        ArrayList<FlashCard> filteredFlashCards = new ArrayList<>();
        for (FlashCard flashCard : flashCards) {
            // So sánh tên FlashCard với từ khóa tìm kiếm (không phân biệt chữ hoa chữ thường).
            if (flashCard.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredFlashCards.add(flashCard);
            }
        }
        // Cập nhật adapter với danh sách FlashCard đã lọc.
        updateAdapters(filteredFlashCards);

        // Cập nhật hiển thị dựa trên kết quả tìm kiếm.
        updateVisibility(newText, filteredFlashCards);
    }

    // Phương thức cập nhật adapter với danh sách FlashCard đã lọc.
    private void updateAdapters(ArrayList<FlashCard> flashCards) {
        setAllAdapter = new SetAllAdapter(this, flashCards);
        binding.setsRv.setAdapter(setAllAdapter);
    }

    // Phương thức cập nhật hiển thị dựa trên từ khóa và kết quả tìm kiếm.
    private void updateVisibility(String newText, ArrayList<FlashCard> flashCards) {
        boolean isSearchEmpty = newText.isEmpty();
        boolean isFlashCardsEmpty = flashCards.isEmpty();
        // Nếu không có kết quả hoặc không có tìm kiếm, ẩn phần hiển thị danh sách.
        binding.setsCl.setVisibility(isSearchEmpty || isFlashCardsEmpty ? View.GONE : View.VISIBLE);

        // Hiển thị gợi ý "Nhập chủ đề" nếu chưa có từ khóa tìm kiếm.
        binding.enterTopicTv.setVisibility(isSearchEmpty ? View.VISIBLE : View.GONE);

        // Hiển thị "Không có kết quả" nếu không tìm thấy FlashCard nào phù hợp.
        binding.noResultTv.setVisibility(isSearchEmpty || !isFlashCardsEmpty ? View.GONE : View.VISIBLE);
    }
}