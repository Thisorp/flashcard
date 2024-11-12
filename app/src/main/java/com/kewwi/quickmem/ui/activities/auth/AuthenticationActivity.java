package com.kewwi.quickmem.ui.activities.auth;

import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.kewwi.quickmem.adapter.viewpager.OnboardingAdapter;
import com.kewwi.quickmem.databinding.ActivityAuthenticationBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.MainActivity;
import com.kewwi.quickmem.ui.activities.auth.signin.SignInActivity;
import com.kewwi.quickmem.ui.activities.auth.signup.SignUpActivity;
// xử lý giao diện và logic cho activity liên quan đến xác thực người dùng ( đăng nhập & đăng ký)
public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //khởi tạo đối tượng lưu trữ thông tin chia sẻ của người dùng
        UserSharePreferences userSharePreferences = new UserSharePreferences(this);

        // kiểm tra nếu người dùng đã đăng nhập, chuyển hướng tới màn hình chính và kết thúc activity hiện tại
        if (userSharePreferences.getLogin()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        // Inflate layout từ XML tương ứng với activity này
        ActivityAuthenticationBinding binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // cài đặt Onboarding ( hứng dẫn sử dụng)
        OnboardingAdapter onboardingAdapter = new OnboardingAdapter(this);
        binding.onboardingVp.setAdapter(onboardingAdapter);//gán adapter vào viewpager
        binding.indicator.setViewPager(binding.onboardingVp);//gán indicator cho viewpager

        // cài đặt nút đăng ký
        binding.signUpBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class)); //chuyển đến đăng nhập
            finish();
        });

        // cài đặt sự kiện đăng nhập
        binding.signInBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));//chuyển đến đăng ký
            finish();
        });

    }
}