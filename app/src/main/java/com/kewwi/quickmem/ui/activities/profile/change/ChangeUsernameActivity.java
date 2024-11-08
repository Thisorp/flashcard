package com.kewwi.quickmem.ui.activities.profile.change;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.databinding.ActivityChangeUsernameBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;

// Lớp ChangeUsernameActivity kế thừa từ AppCompatActivity để tạo một màn hình thay đổi tên người dùng
public class ChangeUsernameActivity extends AppCompatActivity {
    // Khai báo các biến cho việc binding view, DAO người dùng và tên người dùng
    private ActivityChangeUsernameBinding binding;
    private UserDAO userDAO;
    private String oldUsername, newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng ViewBinding để liên kết giao diện người dùng với mã Java
        binding = ActivityChangeUsernameBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập toolbar và sự kiện khi nhấn nút quay lại
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Thiết lập sự kiện khi trường nhập tên người dùng mất focus
        binding.usernameEt.setOnFocusChangeListener((view1, b) -> {
            if (!b) {
                validUsername();// Kiểm tra tính hợp lệ của tên người dùng
            }
        });
    }

    // Hàm cập nhật tên người dùng
    private void updateUsername() {
        // Lấy dữ liệu từ UserDAO và UserSharePreferences
        userDAO = new UserDAO(this);
        newUsername = binding.usernameEt.getText().toString().trim();
        UserSharePreferences userSharePreferences = new UserSharePreferences(ChangeUsernameActivity.this);
        oldUsername = userSharePreferences.getUserName();// Lấy tên người dùng cũ
        String id = userSharePreferences.getId();// Lấy ID người dùng
        validUsername(); // Kiểm tra tính hợp lệ của tên người dùng mới

        // Nếu không có lỗi trong việc nhập tên người dùng
        if (binding.textIL.getHelperText() == null) {
            // Thực hiện cập nhật tên người dùng trong cơ sở dữ liệu
            if (userDAO.updateUsernameUser(id, newUsername) > 0) {
                // Nếu cập nhật thành công, lưu tên người dùng mới vào SharedPreferences
                userSharePreferences.setUserName(newUsername);
                Toast.makeText(this, "Change username SUCCESS", Toast.LENGTH_SHORT).show();
                // Quay lại màn hình trước đó
                getOnBackPressedDispatcher().onBackPressed();
            } else {
                // Nếu cập nhật thất bại, thông báo lỗi
                Toast.makeText(this, "Change username UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Hàm kiểm tra tính hợp lệ của tên người dùng
    private void validUsername() {
        // Kiểm tra nếu tên người dùng mới trống
        if (newUsername.isEmpty()) {
            binding.textIL.setHelperText("Please enter your new username");
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.usernameEt.requestFocus();
        }
        // Kiểm tra độ dài của tên người dùng (phải từ 5 đến 15 ký tự)
        else if (newUsername.length() > 15 || newUsername.length() < 5) {
            binding.textIL.setHelperText("Username must be > 5 characters and less than 15 characters");
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.usernameEt.requestFocus();
        }
        // Kiểm tra xem tên người dùng có chứa khoảng trắng hay không
        else if (newUsername.contains(" ")) {
            binding.textIL.setHelperText("Username cannot contain spaces");
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.usernameEt.requestFocus();
        }
        // Kiểm tra xem tên người dùng mới có trùng với tên cũ không
        else if (newUsername.equals(oldUsername)) {
            binding.textIL.setHelperText("New username must be different with old user name");
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.usernameEt.requestFocus();
        }
        // Kiểm tra xem tên người dùng đã tồn tại trong cơ sở dữ liệu chưa
        else if (userDAO.checkUsername(newUsername)) {
            binding.textIL.setHelperText("Username already exists");
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.usernameEt.requestFocus();
        } else {
            // Nếu hợp lệ, xóa thông báo lỗi
            binding.textIL.setHelperText(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    // Xử lý sự kiện khi nhấn vào mục lưu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            // Gọi hàm cập nhật tên người dùng
            updateUsername();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}