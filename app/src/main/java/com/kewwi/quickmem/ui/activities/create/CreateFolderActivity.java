package com.kewwi.quickmem.ui.activities.create;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.FolderDAO;
import com.kewwi.quickmem.data.model.Folder;
import com.kewwi.quickmem.databinding.ActivityCreateFolderBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.folder.ViewFolderActivity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
//định nghĩa một activity (màn hình) để tạo một thư mục (folder) mới
//quản lý giao diện người dùng và xử lý sự kiện khi người dùng muốn tạo một thư mục.
public class CreateFolderActivity extends AppCompatActivity {
    private ActivityCreateFolderBinding binding;// Biến binding dùng để liên kết giao diện với code Java.

    // Sử dụng ActivityCreateFolderBinding để lấy layout và đặt nội dung hiển thị.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateFolderBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập thanh công cụ (toolbar) và xử lý nút quay lại.
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    }

    // Tạo menu với nút "done" để hoàn tất tạo thư mục.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý khi người dùng chọn một mục trong menu (nút "done").
        final int itemId = item.getItemId();
        if (itemId == R.id.done) {
            // Lấy tên thư mục và mô tả từ giao diện người dùng.
            final String folderName = binding.folderEt.getText().toString().trim();
            final String description = binding.descriptionEt.getText().toString().trim();
            if (folderName.isEmpty()) {
                // Kiểm tra xem tên thư mục có bị trống hay không.
                binding.folderTil.setError("");// Xóa lỗi nếu có
                binding.folderTil.setHelperText("Folder name cannot be empty");// Thông báo lỗi.
                binding.folderEt.requestFocus();// Đưa con trỏ chuột về trường nhập tên thư mục.
                return false;
            } else {
                // Tạo thông tin cho thư mục mới.
                final String folderId = genUUID();// Tạo ID duy nhất cho thư mục.
                final String userId = getUser_id();// Lấy ID người dùng hiện tại.
                final String createdAt = getCurrentDate();// Lấy ngày tạo thư mục.
                final String updatedAt = getCurrentDate();// Ngày cập nhật cũng là ngày hiện tại

                // Tạo đối tượng Folder với các giá trị đã nhập.
                Folder folder = new Folder(folderId, folderName, description, userId, createdAt, updatedAt);
                FolderDAO folderDAO = new FolderDAO(this); // Khởi tạo DAO để làm việc với database.
                // Thực hiện chèn thư mục vào cơ sở dữ liệu.
                if (folderDAO.insertFolder(folder) > 0) {
                    // Nếu tạo thành công, hiện thông báo và chuyển sang màn hình ViewFolderActivity.
                    Toast.makeText(this, "Folder created", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, ViewFolderActivity.class).putExtra("id", folderId));
                    finish();// Kết thúc activity hiện tại.
                } else {
                    Toast.makeText(this, "Folder not created", Toast.LENGTH_SHORT).show();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Hàm lấy ngày hiện tại.
    private String getCurrentDate() {
        // Sử dụng API mới nếu phiên bản Android từ O trở lên.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return getCurrentDateNewApi();
        } else {
            // Sử dụng API cũ nếu phiên bản Android cũ hơn O.
            return getCurrentDateOldApi();
        }
    }


    private String genUUID() {
        // Hàm tạo UUID (ID duy nhất) cho thư mục.
        return java.util.UUID.randomUUID().toString();
    }

    // Hàm lấy ID của người dùng từ UserSharePreferences.
    private String getUser_id() {
        UserSharePreferences userSharePreferences = new UserSharePreferences(this);
        return userSharePreferences.getId();
    }

    // Hàm lấy ngày hiện tại cho API mới (từ Android O trở lên).
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getCurrentDateNewApi() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return currentDate.format(formatter);
    }

    // Hàm lấy ngày hiện tại cho API cũ (dành cho các phiên bản Android trước O).
    private String getCurrentDateOldApi() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

}