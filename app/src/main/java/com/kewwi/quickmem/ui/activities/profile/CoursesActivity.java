package com.kewwi.quickmem.ui.activities.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.databinding.ActivityCoursesBinding;
import com.kewwi.quickmem.databinding.BottomSheetAddCourseBinding;

// lớp điều khiển cho giao diện môn học
public class CoursesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sử dụng ViewBinding để gắn giao diện người dùng với logic
        com.kewwi.quickmem.databinding.ActivityCoursesBinding binding = ActivityCoursesBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);

        // Đặt sự kiện cho nút thêm khóa học
        binding.addCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            // Gọi hàm mở hộp thoại khi người dùng nhấn vào nút "addCourseBtn"
            public void onClick(View view) {
                openDialog();
            }
        });

        // Thiết lập toolbar và sự kiện khi nhấn vào nút quay lại
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Hàm mở hộp thoại (bottom sheet) để thêm khóa học
    private void openDialog() {
        final Dialog dialog = new Dialog(this);// Tạo một hộp thoại mới
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Ẩn tiêu đề của hộp thoại

        // Sử dụng ViewBinding để liên kết layout của bottom sheet với logic
        BottomSheetAddCourseBinding layoutBinding = BottomSheetAddCourseBinding.inflate(getLayoutInflater());

        // Đặt nội dung của hộp thoại là giao diện thêm khóa học
        dialog.setContentView(layoutBinding.getRoot());

        // code xử lý logic liên quan đến thêm khóa học có thể được thêm vào đây


        // Hiển thị hộp thoại và tùy chỉnh các thuộc tính của nó
        dialog.show();
        // Đặt kích thước của hộp thoại
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Đặt nền trong suốt cho hộp thoại
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Áp dụng hiệu ứng chuyển động cho hộp thoại
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        // Đặt vị trí của hộp thoại xuất hiện ở dưới màn hình
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}