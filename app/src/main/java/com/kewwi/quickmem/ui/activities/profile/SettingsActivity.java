package com.kewwi.quickmem.ui.activities.profile;

import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.databinding.ActivitySettingsBinding;

import com.kewwi.quickmem.databinding.DialogChangeEmailBinding;
import com.kewwi.quickmem.databinding.DialogChangeUsernameBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.auth.signin.SignInActivity;
import com.kewwi.quickmem.ui.activities.profile.change.ChangeEmailActivity;
import com.kewwi.quickmem.ui.activities.profile.change.ChangePasswordActivity;
import com.kewwi.quickmem.ui.activities.profile.change.ChangeUsernameActivity;
import com.kewwi.quickmem.utils.PasswordHasher;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import java.util.Objects;

// lớp điều khiển cho giao diện cài đặt
public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding; // Khai báo đối tượng binding cho layout của activity
    private UserSharePreferences userSharePreferences; // Đối tượng dùng để quản lý shared preferences của người dùng
    private AlertDialog detailDialog; // Hộp thoại để hiển thị các chi tiết thay đổi thông tin người dùng
    UserDAO userDAO;// Đối tượng Data Access Object dùng để làm việc với dữ liệu người dùng


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo ViewBinding cho activity
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        final View view = binding.getRoot();
        setContentView(view);
        // Khởi tạo đối tượng quản lý thông tin người dùng từ Shared Preferences
        userSharePreferences = new UserSharePreferences(SettingsActivity.this);

        // Hiển thị tên người dùng hiện tại từ Shared Preferences
        binding.usernameTv.setText(userSharePreferences.getUserName());

        // Hiển thị email người dùng hiện tại từ Shared Preferences
        binding.emailTv.setText(userSharePreferences.getEmail());

        onClickItemSetting();// Gọi hàm thiết lập sự kiện khi người dùng tương tác với các tùy chọn

        // Thiết lập toolbar
        setSupportActionBar(binding.toolbar);

        // Đặt sự kiện quay lại khi nhấn vào nút quay lại trên toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    // Hàm thiết lập sự kiện khi người dùng nhấn vào các tùy chọn trong phần cài đặt
    private void onClickItemSetting() {
        // Khi nhấn vào tùy chọn thay đổi tên, mở hộp thoại thay đổi tên
        binding.usernameCl.setOnClickListener(view -> openDialogChangeUsername());

        // Khi nhấn vào tùy chọn thay đổi email, mở hộp thoại thay đổi email
        binding.emailCl.setOnClickListener(view -> openDialogChangeEmail());

        // Khi nhấn vào tùy chọn thay đổi mật khẩu, mở activity thay đổi mật khẩu
        binding.passwordCl.setOnClickListener(view -> {
            startActivity(new Intent(SettingsActivity.this, ChangePasswordActivity.class));
        });

        // Khi nhấn vào nút đăng xuất
        binding.logOutBtn.setOnClickListener(v -> {
            PopupDialog.getInstance(SettingsActivity.this)
                    .setStyle(Styles.STANDARD)// Thiết lập kiểu popup
                    .setHeading("Log out!") // Tiêu đề của popup
                    .setDescription("Are you sure")// Mô tả yêu cầu xác nhận
                    .setPopupDialogIcon(R.drawable.baseline_logout_24) // Đặt biểu tượng cho popup
                    .setCancelable(true) // Cho phép popup có thể bị hủy bỏ
                    .setPositiveButtonText("OK") // Đặt nhãn cho nút xác nhận
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onPositiveClicked(Dialog dialog) {
                            super.onPositiveClicked(dialog);
                            // Lấy thông tin người dùng
                            userSharePreferences = new UserSharePreferences(SettingsActivity.this);

                            // Xóa thông tin người dùng từ Shared Preferences khi đăng xuất
                            userSharePreferences.clear();

                            // Chuyển về màn hình đăng nhập
                            Intent intent = new Intent(SettingsActivity.this, SignInActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent); // Khởi chạy Intent để mở màn hình đăng nhập
                            finish(); // Kết thúc Activity hiện tại
                        }

                        @Override
                        public void onNegativeClicked(Dialog dialog) {
                            super.onNegativeClicked(dialog);
                            dialog.dismiss();// Đóng popup nếu người dùng chọn hủy
                        }
                    });


        });
    }

    // Mở hộp thoại thay đổi email
    private void openDialogChangeEmail() {
        // Khởi tạo đối tượng DAO để truy xuất dữ liệu người dùng
        userDAO = new UserDAO(SettingsActivity.this);
        // Khởi tạo ViewBinding cho hộp thoại
        DialogChangeEmailBinding changeEmailBinding = DialogChangeEmailBinding.inflate(LayoutInflater.from(SettingsActivity.this));
        View view = changeEmailBinding.getRoot();

        // Tạo đối tượng AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(view);
        detailDialog = builder.create(); // Khởi tạo hộp thoại
        detailDialog.setCanceledOnTouchOutside(false); // Đặt hộp thoại không tự đóng khi nhấn bên ngoài
        detailDialog.show();// Hiển thị hộp thoại
        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));// Đặt nền hộp thoại trong suốt

        changeEmailBinding.cancelChangeEmailBtn.setOnClickListener(view1 -> detailDialog.dismiss());// Đóng hộp thoại khi nhấn hủy

        // Xử lý khi người dùng nhấn nút xác nhận thay đổi email
        changeEmailBinding.submitChangeEmailBtn.setOnClickListener(v -> {
//            startActivity(new Intent(SettingsActivity.this, ChangeEmailActivity.class));

            // Lấy thông tin người dùng từ Shared Preferences
            String password = changeEmailBinding.passwordEt.getText().toString().trim();


            userSharePreferences = new UserSharePreferences(SettingsActivity.this);
            String id = userSharePreferences.getId();
            if (password.isEmpty()) {
                // Kiểm tra mật khẩu trống
                changeEmailBinding.textIL.setHelperText("Please enter your password");
            } else if (!Objects.equals(PasswordHasher.hashPassword(password), userDAO.getPasswordUser(id))) {
                Toast.makeText(this, "Pass: " + userDAO.getPasswordUser(id), Toast.LENGTH_SHORT).show();
                changeEmailBinding.textIL.setHelperText("Password is incorrect");// Thông báo sai mật khẩu
            } else {
                changeEmailBinding.textIL.setHelperText("");
                // Mở activity thay đổi email nếu mật khẩu đúng
                startActivity(new Intent(SettingsActivity.this, ChangeEmailActivity.class));
                detailDialog.dismiss();
            }
        });
    }

    // Mở hộp thoại thay đổi tên người dùng
    private void openDialogChangeUsername() {
        // Khởi tạo đối tượng DAO để truy xuất dữ liệu người dùng
        userDAO = new UserDAO(SettingsActivity.this);
        // Khởi tạo ViewBinding cho hộp thoại
        DialogChangeUsernameBinding changeUsernameBinding = DialogChangeUsernameBinding.inflate(LayoutInflater.from(SettingsActivity.this));

        View view = changeUsernameBinding.getRoot();

        // Tạo đối tượng AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setView(view);
        detailDialog = builder.create();// Khởi tạo hộp thoại
        detailDialog.setCanceledOnTouchOutside(false);// Đặt hộp thoại không tự đóng khi nhấn bên ngoài
        detailDialog.show();// Hiển thị hộp thoại
        detailDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        changeUsernameBinding.cancelChangeName.setOnClickListener(view1 -> detailDialog.dismiss());// Đóng hộp thoại khi nhấn hủy

        // Xử lý khi người dùng nhấn nút xác nhận thay đổi tên
        changeUsernameBinding.submitChangeName.setOnClickListener(v -> {
//           startActivity(new Intent(SettingsActivity.this, ChangeUsernameActivity.class));
            String password = changeUsernameBinding.passwordEt.getText().toString().trim();// Lấy mật khẩu từ ô nhập liệu
            userSharePreferences = new UserSharePreferences(SettingsActivity.this); // Lấy thông tin người dùng từ Shared Preferences
            String id = userSharePreferences.getId();
            if (password.isEmpty()) {
                changeUsernameBinding.textIL.setHelperText("Please enter your password"); // Kiểm tra mật khẩu trống
            } else if (!Objects.equals(PasswordHasher.hashPassword(password), userDAO.getPasswordUser(id))) {
                changeUsernameBinding.textIL.setHelperText("Password is incorrect");// Thông báo sai mật khẩu
            } else {
                changeUsernameBinding.textIL.setHelperText("");
                // Mở activity thay đổi tên nếu mật khẩu đúng
                startActivity(new Intent(SettingsActivity.this, ChangeUsernameActivity.class));
                detailDialog.dismiss();// Đóng hộp thoại
            }
        });
    }
}