package com.kewwi.quickmem.ui.activities.auth.signup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ActivitySignupBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.MainActivity;
import com.kewwi.quickmem.ui.activities.auth.AuthenticationActivity;
import com.kewwi.quickmem.utils.PasswordHasher;
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerDialog;
import com.swnishan.materialdatetimepicker.datepicker.MaterialDatePickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

//đăng ký tài khoản
public class SignUpActivity extends AppCompatActivity {

    // Khai báo UserDAO để tương tác với cơ sở dữ liệu người dùng
    private UserDAO userDAO;
    // Định nghĩa chiều dài tối đa cho trường nhập liệu
    private static final int MAX_LENGTH = 30;
    // Định nghĩa đường dẫn đến thư mục chứa các ảnh đại diện người dùng
    private static final String link = "https://avatar-nqm.koyeb.app/images";

    // Khai báo binding để truy cập các thành phần giao diện
    ActivitySignupBinding binding;

    // Phương thức onCreate được gọi khi Activity được tạo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng binding để truy cập các thành phần giao diện
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        // Lấy root view của binding để đặt làm nội dung của Activity
        View view = binding.getRoot();
        // Đặt nội dung của Activity bằng root view
        setContentView(view);
        // Thiết lập toolbar
        setSupportActionBar(binding.toolbar);

        //Gọi các phương thức thiết lập các thành phần giao diện
        setupToolbar();
        setupSocialLoginButtons();
        setupDateEditText();
        setupEmailEditText();
        setupPasswordEditText();
        setupSignUpButton();
        setupOnBackPressedCallback();
    }

    // Thiết lập toolbar
    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        });
    }

    // Thiết lập các nút đăng nhập bằng mạng xã hội
    private void setupSocialLoginButtons() {
        binding.facebookBtn.setOnClickListener(v -> {
            // Gọi hàm chuyển đến MainActivity sau khi đăng nhập Facebook
            // intentToMain();
        });

        binding.googleBtn.setOnClickListener(v -> {
            // Gọi hàm chuyển đến MainActivity sau khi đăng nhập Google
            // intentToMain();
        });
    }

    //thiết lập ngày tháng
    private void setupDateEditText() {
        // Đặt sự kiện nhấp chuột cho trường nhập liệu ngày tháng
        binding.dateEt.setOnClickListener(v -> openDialogDatePicker(binding.dateEt::setText));

        //Thêm TextWatcher để theo dõi thay đổi trong trường nhập liệu ngày tháng
        binding.dateEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //xử lý văn bản khi nói thay đổi trong trường ngày tháng
                handleDateTextChanged(s.toString(), binding);
            }

            //xử lý văn bản khi văn bản hoàn tất thay đổi
            @Override
            public void afterTextChanged(Editable s) {
                handleDateTextChanged(s.toString(), binding);
            }
        });
    }

    // Phương thức thiết lập trường nhập liệu email
    private void setupEmailEditText() {
        //thêm TextWatcher để theo dõi thay đổi trong trường nhập liệu email
        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //xử lý văn bản khi văn bản thay đổi
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleEmailTextChanged(s.toString(), binding);
            }

            //xử lý văn bản khi văn bản hoàn tất thay đổi
            @Override
            public void afterTextChanged(Editable s) {
                handleEmailTextChanged(s.toString(), binding);
            }
        });
    }

    // Phương thức thiết lập trường nhập liệu mật khẩu
    private void setupPasswordEditText() {
        //thêm TextWatcher để theo dõi thay đổi trong trường nhập liệu mật khẩu
        binding.passwordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            //xử lý mật khẩu khi văn bản thay đổi
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handlePasswordTextChanged(s.toString(), binding);
            }

            //xử lý mật khẩu khi văn bản hoàn tất thay đổi
            @Override
            public void afterTextChanged(Editable s) {
                handlePasswordTextChanged(s.toString(), binding);
            }
        });
    }

    // Phương thức thiết lập nút đăng ký
    private void setupSignUpButton() {
        //xử lý sự kiện khi người dùng nhấn nút đăng ký
        binding.signUpBtn.setOnClickListener(v -> {
            //lấy giá trị từ các trường nhập liệu
            final String date = binding.dateEt.getText().toString();
            final String email = binding.emailEt.getText().toString();
            final String password = binding.passwordEt.getText().toString();

            //kiểm tra tính hợp lệ của các trường nhập liệu
            if (!handleDateTextChanged(date, binding)) return;
            if (!handleEmailTextChanged(email, binding)) return;
            if (!handlePasswordTextChanged(password, binding)) return;

            //lấy trạng thái người dùng
            int role = 2;
            // Nếu radio button "Có" được chọn, cập nhật vai trò thành 1
            if (binding.radioYesNo.getCheckedRadioButtonId() == binding.radioYes.getId()) {
                role = 1;
            }

            // Gọi hàm handleSignUp để xử lý đăng ký
            handleSignUp(date, email, password, role);
        });
    }

    //xử lý đăng ký
    private void handleSignUp(String date, String email, String password, int role) {
        // tạo 1 đối tượng user mới
        User newUser = new User();

        //đặt các giá trị cho đối tượng user
        newUser.setId(UUID.randomUUID().toString());
        newUser.setEmail(email);
        newUser.setAvatar(randomAvatar());
        newUser.setName("");
        newUser.setUsername(userNameFromEmail(email));
        newUser.setRole(role);
        newUser.setPassword(PasswordHasher.hashPassword(password));

        //set ngày tạo và cập nhật
        newUser.setCreated_at(date);
        newUser.setUpdated_at(date);
        newUser.setStatus(1); // Gán trạng thái là 1 (giả sử 1 là trạng thái cho người dùng hoạt động)

        // Chèn người dùng mới vào cơ sở dữ liệu
        userDAO = new UserDAO(this);
        //thực hiện chèn người dùng mới vào cơ sở dữ liệu
        long result = userDAO.insertUser(newUser);

        //  Kiểm tra kết quả của thao tác chèn
        if (result > 0) {
            // Người dùng đã được chèn thành công
            Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show();

            //Lưu người dùng vào shared preferences
            saveUserToSharedPreferences(newUser);

            // chuyển sang activity
            intentToMain();
        } else {

            Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show();
        }
        //in ra kết quả của thao tác chèn
        Log.d("SignUpActivity", "handleSignUp: " + result + newUser.getAvatar());
    }

    //Phương thức để tạo một đường dẫn ngẫu nhiên đến ảnh đại diện
    private String randomAvatar() {
        //tạo 1 số ngẫu nhiên(1-30)
        int random = new Random().nextInt(MAX_LENGTH);
        return link + "/" + random + ".png";
    }

    //lấy tên người dùng từ email
    private String userNameFromEmail(String email) {
        if (email.contains("@")) {
            // Lấy phần tên người dùng từ email (phần trước ký tự '@')
            String userName = email.substring(0, email.indexOf("@"));
            // Kiểm tra độ dài tên người dùng và đặt tên mặc định nếu cần thiết
            if (userName.length() > 18 || userName.length() < 4) {
                return "quickmem" + new Random().nextInt(100000);
            }
            return userName;
        } else {
            return email.substring(0, email.indexOf("@"));
        }
    }

    //lưu người dùng vào shared preferences
    private void saveUserToSharedPreferences(User user) {
        UserSharePreferences userSharePreferences = new UserSharePreferences(this);
        userSharePreferences.saveUser(user);
    }

    //xử lý sự kiện khi người dùng nhấn nút trở lại
    private void setupOnBackPressedCallback() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            //xử lý sự kiện khi người dùng nhấn nút trở lại
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(SignUpActivity.this, AuthenticationActivity.class));
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }


    //chuyển sang màn hình chính
    private void intentToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //interface để xử lý sự kiện chọn ngày
    public interface DatePickCallback {
        void onDatePicked(String date);
    }

    //hiển thị dialog chọn ngày
    private void openDialogDatePicker(DatePickCallback callback) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            MaterialDatePickerDialog builder = MaterialDatePickerDialog.Builder
                    .setTitle(getString(R.string.date_picker_title))
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText("Cancel")
                    .setDate(OffsetDateTime.now().toInstant().toEpochMilli())
                    .setDateFormat(MaterialDatePickerView.DateFormat.DD_MM_YYYY)
                    .setFadeAnimation(350L, 1050L, .3f, .7f)
                    .build();

            builder.setOnDatePickListener(l -> {
                String date = builder.getDayOfMonth() + "/" + builder.getMonth() + "/" + builder.getYear();
                callback.onDatePicked(date);
            });
            builder.show(getSupportFragmentManager(), "tag");
        }
    }

    //lấy ngày hiện tại theo định dạng "dd/MM/yyyy"
    private String getCurrentDate() {
        //kiểm tra phiên bản android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return currentDate.format(formatter);
        } else {
            // Xử lý trường hợp cho các phiên bản Android nhỏ hơn Oreo
            // sử dụng SimpleDateFormat, có sẵn trên tất cả các phiên bản Android
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            return sdf.format(new Date());
        }
    }

    //kiểm tra ngày có lớn hơn ngày hiện tại
    @SuppressLint("SimpleDateFormat")
    private boolean isDateGreaterThanCurrentDate(String dateStr) {
        //kiểm tra ngày có hợp lệ
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            //chuyển đổi chuỗi ngày thành đối tượng Date
            Date date = formatter.parse(dateStr);
            //lấy ngày hiện tại
            Date currentDate = new Date();
            //đảm bảo ngày không null
            assert date != null;
            //so sánh ngày với ngày hiện tại
            return date.after(currentDate);
        } catch (ParseException e) {
            Log.e("SignupActivity", "isDateGreaterThanCurrentDate: Error parsing date. Ensure the date is in the format dd/MM/yyyy.", e);
            return false;
        }
    }

    //kiểm tra tuổi
    @SuppressLint("SimpleDateFormat")
    private boolean isAgeGreaterThan18(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            // Xử lý trường hợp chuỗi ngày rỗng
            return false;
        }

        //định dạng ngày
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            //chuyển đổi chuỗi ngày thành đối tượng Date
            Date date = formatter.parse(dateStr);
            Calendar calendar = Calendar.getInstance();//lấy ngày hiện tại
            calendar.setTime(new Date());//lấy ngày hiện tại
            calendar.add(Calendar.YEAR, -18);//đặt ngày 18 tuổi
            Date eighteenYearsAgo = calendar.getTime();//Lấy thời điểm 18 năm trước
            assert date != null;//đảm bảo ngày không null
            return date.before(eighteenYearsAgo);//Kiểm tra xem ngày nhập vào có trước 18 năm không
        } catch (ParseException e) {
            Log.e("SignUpActivity", "isAgeGreaterThan18: Error parsing date. Ensure the date is in the format dd/MM/yyyy.", e);
            return false;
        }
    }


    //xử lý thay đổi trong ô nhập ngày
    private boolean handleDateTextChanged(String text, ActivitySignupBinding binding) {
        String currentDate = getCurrentDate();//lấy ngày hiện tại
        //kiểm tra ngày có hợp lệ
        if (text.equals(currentDate)) {

            binding.dateTil.setHelperText(getString(R.string.date_error));//hiển thị thông báo lỗi
            binding.dateTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));//đặt màu sắc cho thông báo lỗi
            binding.teacherLl.setVisibility(View.GONE);//ẩn ô nhập thông tin giáo viên
            enableButton(false, binding);//đặt nút đăng ký vô hiệu
            binding.dateEt.requestFocus();//đặt con trỏ vào ô nhập ngày

            return false;
        } else if (isDateGreaterThanCurrentDate(text)) {
            //nếu ngày nhập lớn hơn ngày hiện tại
            binding.dateTil.setHelperText(getString(R.string.date_geater_than_current_date));
            binding.dateTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            binding.teacherLl.setVisibility(View.GONE);
            enableButton(false, binding);
            binding.dateEt.requestFocus();

            return false;
        } else if (isAgeGreaterThan18(text)) {
            //nếu tuổi lớn hơn 18
            binding.teacherLl.setVisibility(View.VISIBLE);
            binding.dateTil.setHelperText(getString(R.string.date_of_birth));
            binding.dateTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
            enableButton(true, binding);

            return true;
        } else {
            // nếu  ngày hợp lệ
            binding.dateTil.setHelperText(getString(R.string.date_of_birth));
            binding.dateTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
            binding.teacherLl.setVisibility(View.GONE);
            enableButton(true, binding);

            return true;
        }
    }

    //kiểm tra email
    private boolean handleEmailTextChanged(String text, ActivitySignupBinding binding) {
        if (text.isEmpty()) {
            //nếu email rỗng
            binding.emailTil.setHelperText(getString(R.string.email_is_empty));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            enableButton(false, binding);
            binding.emailEt.requestFocus();

            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            //nếu email không hợp lệ
            binding.emailTil.setHelperText(getString(R.string.email_is_invalid));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            enableButton(false, binding);
            binding.emailEt.requestFocus();

            return false;
        } else if (isEmailExist(text)) {
            //nếu email đã tồn tại
            binding.emailTil.setHelperText(getString(R.string.email_is_exist));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            enableButton(false, binding);
            binding.emailEt.requestFocus();
        } else {
            //nếu email hợp lệ
            binding.emailTil.setHelperText(getString(R.string.email));
            binding.emailTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
            enableButton(true, binding);

            return true;
        }
        return false;
    }

    //kiểm tra mật khẩu
    private boolean handlePasswordTextChanged(String text, ActivitySignupBinding binding) {
        if (text.isEmpty()) {
            //nếu mật khẩu rỗng
            binding.passwordTil.setHelperText(getString(R.string.password_is_empty));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            enableButton(false, binding);
            binding.passwordEt.requestFocus();

            return false;
        } else if (text.length() < 8) {
            //nếu mật khẩu nhỏ hơn 8 ký tự
            binding.passwordTil.setHelperText(getString(R.string.password_is_invalid));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.RED));
            enableButton(false, binding);
            binding.passwordEt.requestFocus();

            return false;
        } else {
            //mật khẩu hợp lệ
            binding.passwordTil.setHelperText(getString(R.string.password));
            binding.passwordTil.setHelperTextColor(ColorStateList.valueOf(Color.GRAY));
            enableButton(true, binding);

            return true;
        }

    }

    //kiểm tra trạng thái nút đăng ký
    private void enableButton(Boolean check, ActivitySignupBinding binding) {
        if (check) {
            binding.signUpBtn.setEnabled(true);//đặt nút đăng ký là khả dụng
            binding.signUpBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.button_background));//đặt màu nền cho nút đăng ký
        } else {
            binding.signUpBtn.setEnabled(false);//đặt nút đăng ký là vô hiệu
            binding.signUpBtn.setBackground(AppCompatResources.getDrawable(this, R.drawable.background_button_disable));//đặt màu nền cho nút đăng ký
        }
    }

    //kiểm tra email đã tồn tại
    private boolean isEmailExist(String email) {
        //hởi tạo đối tượng UserDAO để thực hiện các thao tác với cơ sở dữ liệu người dùng
        userDAO = new UserDAO(this);
        //Gọi phương thức checkEmail từ UserDAO để kiểm tra xem email đã tồn tại hay chưa
        return userDAO.checkEmail(email);
    }


}