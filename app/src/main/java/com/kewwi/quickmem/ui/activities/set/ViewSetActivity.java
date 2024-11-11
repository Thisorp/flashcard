package com.kewwi.quickmem.ui.activities.set;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.ui.activities.learn.TrueFalseFlashCardsActivity;
import com.kewwi.quickmem.adapter.card.ViewTermsAdapter;
import com.kewwi.quickmem.adapter.card.ViewSetAdapter;
import com.kewwi.quickmem.data.dao.CardDAO;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.Card;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ActivityViewSetBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.folder.AddToFolderActivity;
import com.kewwi.quickmem.ui.activities.group.AddToClassActivity;
import com.kewwi.quickmem.ui.activities.learn.LearnActivity;
import com.kewwi.quickmem.ui.activities.learn.QuizActivity;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class ViewSetActivity extends AppCompatActivity {
    // Biến liên quan đến giao diện và các lớp quản lý dữ liệu
    private ActivityViewSetBinding binding;
    private CardDAO cardDAO;
    private FlashCardDAO flashCardDAO;
    private ArrayList<Card> cards;
    private LinearLayoutManager linearLayoutManager;
    private static final String LIST_POSITION = "list_position";
    private int listPosition = 0;
    private UserSharePreferences userSharePreferences;
    private String idCard;

    // Phương thức onCreate() khởi tạo giao diện và các thành phần cần thiết cho Activity
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo Binding và thiết lập giao diện
        binding = ActivityViewSetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Khởi tạo các đối tượng liên quan đến việc quản lý dữ liệu
        userSharePreferences = new UserSharePreferences(this);
        flashCardDAO = new FlashCardDAO(this);

        // Thiết lập RecyclerView và các phương thức liên quan
        setupRecyclerView(savedInstanceState);
        setupCardData();
        setupNavigationListener();
        setupScrollListeners();
        setupOnScrollListener();
        setupUserDetails();
        setupReviewClickListener();
        setupLearnClickListener();
        setTrueFalseClickListener();
        setupToolbarNavigation();
    }

    // Thiết lập hành động khi người dùng nhấn vào phần "True/False" để học các FlashCards
    private void setTrueFalseClickListener() {
        binding.trueFalseCl.setOnClickListener(v -> {
            // Kiểm tra nếu người dùng không phải là chủ sở hữu, sẽ hiển thị lỗi
            if (!isUserOwner()) {
                showLearnErrorDialog();
            } else {
                // Nếu là chủ sở hữu, chuyển sang activity học các FlashCards với True/False
                Intent intent = new Intent(this, TrueFalseFlashCardsActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });
    }

    // Lắng nghe sự kiện cuộn của RecyclerView để cập nhật vị trí của item hiện tại
    private void setupOnScrollListener() {
        binding.recyclerViewSet.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int centerPosition = linearLayoutManager.findFirstVisibleItemPosition() + 1;
                binding.centerTv.setText(String.valueOf(centerPosition));
                binding.previousTv.setText(centerPosition > 1 ? String.valueOf(centerPosition - 1) : "");
                binding.nextTv.setText(centerPosition < cards.size() ? String.valueOf(centerPosition + 1) : "");
            }
        });
    }

    // Thiết lập thông tin người dùng, bao gồm avatar, tên và mô tả bộ flashcard
    @SuppressLint("SetTextI18n")
    private void setupUserDetails() {
        String id = getIntent().getStringExtra("id");
        UserDAO userDAO = new UserDAO(this);
        flashCardDAO = new FlashCardDAO(this);
        // Lấy thông tin người dùng từ cơ sở dữ liệu và hiển thị lên UI
        User user = userDAO.getUserById(flashCardDAO.getFlashCardById(id).getUser_id());

        Picasso.get().load(user.getAvatar()).into(binding.avatarIv);
        binding.userNameTv.setText(user.getUsername());
        binding.descriptionTv.setText(flashCardDAO.getFlashCardById(id).getDescription());
        cardDAO = new CardDAO(this);
        binding.termCountTv.setText(cardDAO.countCardByFlashCardId(getIntent().getStringExtra("id")) + " " + getString(R.string.term));
        flashCardDAO = new FlashCardDAO(this);
        binding.setNameTv.setText(flashCardDAO.getFlashCardById(getIntent().getStringExtra("id")).getName());

        userSharePreferences = new UserSharePreferences(this);
    }

    // Thiết lập sự kiện khi người dùng nhấn vào phần "Review" (Đánh giá lại bộ Flashcard)
    private void setupReviewClickListener() {
        binding.reviewCl.setOnClickListener(v -> {
            if (!isUserOwner()) {
                showLearnErrorDialog();
            } else {
                Intent intent = new Intent(this, LearnActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });
    }

    // Thiết lập sự kiện khi người dùng nhấn vào phần "Learn" (Học bộ Flashcard)
    private void setupLearnClickListener() {
        binding.learnCl.setOnClickListener(v -> {
            cardDAO = new CardDAO(this);

            // Kiểm tra nếu người dùng không phải là chủ sở hữu, sẽ hiển thị lỗi
            if (!isUserOwner()) {
                showLearnErrorDialog();
                return;
            }

            // Nếu số lượng thẻ học ít hơn 4, yêu cầu người dùng xem lại bộ flashcard
            if (cardDAO.countCardByFlashCardId(getIntent().getStringExtra("id")) < 4) {
                showReviewErrorDialog();
            } else {
                // Chuyển sang activity Quiz để học với các câu hỏi trắc nghiệm
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("id", getIntent().getStringExtra("id"));
                startActivity(intent);
            }
        });
    }

    // Phương thức hiển thị hộp thoại lỗi khi người dùng không thể bắt đầu đánh giá bộ flashcard
    private void showReviewErrorDialog() {
        PopupDialog.getInstance(this)
                .setStyle(Styles.FAILED)// Đặt kiểu của hộp thoại thành "FAILED" (lỗi)
                .setHeading(getString(R.string.error)) // Tiêu đề là "Lỗi"
                .setDescription(getString(R.string.learn_error)) // Mô tả lỗi (Thông báo không thể học bộ flashcard)
                .setDismissButtonText(getString(R.string.ok)) // Nút đóng với văn bản "OK"
                .setCancelable(true) // Cho phép đóng hộp thoại bằng cách chạm ra ngoài
                .showDialog(new OnDialogButtonClickListener() { // Hiển thị hộp thoại và lắng nghe sự kiện nút
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        super.onDismissClicked(dialog);
                        dialog.dismiss(); // Đóng hộp thoại khi người dùng nhấn "OK"
                    }
                });
    }

    // Phương thức hiển thị hộp thoại lỗi khi người dùng không thể thực hiện thao tác học
    private void showLearnErrorDialog() {
        PopupDialog.getInstance(this)
                .setStyle(Styles.STANDARD)// Đặt kiểu của hộp thoại thành "STANDARD" (mặc định)
                .setHeading(getString(R.string.error)) // Tiêu đề là "Lỗi"
                .setDescription(getString(R.string.review_error)) // Mô tả lỗi (Thông báo không thể xem lại bộ flashcard)
                .setPopupDialogIcon(R.drawable.baseline_error_24) // Đặt biểu tượng lỗi
                .setDismissButtonText(getString(R.string.ok)) // Nút "OK"
                .setNegativeButtonText(getString(R.string.cancel)) // Nút "Hủy"
                .setPositiveButtonText(getString(R.string.ok)) // Nút "OK" cho phép sao chép bộ flashcard
                .setCancelable(true) // Cho phép đóng hộp thoại khi chạm ngoài
                .showDialog(new OnDialogButtonClickListener() { // Hiển thị hộp thoại và lắng nghe sự kiện các nút
                    @Override
                    public void onNegativeClicked(Dialog dialog) {
                        super.onNegativeClicked(dialog); // Khi người dùng nhấn "Hủy", không làm gì cả
                    }

                    @Override
                    public void onPositiveClicked(Dialog dialog) {
                        super.onPositiveClicked(dialog);
                        copyFlashCard(); // Thực hiện sao chép bộ flashcard
                        // Hiển thị hộp thoại thành công sau khi sao chép
                        PopupDialog.getInstance(ViewSetActivity.this)
                                .setStyle(Styles.SUCCESS) // Đặt kiểu thành công
                                .setHeading(getString(R.string.success)) // Tiêu đề "Thành công"
                                .setDescription(getString(R.string.review_success)) // Mô tả thành công
                                .setCancelable(false) // Không cho phép đóng hộp thoại khi chạm ngoài
                                .setDismissButtonText(getString(R.string.view)) // Nút "Xem"
                                .showDialog(new OnDialogButtonClickListener() { // Hiển thị hộp thoại và lắng nghe sự kiện nút
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                        dialog.dismiss(); // Đóng hộp thoại khi nhấn "Xem"
                                    }
                                });
                    }
                });

    }

    // Phương thức thiết lập điều hướng trên toolbar (quay lại trang trước)
    private void setupToolbarNavigation() {
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // Khi nhấn nút quay lại trên toolbar, sẽ gọi phương thức onBackPressed để quay lại Activity trước đó
    }

    // Phương thức thiết lập RecyclerView, bao gồm việc thiết lập các adapter và layout manager
    private void setupRecyclerView(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            listPosition = savedInstanceState.getInt(LIST_POSITION); // Lưu vị trí cuộn của RecyclerView nếu có
        }
        // Thiết lập LinearLayoutManager cho RecyclerView để hiển thị các item theo chiều ngang
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.recyclerViewSet.setLayoutManager(linearLayoutManager);
        binding.recyclerViewSet.scrollToPosition(listPosition); // Cuộn đến vị trí đã lưu

        // Thiết lập RecyclerView cho các term, sử dụng LinearLayoutManager theo chiều dọc
        LinearLayoutManager linearLayoutManagerVertical = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        );

        binding.recyclerViewTerms.setLayoutManager(linearLayoutManagerVertical);
        binding.recyclerViewTerms.setNestedScrollingEnabled(false); // Tắt cuộn nested scrolling cho RecyclerView này
    }

    // Phương thức thiết lập dữ liệu cho các thẻ flashcard và cập nhật adapter
    @SuppressLint("NotifyDataSetChanged")
    private void setupCardData() {
        String id = getIntent().getStringExtra("id"); // Lấy ID bộ flashcard từ intent
        cardDAO = new CardDAO(this); // Khởi tạo DAO để truy vấn dữ liệu thẻ
        cards = cardDAO.getCardsByFlashCardId(id); // Lấy danh sách các thẻ flashcard theo ID bộ
        // Thiết lập adapter cho RecyclerView hiển thị các thẻ flashcard
        setUpProgress(cards); // Hiển thị tiến trình (nếu có)
        ViewSetAdapter viewSetAdapter = new ViewSetAdapter(this, cards);
        binding.recyclerViewSet.setAdapter(viewSetAdapter);
        viewSetAdapter.notifyDataSetChanged(); // Cập nhật giao diện

        // Thiết lập adapter cho RecyclerView hiển thị các term
        ViewTermsAdapter viewTermsAdapter = new ViewTermsAdapter(cards);
        binding.recyclerViewTerms.setAdapter(viewTermsAdapter);
        viewTermsAdapter.notifyDataSetChanged(); // Cập nhật giao diện

    }

    // Phương thức thiết lập listener cho sự kiện điều hướng (quay lại)
    private void setupNavigationListener() {
        // Khi người dùng nhấn nút quay lại trên toolbar, gọi phương thức onBackPressedDispatcher() để quay lại Activity trước đó
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher());
    }

    // Phương thức thiết lập các sự kiện cuộn qua các thẻ flashcard
    private void setupScrollListeners() {
        // Khi nhấn nút "Trước", cuộn về vị trí trước đó trong RecyclerView
        binding.previousIv.setOnClickListener(v -> {
            // Lấy vị trí thẻ đầu tiên hiển thị đầy đủ
            int currentPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
            // Nếu vị trí hiện tại lớn hơn 0, cuộn về phía trước một vị trí
            if (currentPosition > 0) {
                binding.recyclerViewSet.scrollToPosition(currentPosition - 1);
            }
        });

        // Khi nhấn nút "Sau", cuộn về vị trí tiếp theo trong RecyclerView
        binding.nextIv.setOnClickListener(v -> {
            // Lấy vị trí thẻ cuối cùng hiển thị đầy đủ
            int currentPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
            // Nếu vị trí hiện tại nhỏ hơn kích thước danh sách thẻ, cuộn về phía sau một vị trí
            if (currentPosition < cards.size() - 1) {
                binding.recyclerViewSet.scrollToPosition(currentPosition + 1);
            }
        });
    }

    // Phương thức lưu lại trạng thái của RecyclerView (vị trí cuộn) khi Activity bị hủy và khôi phục lại sau
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu lại vị trí cuộn đầu tiên trong RecyclerView để khôi phục lại khi Activity quay lại
        outState.putInt(LIST_POSITION, linearLayoutManager.findFirstVisibleItemPosition());
    }

    // Phương thức tạo menu cho Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Tạo menu từ file XML
        getMenuInflater().inflate(R.menu.menu_view_set, menu);
        return true;
    }


    // Phương thức xử lý khi người dùng chọn một mục trong menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Kiểm tra ID của mục được chọn trong menu
        if (item.getItemId() == R.id.menu) {
            // Tạo và hiển thị một BottomSheetMenuDialogFragment (hộp thoại chọn tùy chọn từ dưới lên)
            new BottomSheetMenuDialogFragment.Builder(this)
                    .setSheet(R.menu.menu_bottom_view_set)// Đặt sheet cho menu bên dưới
                    .setTitle(R.string.book) // Tiêu đề của BottomSheet
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @Nullable Object o) {
                            // Sự kiện khi BottomSheet được hiển thị
                        }

                        @Override
                        public void onSheetItemSelected(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @NotNull MenuItem menuItem, @Nullable Object o) {
                            String id = getIntent().getStringExtra("id"); // Lấy ID bộ flashcard từ Intent

                            // Xử lý các tùy chọn trong menu
                            int itemId = menuItem.getItemId();
                            if (itemId == R.id.edit) {
                                // Nếu người dùng là chủ sở hữu, cho phép chỉnh sửa
                                if (isUserOwner()) {
                                    handleEditOption(id);
                                } else {
                                    Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                                }
                            } else if (itemId == R.id.delete_set) {
                                // Nếu người dùng là chủ sở hữu, cho phép xóa bộ flashcard
                                if (isUserOwner()) {
                                    handleDeleteSetOption(id);
                                } else {
                                    Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                                }
                            } else if (itemId == R.id.add_to_folder) {
                                // Nếu người dùng là chủ sở hữu, cho phép thêm vào thư mục
                                if (isUserOwner()) {
                                    handleAddToFolderOption(id);
                                } else {
                                    Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
                                }
                            } else if (itemId == R.id.add_to_class) {
                                // Nếu người dùng là chủ sở hữu, cho phép thêm vào lớp
                                if (isUserOwner()) {
                                    handleAddToClassOption(id);
                                } else {
                                    Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
                                }
                            } else if (itemId == R.id.reset) {
                                // Nếu người dùng là chủ sở hữu, cho phép reset bộ flashcard
                                if (isUserOwner()) {
                                    handleResetOption(id);
                                } else {
                                    Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onSheetDismissed(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @Nullable Object o, int i) {
                            // Sự kiện khi BottomSheet bị đóng
                        }
                    })
                    .setCloseTitle(getString(R.string.close))// Đặt tiêu đề nút đóng là "Đóng"
                    .setAutoExpand(true) // Tự động mở rộng BottomSheet
                    .setCancelable(true) // Cho phép đóng khi chạm ngoài BottomSheet
                    .show(getSupportFragmentManager()); // Hiển thị BottomSheet
            return true;
        }
        return super.onOptionsItemSelected(item); // Trả về kết quả mặc định nếu không phải menu "menu"
    }

    // Phương thức xử lý tùy chọn chỉnh sửa bộ flashcard
    private void handleEditOption(String id) {
        // Kiểm tra xem người dùng có phải là chủ sở hữu bộ flashcard không
        if (isUserOwner()) {
            // Nếu người dùng là chủ sở hữu, chuyển đến Activity chỉnh sửa bộ flashcard
            Intent intent = new Intent(ViewSetActivity.this, EditFlashCardActivity.class);
            intent.putExtra("flashcard_id", id);  // Truyền ID bộ flashcard vào Intent
            startActivity(intent); // Khởi chạy Activity chỉnh sửa
        } else {
            // Nếu người dùng không phải là chủ sở hữu, hiển thị thông báo lỗi
            Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức xử lý tùy chọn xóa bộ flashcard
    private void handleDeleteSetOption(String id) {
        // Kiểm tra xem người dùng có phải là chủ sở hữu bộ flashcard không
        if (isUserOwner()) {
            // Nếu người dùng là chủ sở hữu, hiển thị hộp thoại xác nhận xóa
            showDeleteSetDialog(id);
        } else {
            // Nếu người dùng không phải là chủ sở hữu, hiển thị thông báo lỗi
            Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức xử lý tùy chọn thêm bộ flashcard vào thư mục
    private void handleAddToFolderOption(String id) {
        // Mở Activity thêm bộ flashcard vào thư mục
        Intent intent = new Intent(ViewSetActivity.this, AddToFolderActivity.class);
        intent.putExtra("flashcard_id", id);  // Truyền ID bộ flashcard vào Intent
        startActivity(intent); // Khởi chạy Activity thêm vào thư mục
    }

    // Phương thức xử lý tùy chọn thêm bộ flashcard vào lớp học
    private void handleAddToClassOption(String id) {
        // Mở Activity thêm bộ flashcard vào lớp học
        Intent intent = new Intent(ViewSetActivity.this, AddToClassActivity.class);
        intent.putExtra("flashcard_id", id);  // Truyền ID bộ flashcard vào Intent
        startActivity(intent); // Khởi chạy Activity thêm vào lớp học
    }

    // Phương thức xử lý tùy chọn reset trạng thái bộ flashcard
    private void handleResetOption(String id) {
        // Kiểm tra xem người dùng có phải là chủ sở hữu bộ flashcard không
        if (isUserOwner()) {
            cardDAO = new CardDAO(ViewSetActivity.this);
            // Reset trạng thái học và tình trạng của các thẻ flashcard trong bộ
            if (cardDAO.resetIsLearnedAndStatusCardByFlashCardId(id) > 0L) {
                // Nếu thành công, hiển thị thông báo thành công và cập nhật lại dữ liệu
                Toast.makeText(ViewSetActivity.this, getString(R.string.reset_success), Toast.LENGTH_SHORT).show();
                setupCardData();
            } else {
                // Nếu thất bại, hiển thị thông báo lỗi
                Toast.makeText(ViewSetActivity.this, getString(R.string.reset_error), Toast.LENGTH_SHORT).show();
            }

        } else {
            // Nếu người dùng không phải là chủ sở hữu, hiển thị thông báo lỗi
            Toast.makeText(ViewSetActivity.this, getString(R.string.edit_error), Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức hiển thị hộp thoại xác nhận xóa bộ flashcard
    private void showDeleteSetDialog(String id) {
        PopupDialog.getInstance(ViewSetActivity.this)
                .setStyle(Styles.STANDARD) // Đặt kiểu hộp thoại
                .setHeading(getString(R.string.delete_set)) // Tiêu đề của hộp thoại
                .setDescription(getString(R.string.delete_set_description)) // Mô tả cho hành động xóa
                .setPopupDialogIcon(R.drawable.ic_delete) // Biểu tượng của hộp thoại
                .setCancelable(true) // Cho phép đóng hộp thoại khi chạm ngoài
                .showDialog(new OnDialogButtonClickListener() {
                    // Khi người dùng nhấn nút "OK"
                    @Override
                    public void onPositiveClicked(Dialog dialog) {
                        super.onPositiveClicked(dialog);
                        // Xóa bộ flashcard
                        deleteSet(id);
                    }

                    // Khi người dùng nhấn nút "Cancel"
                    @Override
                    public void onNegativeClicked(Dialog dialog) {
                        super.onNegativeClicked(dialog);
                        dialog.dismiss(); // Đóng hộp thoại mà không thực hiện hành động nào
                    }
                });
    }

    // Phương thức xóa bộ flashcard
    private void deleteSet(String id) {
        FlashCardDAO flashCardDAO = new FlashCardDAO(ViewSetActivity.this);
        // Thực hiện xóa bộ flashcard và các thẻ liên quan
        if (flashCardDAO.deleteFlashcardAndCards(id)) {
            // Nếu xóa thành công, hiển thị hộp thoại thành công và quay lại màn hình trước
            PopupDialog.getInstance(ViewSetActivity.this)
                    .setStyle(Styles.SUCCESS)// Đặt kiểu hộp thoại thành công
                    .setHeading(getString(R.string.success)) // Tiêu đề thành công
                    .setDescription(getString(R.string.delete_set_success)) // Mô tả thành công
                    .setCancelable(false) // Không cho phép đóng hộp thoại khi chạm ngoài
                    .setDismissButtonText(getString(R.string.ok)) // Nút "OK" để đóng hộp thoại
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog); // Đóng hộp thoại khi nhấn "OK"
                            finish(); // Quay lại màn hình trước đó
                        }
                    });
        } else {
            // Nếu xóa thất bại, hiển thị hộp thoại lỗi
            PopupDialog.getInstance(ViewSetActivity.this)
                    .setStyle(Styles.FAILED) // Đặt kiểu hộp thoại lỗi
                    .setHeading(getString(R.string.error)) // Tiêu đề lỗi
                    .setDescription(getString(R.string.delete_set_error)) // Mô tả lỗi
                    .setCancelable(true) // Cho phép đóng hộp thoại khi chạm ngoài
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClicked(Dialog dialog) {
                            super.onPositiveClicked(dialog);
                        }
                    });
        }
    }

    // Hàm copyFlashCard() sao chép flashcard và các thẻ (cards) của nó
    private void copyFlashCard() {
        // Lấy ID của flashcard từ Intent
        String id = getIntent().getStringExtra("id");
        // Khởi tạo đối tượng UserSharePreferences để lấy thông tin người dùng
        userSharePreferences = new UserSharePreferences(this);
        // Khởi tạo đối tượng FlashCardDAO để làm việc với cơ sở dữ liệu
        flashCardDAO = new FlashCardDAO(this);
        // Lấy flashcard hiện tại từ cơ sở dữ liệu theo ID
        FlashCard flashCard = flashCardDAO.getFlashCardById(id);

        // Tạo ID mới cho flashcard sao chép và gán các thuộc tính
        idCard = genUUID();  // Tạo UUID mới
        flashCard.setId(idCard);  // Gán ID mới cho flashcard
        flashCard.setUser_id(getUser_id());  // Gán ID người dùng hiện tại
        flashCardDAO.insertFlashCard(flashCard);  // Chèn flashcard mới vào cơ sở dữ liệu

        // Khởi tạo đối tượng CardDAO để làm việc với thẻ (cards)
        CardDAO cardDAO = new CardDAO(this);
        // Lấy danh sách các thẻ liên kết với flashcard hiện tại
        ArrayList<Card> cards = cardDAO.getCardsByFlashCardId(id);

        // Duyệt qua từng thẻ và sao chép chúng
        for (Card card : cards) {
            card.setId(genUUID());// Tạo ID mới cho mỗi thẻ
            card.setFlashcard_id(flashCard.getId());  // Gán ID của flashcard mới vào thẻ
            card.setIsLearned(0);  // Đặt trạng thái là chưa học
            card.setStatus(0);  // Đặt trạng thái thẻ là chưa hoàn thành
            card.setCreated_at(getCurrentDate());  // Gán ngày tạo
            card.setUpdated_at(getCurrentDate());  // Gán ngày cập nhật
            // Chèn thẻ mới vào cơ sở dữ liệu
            if (cardDAO.insertCard(card) > 0L) {
                // Nếu việc chèn thẻ thành công, không làm gì cả
            } else {
                // Nếu chèn thẻ thất bại, hiển thị thông báo lỗi
                Toast.makeText(this, getString(R.string.review_error), Toast.LENGTH_SHORT).show();
            }
        }
        // Hiển thị thông báo sao chép thành công
        Toast.makeText(this, getString(R.string.review_success), Toast.LENGTH_SHORT).show();
    }

    // Hàm lấy ngày hiện tại
    private String getCurrentDate() {
        // Kiểm tra phiên bản Android, nếu >= Oreo sử dụng LocalDate, ngược lại sử dụng SimpleDateFormat
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return getCurrentDateNewApi();  // Dành cho Android 8.0 (API 26) trở lên
        } else {
            return getCurrentDateOldApi();  // Dành cho các phiên bản cũ hơn
        }
    }

    // Hàm tạo UUID mới
    private String genUUID() {
        return java.util.UUID.randomUUID().toString();  // Tạo UUID mới
    }

    // Hàm lấy ID người dùng từ shared preferences
    private String getUser_id() {
        userSharePreferences = new UserSharePreferences(this);  // Khởi tạo UserSharePreferences
        return userSharePreferences.getId();  // Lấy ID người dùng từ shared preferences
    }

    // Hàm lấy ngày hiện tại cho API >= Oreo
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDateNewApi() {
        LocalDate currentDate = LocalDate.now(); // Lấy ngày hiện tại
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Định dạng ngày tháng
        return currentDate.format(formatter); // Trả về ngày theo định dạng dd/MM/yyyy
    }

    // Hàm lấy ngày hiện tại cho API < Oreo
    private String getCurrentDateOldApi() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");// Định dạng ngày tháng
        return sdf.format(new Date());  // Trả về ngày theo định dạng dd/MM/yyyy
    }

    // Hàm kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của flashcard không
    private boolean isUserOwner() {
        // Kiểm tra nếu ID người dùng hiện tại bằng ID người tạo flashcard
        Log.d("isUserOwner", "isUserOwner: " + userSharePreferences.getId().equals(flashCardDAO.getFlashCardById(getIntent().getStringExtra("id")).getUser_id()));
        return userSharePreferences.getId().equals(flashCardDAO.getFlashCardById(getIntent().getStringExtra("id")).getUser_id()); // Trả về true nếu người dùng là chủ sở hữu

    }

    // Hàm thiết lập tiến trình học của thẻ (cards)
    @SuppressLint("SetTextI18n")
    private void setUpProgress(ArrayList<Card> cards) {
        int notLearned = 0;
        int learning = 0;
        int learned = 0;
        // Duyệt qua từng thẻ để đếm số thẻ chưa học, đang học và đã học
        for (Card card : cards) {
            if (card.getStatus() == 0) {
                notLearned++;  // Thẻ chưa học
            } else if (card.getStatus() == 1) {
                learned++;  // Thẻ đã học
            } else {
                learning++;  // Thẻ đang học
            }
        }

        // Nếu người dùng là chủ sở hữu, hiển thị thông tin tiến trình học
        if (isUserOwner()) {
            // Nếu người dùng không phải là chủ sở hữu, hiển thị mặc định
            binding.notLearnTv.setText("Not learned: " + notLearned);  // Hiển thị số thẻ chưa học
            binding.isLearningTv.setText("Learning: " + learning);  // Hiển thị số thẻ đang học
            binding.learnedTv.setText("Learned: " + learned);  // Hiển thị số thẻ đã học
        } else {
            binding.notLearnTv.setText("Not learned: " + cards.size());
            binding.isLearningTv.setText("Learning: " + 0);
            binding.learnedTv.setText("Learned: " + 0);
            // Ẩn các biểu tượng bên cạnh các số liệu
            binding.notLearnTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            binding.isLearningTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            binding.learnedTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

    }

    // Hàm gọi lại khi Activity được tiếp tục (resumed)
    @Override
    protected void onResume() {
        super.onResume();
        setupCardData();  // Tải lại dữ liệu flashcard
        setupUserDetails();  // Tải lại thông tin người dùng
    }
}
