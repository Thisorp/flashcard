package com.kewwi.quickmem.ui.activities.create;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.adapter.card.CardAdapter;
import com.kewwi.quickmem.data.dao.CardDAO;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.model.Card;

import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.databinding.ActivityCreateSetBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.set.ViewSetActivity;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
//Tạo và quản lý giao diện người dùng cho hoạt động tạo "set" flashcard mới.
// Chức năng chính của file này bao gồm tạo, lưu và quản lý các thẻ ghi nhớ (cards) trong một set,
// xử lý thao tác vuốt để xóa thẻ,
// và hiển thị số lượng thẻ hiện tại.

// Lớp CreateSetActivity là lớp quản lý giao diện người dùng
// và các hành động liên quan đến việc tạo bộ thẻ flashcard.
public class CreateSetActivity extends AppCompatActivity {
    private CardAdapter cardAdapter;// Adapter hiển thị danh sách thẻ ghi nhớ
    private ArrayList<Card> cards;// Danh sách thẻ
    private ActivityCreateSetBinding binding;// Liên kết giao diện người dùng với mã nguồn
    private final String id = genUUID();// ID duy nhất của bộ thẻ


    // Phương thức onCreate thiết lập giao diện khi hoạt động được tạo
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateSetBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập giao diện, các hành động cho toolbar và danh sách thẻ.
        setupToolbar();
        setupSubjectEditText();
        setupDescriptionTextView();
        setupCardsList();
        setupCardAdapter();
        setupAddFab();
        setupItemTouchHelper();
    }

    // Thiết lập thanh công cụ và hành động quay lại
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
    // Thiết lập EditText để nhập tiêu đề cho bộ thẻ
    private void setupSubjectEditText() {
        if (binding.subjectEt.getText().toString().isEmpty()) {
            binding.subjectEt.requestFocus();
        }
    }
    // Thiết lập hành động khi nhấp vào TextView mô tả, để hiện hoặc ẩn TextInputLayout
    private void setupDescriptionTextView() {
        binding.descriptionTv.setOnClickListener(v -> {
            if (binding.descriptionTil.getVisibility() == View.GONE) {
                binding.descriptionTil.setVisibility(View.VISIBLE);
            } else {
                binding.descriptionTil.setVisibility(View.GONE);
            }
        });
    }

    // Thiết lập danh sách thẻ và cập nhật số lượng thẻ
    private void setupCardsList() {
        //create list two set
        cards = new ArrayList<>();
        cards.add(new Card());
        cards.add(new Card());
        updateTotalCards();
    }

    // Cập nhật số lượng thẻ hiện tại
    private void updateTotalCards() {
        binding.totalCardsTv.setText(String.format("Total Cards: %s", cards.size()));
    }

    // Thiết lập adapter cho danh sách thẻ và cập nhật khi có thay đổi
    @SuppressLint("NotifyDataSetChanged")
    private void setupCardAdapter() {
        cardAdapter = new CardAdapter(this, cards);
        binding.cardsLv.setAdapter(cardAdapter);
        binding.cardsLv.setLayoutManager(new LinearLayoutManager(this));
        binding.cardsLv.setHasFixedSize(true);
        cardAdapter.notifyDataSetChanged();

    }

    // Thiết lập nút "thêm" thẻ mới
    private void setupAddFab() {
        binding.addFab.setOnClickListener(v -> {
            if (!checkTwoCardsEmpty()) {

                Card newCard = new Card();
                cards.add(newCard);
                //scroll to last item
                binding.cardsLv.smoothScrollToPosition(cards.size() - 1);
                //notify adapter
                cardAdapter.notifyItemInserted(cards.size() - 1);
                updateTotalCards();

            } else {
                Toast.makeText(this, "Please enter front and back", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // Thiết lập thao tác vuốt để xóa thẻ
    private void setupItemTouchHelper() {
        ItemTouchHelper.SimpleCallback callback = createItemTouchHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.cardsLv);
    }

    // Tạo callback cho thao tác vuốt
    private ItemTouchHelper.SimpleCallback createItemTouchHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                handleOnSwiped(viewHolder);
            }

            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                handleOnChildDraw(c, viewHolder, dX);
            }
        };
    }

    // Xử lý khi một thẻ bị vuốt để xóa
    private void handleOnSwiped(RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getBindingAdapterPosition();

        // Sao lưu thẻ bị xóa
        Card deletedItem = cards.get(position);

        // Xóa thẻ khỏi danh sách
        cards.remove(position);
        updateTotalCards();
        cardAdapter.notifyItemRemoved(position);

        // Hiển thị thông báo Snack bar với tùy chọn "Hoàn tác"
        Snackbar snackbar = Snackbar.make(binding.getRoot(), "Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", view -> {

            // Check if the position is valid before adding the item back
            if (position >= 0 && position <= cards.size()) {
                cards.add(position, deletedItem);
                cardAdapter.notifyItemInserted(position);
                updateTotalCards();
            } else {
                // If the position isn't valid, show a message or handle the error appropriately
                Toast.makeText(getApplicationContext(), "Error restoring item", Toast.LENGTH_LONG).show();
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    // Xử lý vẽ lại giao diện khi vuốt thẻ
    private void handleOnChildDraw(@NotNull Canvas c, @NotNull RecyclerView.ViewHolder viewHolder, float dX) {
        Drawable icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete);
        View itemView = viewHolder.itemView;
        assert icon != null;
        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX < 0) { // Vuốt sang trái
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            final ColorDrawable background = new ColorDrawable(Color.WHITE);
            background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);
        } else { // Không vuốt
            icon.setBounds(0, 0, 0, 0);
        }

        icon.draw(c);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);

    }

    // Thiết lập menu khi tạo
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_set, menu);
        return true;

    }

    // Xử lý khi chọn các mục trong menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            saveChanges();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    // Lưu tất cả thay đổi sau khi tạo bộ thẻ
    private void saveChanges() {
        String subject = binding.subjectEt.getText().toString();
        String description = binding.descriptionEt.getText().toString();

        // Kiểm tra nếu tiêu đề trống, yêu cầu nhập tiêu đề
        if (subject.isEmpty()) {
            binding.subjectTil.setError("Please enter subject");
            binding.subjectEt.requestFocus();
            return;
        } else {
            binding.subjectTil.setError(null);
        }

        // Lưu các thẻ, nếu lưu thất bại thì ngừng xử lý
        if (!saveAllCards()) {
            return;
        }

        // Lưu flashcard, nếu thất bại thì thông báo lỗi
        if (!saveFlashCard(subject, description)) {
            Toast.makeText(this, "Insert flashcard failed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sau khi lưu thành công, chuyển sang màn hình xem bộ thẻ vừa tạo
        Intent intent = new Intent(this, ViewSetActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    // Lưu tất cả các thẻ trong danh sách
    private boolean saveAllCards() {
        for (Card card : cards) {
            if (!saveCard(card)) {
                return false;// Nếu có bất kỳ thẻ nào không lưu được, trả về false
            }
        }
        return true;// Tất cả thẻ được lưu thành công
    }

    // Lưu một thẻ cụ thể
    private boolean saveCard(Card card) {
        String front = card.getFront();// Lấy mặt trước của thẻ
        String back = card.getBack();// Lấy mặt sau của thẻ

        // Kiểm tra nếu mặt trước hoặc mặt sau của thẻ trống, yêu cầu nhập dữ liệu
        if (front == null || front.isEmpty()) {
            binding.cardsLv.requestFocus();
            Toast.makeText(this, "Please enter front", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (back == null || back.isEmpty()) {
            binding.cardsLv.requestFocus();
            Toast.makeText(this, "Please enter back", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Khởi tạo DAO và lưu thẻ vào cơ sở dữ liệu
        CardDAO cardDAO = new CardDAO(this);
        card.setId(genUUID());
        card.setFront(front);
        card.setBack(back);
        card.setStatus(0);// Đặt trạng thái mặc định
        card.setIsLearned(0);// Đặt trạng thái chưa học
        card.setFlashcard_id(id);// Gắn thẻ với ID bộ flashcard
        card.setCreated_at(getCurrentDate());// Lấy ngày hiện tại
        card.setUpdated_at(getCurrentDate());// Lấy ngày hiện tại

        // Nếu việc lưu vào cơ sở dữ liệu thất bại, trả về false
        if (cardDAO.insertCard(card) <= 0) {
            Toast.makeText(this, "Insert card failed" + id, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;// Thẻ được lưu thành công
    }

    // Lưu thông tin flashcard bao gồm tiêu đề và mô tả
    private boolean saveFlashCard(String subject, String description) {
        FlashCardDAO flashCardDAO = new FlashCardDAO(this);
        FlashCard flashCard = new FlashCard();
        flashCard.setName(subject);// Đặt tên flashcard
        flashCard.setDescription(description);// Đặt mô tả
        UserSharePreferences userSharePreferences = new UserSharePreferences(this);
        flashCard.setUser_id(userSharePreferences.getId());// Lấy ID người dùng từ SharedPreferences
        flashCard.setCreated_at(getCurrentDate()); // Lấy ngày hiện tại
        flashCard.setUpdated_at(getCurrentDate());// Lấy ngày hiện tại
        flashCard.setId(id);// Gắn ID bộ flashcard

        // Xử lý thay đổi trạng thái riêng tư/công khai
        binding.privateSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "Public", Toast.LENGTH_SHORT).show();
                flashCard.setIs_public(1);// Đặt bộ thẻ ở trạng thái công khai
            } else {
                Toast.makeText(this, "Private", Toast.LENGTH_SHORT).show();
                flashCard.setIs_public(0);// Đặt bộ thẻ ở trạng thái riêng tư
            }
        });

        // Lưu flashcard vào cơ sở dữ liệu, trả về true nếu thành công, false nếu thất bại
        return flashCardDAO.insertFlashCard(flashCard) > 0;
    }

    // Lấy ngày hiện tại dưới dạng chuỗi, kiểm tra phiên bản API để định dạng ngày
    private String getCurrentDate() {
        // Dành cho API mới
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return getCurrentDateNewApi();
        } else {
            return getCurrentDateOldApi();// Dành cho API cũ
        }
    }

    // Kiểm tra nếu có ít nhất 2 thẻ trống (cả mặt trước và mặt sau đều trống)
    public boolean checkTwoCardsEmpty() {
        // check if 2 cards are empty return true
        int emptyCount = 0;
        for (Card card : cards) {
            if (card.getFront() == null || card.getFront().isEmpty() || card.getBack() == null || card.getBack().isEmpty()) {
                emptyCount++;// Đếm số thẻ trống
                if (emptyCount == 2) {
                    return true;// Nếu có ít nhất 2 thẻ trống, trả về true
                }
            }
        }
        return false;// Không có đủ 2 thẻ trống
    }

    // Sinh UUID duy nhất cho các đối tượng
    private String genUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    // Định dạng ngày hiện tại cho API mới (từ Android O trở lên)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDateNewApi() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return currentDate.format(formatter);
    }

    // Định dạng ngày hiện tại cho API cũ (dưới Android O)
    private String getCurrentDateOldApi() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }
}