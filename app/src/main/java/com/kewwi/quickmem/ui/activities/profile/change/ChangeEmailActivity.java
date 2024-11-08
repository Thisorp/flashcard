package com.kewwi.quickmem.ui.activities.profile.change;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.databinding.ActivityChangeEmailBinding;
import com.kewwi.quickmem.databinding.ActivityChangeUsernameBinding;
import com.kewwi.quickmem.databinding.ActivitySignupBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
//lớp điều khiển giao diện cho chức năng thay đổi email người dùng trong ứng dụng Android.

// Lớp ChangeEmailActivity kế thừa từ AppCompatActivity để tạo một màn hình thay đổi email người dùng
public class ChangeEmailActivity extends AppCompatActivity {
    // Khai báo biến toàn cục cho việc binding giao diện, lưu trữ preferences,
    // và DAO để truy xuất dữ liệu người dùng
    private ActivityChangeEmailBinding binding;
    private UserSharePreferences userSharePreferences;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng View Binding để gán layout cho Activity
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập Toolbar và thêm chức năng quay lại khi người dùng bấm nút điều hướng
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Lắng nghe sự thay đổi trong ô nhập email
        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Xử lý logic khi người dùng đang nhập email
                handleEmailTextChanged(charSequence.toString(), binding);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Xử lý logic sau khi người dùng thay đổi nội dung email
                handleEmailTextChanged(editable.toString(), binding);
            }
        });
    }

    // Phương thức này cập nhật email của người dùng
    private void updateEmail() {
        userDAO = new UserDAO(this);
        userSharePreferences = new UserSharePreferences(this);

        // Lấy email người dùng nhập vào
        String id = userSharePreferences.getId();
        final String email = binding.emailEt.getText().toString();

        // Kiểm tra email trước khi thực hiện cập nhật
        if (!handleEmailTextChanged(email, binding)) return;
        // Nếu email trống
        if (email.isEmpty()) {
            binding.textIL.setHelperText(getString(R.string.email_is_empty));
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.emailEt.requestFocus();
        }
        // Nếu cập nhật email thành công
        else if (userDAO.updateEmailUser(id, email) > 0) {
            userSharePreferences.setEmail(email);
            Toast.makeText(this, "Change email SUCCESS", Toast.LENGTH_SHORT).show();
            getOnBackPressedDispatcher().onBackPressed();// Quay lại màn hình trước đó
        } else {
            Toast.makeText(this, "Change username UNSUCCESS", Toast.LENGTH_SHORT).show();
        }
    }

    // Phương thức kiểm tra email và cung cấp phản hồi cho người dùng
    private boolean handleEmailTextChanged(String text, ActivityChangeEmailBinding binding) {

        // Nếu email trống
        if (text.isEmpty()) {
            binding.textIL.setHelperText(getString(R.string.email_is_empty));
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.emailEt.requestFocus();
            return false;
        }
        // Nếu email không hợp lệ
        else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            binding.textIL.setHelperText(getString(R.string.email_is_invalid));
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.emailEt.requestFocus();
            return false;
        }
        // Nếu email đã tồn tại
        else if (isEmailExist(text)) {
            binding.textIL.setHelperText(getString(R.string.email_is_exist));
            binding.textIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.emailEt.requestFocus();
        }
        // Nếu email hợp lệ
        else {
            binding.textIL.setHelperText("");
            return true;
        }
        return false;
    }

    // Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu chưa
    private boolean isEmailExist(String email) {
        userDAO = new UserDAO(this);
        return userDAO.checkEmail(email);
    }

    // Khởi tạo menu chứa nút "Lưu"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    // Xử lý khi người dùng bấm vào các mục menu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            // Nếu người dùng chọn lưu thì cập nhật email
            updateEmail();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }
}