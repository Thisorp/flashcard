package com.kewwi.quickmem.ui.activities.auth.signin;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ActivitySigninBinding;
import com.kewwi.quickmem.databinding.DialogForGotPasswordBinding;
import com.kewwi.quickmem.databinding.DialogForgotUsernameBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.MainActivity;
import com.kewwi.quickmem.ui.activities.auth.AuthenticationActivity;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
//chức năng đăng  nhập

public class SignInActivity extends AppCompatActivity {
    private User user;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySigninBinding binding = ActivitySigninBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //toolbar
        binding.toolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(SignInActivity.this, AuthenticationActivity.class))
        );

        //sign in by social
        setupSocialSignIn(binding);

        //on text changed
        setupTextChangedListeners(binding);

        //sign in button
        setupSignInButton(binding);

        //forgot username
        binding.usernameTv.setOnClickListener(v -> openDialogUsername());

        //forgot password
        binding.passwordTv.setOnClickListener(v -> openDialogPassword());
    }

    //đăng nhập bằng mạng xã hội
    private void setupSocialSignIn(ActivitySigninBinding binding) {
        binding.googleBtn.setOnClickListener(v -> {
            //intentToMain();
            //tạm thời bị tắt
        });
        binding.facebookBtn.setOnClickListener(v -> {
            //intentToMain();
            //tạm thời bị tắt
        });
    }

    //cài đặt cho thay đổi tài khoản và mật khẩu
    private void setupTextChangedListeners(ActivitySigninBinding binding) {
        //Thêm TextWatcher cho trường email. Khi văn bản trong trường email thay đổi, hàm createEmailTextChangedListener
        // sẽ được gọi để lắng nghe sự kiện và kiểm tra tính hợp lệ của email.
        binding.emailEt.addTextChangedListener(createEmailTextChangedListener(binding));

        //thêm TextWatcher cho trường mật khẩu. Khi văn bản trong trường mật khẩu thay đổi,
        // hàm createPasswordTextChangedListener sẽ được gọi để lắng nghe sự kiện và kiểm tra tính hợp lệ của mật khẩu.
        binding.passwordEt.addTextChangedListener(createPasswordTextChangedListener(binding));
    }

    //cài đặt thay đổi email
    private TextWatcher createEmailTextChangedListener(ActivitySigninBinding binding) {
        return new TextWatcher() {
            // Phương thức này được gọi trước khi văn bản thay đổi.
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /* Phương thức này được gọi trong quá trình văn bản đang thay đổi.
             Khi người dùng thay đổi văn bản trong trường email, phương thức này gọi handleEmailTextChanged để
             kiểm tra tính hợp lệ của email trong thời gian thực.*/
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleEmailTextChanged(s.toString(), binding);
            }

            /* Phương thức này được gọi sau khi người dùng hoàn tất thay đổi văn bản.
               Tương tự như onTextChanged, phương thức này gọi handleEmailTextChanged để kiểm tra tính hợp lệ của email
               sau khi người dùng nhập xong, hỗ trợ cung cấp phản hồi ngay lập tức về tính hợp lệ của email.*/
            @Override
            public void afterTextChanged(Editable s) {
                handleEmailTextChanged(s.toString(), binding);
            }
        };
    }

    //cài đặt thay đổi mật khẩu
    private TextWatcher createPasswordTextChangedListener(ActivitySigninBinding binding) {
        return new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handlePasswordTextChanged(s.toString(), binding);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handlePasswordTextChanged(s.toString(), binding);
            }
        };
    }

    //cài đặt nút đăng nhập
    private void setupSignInButton(ActivitySigninBinding binding) {
        //thiết lập sự kiện khi nhấn vào nút signINBtn
        binding.signInBtn.setOnClickListener(v -> {
            //lấy giá trị từ trường email và mật khẩu
            final String email = binding.emailEt.getText().toString();
            //lấy giá trị mật khẩu từ trường nhập liệu passwordsEt
            String password = binding.passwordEt.getText().toString();

            //kiểm tra tính hợp lệ của email và mật khẩu
            //nếu cả email và mật khẩu hợp lệ, thì gọi hàm handleSignIn để đăng nhập
            if (handleEmailTextChanged(email, binding) && handlePasswordTextChanged(password, binding)) {
                handleSignIn(email, password, binding);
            }
        });
    }

    //đăng nhập = email hoặc mật khẩu
    private void handleSignIn(String email, String password, ActivitySigninBinding binding) {
        //khởi tạo đối tượng UserDAO
        userDAO = new UserDAO(SignInActivity.this);

        // Kiểm tra xem chuỗi nhập vào có định dạng hợp lệ của email hay không.
        // Nếu chuỗi có định dạng email hợp lệ, thì gọi phương thức handleEmailSignIn để đăng nhập bằng email.
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            handleEmailSignIn(email, password, binding);
        }
        //nếu chuỗi không có định dạng email hợp lệ, thì gọi phương thức handleUsernameSignIn để đăng nhập bằng tên đăng nhập.
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            handleUsernameSignIn(email, password, binding);
        }
    }

    //đăng nhập bằng email
    private void handleEmailSignIn(String email, String password, ActivitySigninBinding binding) {
        //kiểm tra email
        if (!userDAO.checkEmail(email)) {
            Toast.makeText(this, "email", Toast.LENGTH_SHORT).show();
            binding.emailTil.setHelperText(getString(R.string.email_is_not_exist));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return;
        }
        //kiểm tra mật khẩu
        if (!userDAO.checkPasswordEmail(email, password)) {
            binding.passwordTil.setHelperText(getString(R.string.password_is_not_correct));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return;
        }
        //lấy thông tin người dùng
        user = getUser(0, email);
        handleUserStatus(user);
    }

    //đăng nhập bằng tên đăng nhập
    private void handleUsernameSignIn(String username, String password, ActivitySigninBinding binding) {
        //kiểm tra tên đăng nhập
        if (!userDAO.checkUsername(username)) {
            binding.emailTil.setHelperText(getString(R.string.user_is_not_exist));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return;
        }
        //kiểm tra mật khẩu
        if (!userDAO.checkPasswordUsername(username, password)) {
            binding.passwordTil.setHelperText(getString(R.string.password_is_not_correct));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            return;
        }
        //nếu tên đăng nhập hợp lệ, thì lấy email của người dùng
        String email = userDAO.getEmailByUsername(username);
        //lấy thông tin người dùng
        user = getUser(1, email);
        handleUserStatus(user);
    }

    //kiểm tra trạng thái người dùng sau khi đăng nhập
    private void handleUserStatus(User user) {
        if (user.getStatus() == 0) {
            // hiển thị một thông báo lỗi bằng PopupDialog khi người dùng bị chặn và không thể đăng nhập.
            PopupDialog.getInstance(SignInActivity.this)
                    .setStyle(Styles.FAILED)
                    .setHeading(getString(R.string.loginFailed))
                    .setDescription(getString(R.string.you_are_blocked))
                    .setPopupDialogIcon(R.drawable.ic_delete)
                    .setCancelable(true)
                    //Hiển thị thông báo và xử lý sự kiện khi người dùng nhấn nút hủy.
                    .showDialog(new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);
                            dialog.dismiss();
                        }
                    });

        } else {
            //lưu thông  tin người  dùng vào SharePreferences và chuyển đến Activity
            UserSharePreferences userSharePreferences = new UserSharePreferences(SignInActivity.this);
            userSharePreferences.setRole(user.getRole());
            userSharePreferences.saveUser(user);
            userSharePreferences.setLogin(true);
            userSharePreferences.setUserName(user.getUsername());
            userSharePreferences.setAvatar(user.getAvatar());
            userSharePreferences.setEmail(user.getEmail());
            intentToMain();
            Toast.makeText(this, "Login Success!", Toast.LENGTH_SHORT).show();
        }
    }

    //chuyển hướng sang màn hình chính
    private void intentToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //kiểm tra tính hợp lệ của mail
    private boolean handleEmailTextChanged(String text, ActivitySigninBinding binding) {
        if (text.isEmpty()) {
            //nếu email rỗng thì hiển thị thông báo lỗi
            binding.emailTil.setHelperText(getString(R.string.email_is_empty));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.emailEt.requestFocus();
            return false;
        }

        //nếu email hợp lệ thì hiển thị thông báo thành công
        binding.emailTil.setHelperText(getString(R.string.email));
        binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
        return true;
    }

    //kiểm tra tính hợp lệ của mật khẩu
    private boolean handlePasswordTextChanged(String text, ActivitySigninBinding binding) {
        if (text.isEmpty()) {
            binding.passwordTil.setHelperText(getString(R.string.password_is_empty));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.passwordEt.requestFocus();
            return false;
        }

        binding.passwordTil.setHelperText(getString(R.string.password));
        binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
        return true;
    }

    //chuyển sang màn hình AuthenticationActivity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignInActivity.this, AuthenticationActivity.class));
    }

    //lấy người dùng  từ email hoặc username
    private User getUser(int number, String input) {
        //Khởi tạo đối tượng UserDAO để truy xuất thông tin người dùng từ cơ sở dữ liệu.
        userDAO = new UserDAO(SignInActivity.this);
        //gán giá trị cho biến đầu vào vào biến email
        String email = input;
        //nếu number = 1 thì lấy email từ username
        if (number == 1) {
            email = userDAO.getEmailByUsername(input);
        }
        return user = userDAO.getUserByEmail(email);// trả về thông tin người dùng qua email
    }

    //mở hộp thoại quên mật khẩu
    private void openDialogUsername() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final DialogForgotUsernameBinding binding = DialogForgotUsernameBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        builder.setView(view);
        builder.setCancelable(true);

        // hiển thị bàn phím
        AlertDialog alertDialog = builder.create();
        binding.cancelTv.setOnClickListener(v -> alertDialog.dismiss());

        //show keyboard automatically
        showKeyboardAutomatically(binding);

        binding.emailEt.addTextChangedListener(createEmailTextWatcher(binding));

        binding.okTv.setOnClickListener(v -> handleOkButtonClick(binding, alertDialog));

        alertDialog.show();
    }

    //Hiển thị bàn phím tự động email và tên người dùng
    private void showKeyboardAutomatically(DialogForgotUsernameBinding binding) {
        //Sử dụng postDelayed để thực hiện một hành động sau 100ms
        binding.emailEt.postDelayed(() -> {
            // Thiết lập tiêu điểm cho EditText email
            binding.emailEt.requestFocus();
            //Gửi sự kiện TOUCH DOWN để bắt đầu tương tác với EditText
            binding.emailEt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
            binding.emailEt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }, 100);
    }


    //Kiểm tra định dạng email
    private TextWatcher createEmailTextWatcher(DialogForgotUsernameBinding binding) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handleEmailUserTextChanged(s.toString(), binding);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleEmailUserTextChanged(s.toString(), binding);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handleEmailUserTextChanged(s.toString(), binding);
            }
        };
    }

    //Xử lý nút OK Nhấp vào
    private void handleOkButtonClick(DialogForgotUsernameBinding binding, AlertDialog alertDialog) {
        //lấy địa chỉ email từ Edittext
        String email = binding.emailEt.getText().toString();

        //kiểm tra email
        if (handleEmailUserTextChanged(email, binding)) {
            //khởi tạo đối tượng UserDAO
            userDAO = new UserDAO(SignInActivity.this);
            //kiểm tra email
            if (!userDAO.checkEmail(email)) {
                binding.emailEt.setError(getString(R.string.email_is_not_exist));
                return;
            } else {
                //nếu email hợp lệ thì hiển thị thông báo thành công
                binding.emailEt.setError(null);
                alertDialog.dismiss();
            }
            //lấy thông tin người dùng
            user = userDAO.getUserByEmail(email);
            Toast.makeText(this, getString(R.string.check_your_email), Toast.LENGTH_SHORT).show();
        }
    }

    //Kiểm tra định dạng email
    private void openDialogPassword() {
        //khởi tạo đối tượng AlertDialog.Builder để xây dựng hộp thoại
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //khởi tạo đối tượng DialogForGotPasswordBinding để liên kết layout
        final DialogForGotPasswordBinding binding = DialogForGotPasswordBinding.inflate(getLayoutInflater());
        //lấy view từ DialogForGotPasswordBinding
        final View view = binding.getRoot();
        //thiết lập view cho builder
        builder.setView(view);
        //thiết lập chế độ hiển thị của hộp thoại
        builder.setCancelable(true);
        //hiển thị hộp thoại
        AlertDialog alertDialog = builder.create();

        //Hiển thị bàn phím tự động
        showKeyboardAutomaticallyPassword(binding);

        //Xử lý sự kiện khi người dùng nhấn nút hủy
        binding.cancelTv.setOnClickListener(v -> alertDialog.dismiss());
        //Xử lý sự kiện khi người dùng thay đổi văn bản trong trường email
        binding.emailOrUsernameEt.addTextChangedListener(createEmailUserForgotPasswordTextWatcher(binding));
        //Xử lý sự kiện khi người dùng nhấn nút OK
        binding.okTv.setOnClickListener(v -> handleOkPasswordButtonClick(binding, alertDialog));

        //hiển thị hộp thoại
        alertDialog.show();
    }


    //Hiển thị bàn phím tự động mật khẩu
    private void showKeyboardAutomaticallyPassword(DialogForGotPasswordBinding binding) {
        binding.emailOrUsernameEt.postDelayed(() -> {
            binding.emailOrUsernameEt.requestFocus();
            binding.emailOrUsernameEt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
            binding.emailOrUsernameEt.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
        }, 100);
    }

    private TextWatcher createEmailUserForgotPasswordTextWatcher(DialogForGotPasswordBinding binding) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handleEmailUserForgotPasswordTextChanged(s.toString(), binding);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleEmailUserForgotPasswordTextChanged(s.toString(), binding);
            }

            @Override
            public void afterTextChanged(Editable s) {
                handleEmailUserForgotPasswordTextChanged(s.toString(), binding);
            }
        };
    }

    //Xử lý nút OK Nhấp vào
    private void handleOkPasswordButtonClick(DialogForGotPasswordBinding binding, AlertDialog alertDialog) {
        String emailOrUsername = binding.emailOrUsernameEt.getText().toString();
        if (!handleEmailUserForgotPasswordTextChanged(emailOrUsername, binding)) {
            return;
        }

        //khởi tạo đối tượng UserDAO
        userDAO = new UserDAO(SignInActivity.this);
        //kiểm tra email
        if (Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
            if (!userDAO.checkEmail(emailOrUsername)) {
                binding.emailOrUsernameEt.setError(getString(R.string.email_is_not_exist));
                return;
            }
            binding.emailOrUsernameEt.setError(null);
            alertDialog.dismiss();
            Toast.makeText(this, getString(R.string.check_your_email), Toast.LENGTH_SHORT).show();
            return;
        }

        //kiểm tra tên đăng nhập
        if (!userDAO.checkUsername(emailOrUsername)) {
            binding.emailOrUsernameEt.setError(getString(R.string.user_is_not_exist));
            return;
        }
        //nếu tên đăng nhập hợp lệ thì lấy email của người dùng
        binding.emailOrUsernameEt.setError(null);
        //đóng hộp thoại
        alertDialog.dismiss();
        Toast.makeText(this, getString(R.string.check_your_email), Toast.LENGTH_SHORT).show();
    }

    //Kiểm tra định dạng email
    private boolean handleEmailUserTextChanged(String text, DialogForgotUsernameBinding binding) {
        //kiểm tra email
        if (text.isEmpty()) {
            binding.emailEt.requestFocus();
            binding.emailEt.setError(getString(R.string.email_is_empty));
            return false;
        }
        //kiểm tra định dạng email
        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            binding.emailEt.requestFocus();
            binding.emailEt.setError(getString(R.string.email_is_invalid));
            return false;
        }
        //nếu email hợp lệ thì hiển thị thông báo thành công
        binding.emailEt.setError(null);
        return true;
    }

    //Kiểm tra định dạng email và tên người dùng
    private boolean handleEmailUserForgotPasswordTextChanged(String text, DialogForGotPasswordBinding binding) {
        //kiểm tra email
        if (text.isEmpty()) {
            binding.emailOrUsernameEt.requestFocus();
            binding.emailOrUsernameEt.setError(getString(R.string.email_is_empty));
            return false;
        }
        //kiểm tra định dạng email
        if (text.contains("@") && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            binding.emailOrUsernameEt.requestFocus();
            binding.emailOrUsernameEt.setError(getString(R.string.email_is_invalid));
            return false;
        }

        //nếu email hợp lệ thì hiển thị thông báo thành công
        binding.emailOrUsernameEt.setError(null);
        return true;
    }
}