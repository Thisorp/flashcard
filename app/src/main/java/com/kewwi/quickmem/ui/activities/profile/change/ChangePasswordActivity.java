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
import com.kewwi.quickmem.databinding.ActivityChangePasswordBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.utils.PasswordHasher;

import java.util.Objects;
//lớp điều khiển cho giao diện thay đổi mật khẩu của người dùng
// Lớp ChangePasswordActivity kế thừa từ AppCompatActivity để tạo một màn hình thay đổi mật khẩu người dùng
public class ChangePasswordActivity extends AppCompatActivity {
    // Khai báo các biến toàn cục
    private ActivityChangePasswordBinding binding;
    private UserDAO userDAO;
    String currentPass, newPass, reNewPass, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng View Binding để gán layout cho Activity
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Thiết lập Toolbar và thêm chức năng quay lại khi người dùng bấm nút điều hướng
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Lấy ID người dùng từ UserSharePreferences
        UserSharePreferences userSharePreferences = new UserSharePreferences(this);
        id = userSharePreferences.getId();

        // Lắng nghe sự thay đổi khi người dùng rời khỏi các ô nhập liệu
        binding.currentPassEt.setOnFocusChangeListener((view1, b) -> {
            if (!b) {
                validCurrentPass();// Kiểm tra mật khẩu hiện tại
            }
        });
        binding.newPassEt.setOnFocusChangeListener((view12, b) -> {
            if (!b) {
                validNewPass();// Kiểm tra mật khẩu mới
            }
        });
        binding.confirmPassEt.setOnFocusChangeListener((view13, b) -> {
            if (!b) {
                validConfirmPass(); // Kiểm tra xác nhận mật khẩu
            }
        });
    }

    // Phương thức thay đổi mật khẩu
    private void changePassword() {
        validConfirmPass();// Kiểm tra xác nhận mật khẩu
        validNewPass();// Kiểm tra mật khẩu mới
        validConfirmPass();// Kiểm tra xác nhận mật khẩu

        // Nếu không có lỗi trợ giúp nào trong các trường mật khẩu
        if (binding.currentPassIL.getHelperText() == null &&
                binding.newPassIL.getHelperText() == null &&
                binding.currentPassIL.getHelperText() == null) {

            // Mã hóa mật khẩu mới và cập nhật nó trong cơ sở dữ liệu
            userDAO = new UserDAO(this);
            String hashedPassword = PasswordHasher.hashPassword(newPass);
            if (userDAO.updatePasswordUser(id, hashedPassword) > 0) {
                Toast.makeText(this, "Change username SUCCESS", Toast.LENGTH_SHORT).show();
                getOnBackPressedDispatcher().onBackPressed();// Quay lại màn hình trước đó
            } else {
                Toast.makeText(this, "Change username UNSUCCESSFUL", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Phương thức kiểm tra xác nhận mật khẩu
    private void validConfirmPass() {
        // Lấy dữ liệu từ trường xác nhận mật khẩu, loại bỏ khoảng trắng thừa
        reNewPass = binding.confirmPassEt.getText().toString().trim();

        // Kiểm tra nếu trường nhập xác nhận mật khẩu trống
        if (reNewPass.isEmpty()) {
            // Nếu trống, hiển thị thông báo yêu cầu nhập mật khẩu mới
            binding.confirmPassIL.setHelperText("Please enter your new password");

            // Đặt màu cho thông báo lỗi thành màu đỏ
            binding.confirmPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));

            // Đặt con trỏ vào trường nhập xác nhận mật khẩu để người dùng nhập lại
            binding.confirmPassEt.requestFocus();
        }
        // Kiểm tra nếu xác nhận mật khẩu không khớp với mật khẩu mới
        else if (!reNewPass.equals(newPass)) {
            // Nếu không khớp, hiển thị thông báo lỗi về việc xác nhận mật khẩu phải giống với mật khẩu mới
            binding.confirmPassIL.setHelperText("Confirm password must be similar new password");

            // Đặt màu cho thông báo lỗi thành màu đỏ
            binding.confirmPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));

            // Đặt con trỏ vào trường nhập xác nhận mật khẩu để người dùng nhập lại
            binding.confirmPassEt.requestFocus();
        }
        // Nếu tất cả hợp lệ
        else {
            // Xóa thông báo lỗi
            binding.confirmPassIL.setHelperText(null);
        }
    }

    // Phương thức kiểm tra mật khẩu mới
    private void validNewPass() {
        newPass = binding.newPassEt.getText().toString().trim();
        if (newPass.isEmpty()) {
            binding.newPassIL.setHelperText("Please enter your new password");
            binding.newPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.newPassEt.requestFocus();
        } else if (newPass.length() < 8) {
            binding.newPassIL.setHelperText(getString(R.string.password_is_invalid));
            binding.newPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.newPassEt.requestFocus();
        } else if (newPass.contains(" ")) {
            binding.newPassIL.setHelperText("Password cannot contain spaces");
            binding.newPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.newPassEt.requestFocus();
        } else {
            binding.newPassIL.setHelperText(null);
        }
    }

    // Phương thức kiểm tra mật khẩu hiện tại
    private void validCurrentPass() {
        // Khởi tạo đối tượng UserDAO để truy xuất dữ liệu người dùng từ cơ sở dữ liệu
        userDAO = new UserDAO(this);

        // Lấy mật khẩu hiện tại mà người dùng đã nhập và loại bỏ khoảng trắng thừa
        currentPass = binding.currentPassEt.getText().toString().trim();

        // Kiểm tra nếu trường nhập mật khẩu hiện tại đang trống
        if (currentPass.isEmpty()) {
            // Nếu trống, hiển thị thông báo yêu cầu nhập mật khẩu
            binding.currentPassIL.setHelperText("Please enter your password");

            // Đặt màu cho thông báo lỗi thành màu đỏ
            binding.currentPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));

            // Đặt con trỏ vào trường nhập mật khẩu hiện tại để người dùng nhập lại
            binding.currentPassEt.requestFocus();
        }
        // Kiểm tra nếu mật khẩu hiện tại không khớp với mật khẩu trong cơ sở dữ liệu
        else if (!Objects.equals(PasswordHasher.hashPassword(currentPass), userDAO.getPasswordUser(id))) {
            // Hiển thị một thông báo ngắn cho biết mật khẩu không chính xác
            Toast.makeText(this, "Pass: " + userDAO.getPasswordUser(id), Toast.LENGTH_SHORT).show();

            // Hiển thị thông báo lỗi về việc mật khẩu không đúng
            binding.currentPassIL.setHelperText("Password is incorrect");

            // Đặt màu cho thông báo lỗi thành màu đỏ
            binding.currentPassIL.setHelperTextColor(ColorStateList.valueOf(Color.RED));

            // Đặt con trỏ vào trường nhập mật khẩu hiện tại để người dùng nhập lại
            binding.currentPassEt.requestFocus();
        }
        // Nếu mật khẩu hiện tại hợp lệ
        else {
            // Xóa thông báo lỗi (nếu có)
            binding.currentPassIL.setHelperText(null);
        }
    }

    // Tạo menu có nút "Lưu"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    // Xử lý khi người dùng chọn mục menu
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            // Nếu người dùng chọn "Lưu", gọi phương thức thay đổi mật khẩu
            changePassword();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

}