package com.kewwi.quickmem.ui.activities.create;

import android.annotation.SuppressLint;
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
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.databinding.ActivityCreateClassBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
// File này định nghĩa một activity cho việc tạo lớp học
// Nó quản lý giao diện người dùng và xử lý sự kiện khi người dùng muốn tạo lớp học.
public class CreateClassActivity extends AppCompatActivity {
    ActivityCreateClassBinding binding;// Biến binding để liên kết giao diện với code Java.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng ActivityCreateClassBinding để lấy layout và đặt nội dung hiển thị.
        binding = ActivityCreateClassBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập thanh công cụ (toolbar) và xử lý nút quay lại.
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Tạo menu với nút "done" để hoàn tất tạo lớp.
        getMenuInflater().inflate(R.menu.menu_tick, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý khi người dùng chọn menu item (ví dụ: nút "done").
        if (item.getItemId() == R.id.done) {
            // Kiểm tra điều kiện nhập liệu.
            if (validate()) {
                // Lấy các giá trị từ giao diện người dùng.
                String name = binding.classEt.getText().toString();
                String description = binding.descriptionEt.getText().toString();
                boolean status = binding.privateSt.isChecked();// Kiểm tra trạng thái lớp có phải là private không.

                // Tạo các thông tin cho lớp học mới.
                String id = genUUID();// Tạo ID duy nhất cho lớp học.
                String user_id = getUser_id();// Lấy ID của người dùng hiện tại.
                String created_at = getCurrentDate(); // Lấy ngày tạo lớp.
                String updated_at = getCurrentDate();// Ngày cập nhật cũng là ngày hiện tại.

                // Khởi tạo đối tượng DAO để làm việc với database.
                GroupDAO groupDAO = new GroupDAO(this);
                // Tạo đối tượng Group để lưu thông tin lớp học.
                Group group = new Group();
                group.setName(name);
                group.setDescription(description);
                group.setStatus(status ? 1 : 0);// Nếu private thì 1, nếu không thì 0.
                group.setId(id);
                group.setUser_id(user_id);
                group.setCreated_at(created_at);
                group.setUpdated_at(updated_at);

                // Thực hiện chèn dữ liệu vào database.
                if (groupDAO.insertGroup(group) > 0) {
                    getOnBackPressedDispatcher().onBackPressed();
                    Toast.makeText(this, "Create class success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Create class failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter class name", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // Hàm kiểm tra xem người dùng đã nhập tên lớp hay chưa.
    private boolean validate() {
        if (binding.classEt.getText().toString().isEmpty()) {
            binding.classTil.setHelperText("Please enter class name");
            return false;
        } else {
            binding.classTil.setHelperText("");
            return true;
        }
    }

    // Hàm lấy ngày hiện tại.
    private String getCurrentDate() {
        // Nếu phiên bản Android từ O trở lên, sử dụng API mới.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return getCurrentDateNewApi();
        } else {
            // Nếu phiên bản cũ hơn, sử dụng API cũ.
            return getCurrentDateOldApi();
        }
    }

    // Hàm tạo UUID (ID duy nhất) cho lớp học.
    private String genUUID() {
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