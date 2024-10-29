// Tên file: CreateFragment.java
// Chức năng chính: Định nghĩa Fragment dạng BottomSheet cho việc tạo lớp học, thư mục, hoặc tập học (set).
//                  Fragment này hiển thị các tùy chọn khác nhau tùy vào vai trò người dùng (học sinh hay giáo viên).
package com.kewwi.quickmem.ui.fragments.home;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import com.kewwi.quickmem.databinding.FragmentCreateBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateClassActivity;
import com.kewwi.quickmem.ui.activities.create.CreateFolderActivity;
import com.kewwi.quickmem.ui.activities.create.CreateSetActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import org.jetbrains.annotations.NotNull;


public class CreateFragment extends BottomSheetDialogFragment {

    // Binding dữ liệu để truy cập các phần tử giao diện trong XML
    private FragmentCreateBinding binding;
    // Đối tượng lưu trữ tùy chọn người dùng
    UserSharePreferences userSharePreferences;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập hoạt ảnh chuyển đổi khi fragment xuất hiện
        setEnterTransition(new CustomEnterTransition().setDuration(500));
        // Thiết lập hoạt ảnh chuyển đổi khi fragment biến mất
        setExitTransition(new CustomExitTransition().setDuration(500));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo binding với layout FragmentCreate
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Khởi tạo đối tượng để kiểm tra vai trò người dùng
        userSharePreferences = new UserSharePreferences(requireActivity());
        if (userSharePreferences.getRole() == 2) { // 2 đại diện cho học sinh
            binding.llCreateClass.setVisibility(View.GONE);// Ẩn tùy chọn tạo lớp học cho học sinh
        }
        // Xử lý sự kiện khi người dùng nhấn vào "llCreateClass"
        binding.llCreateClass.setOnClickListener(v -> {
            // Tạo hoạt ảnh chuyển đổi cho màn hình "CreateClassActivity"
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.llCreateClass, "transition");
            startActivity(new Intent(requireContext(), CreateClassActivity.class), options.toBundle());
            // Đóng bottom sheet sau khi nhấn
            dismiss();
        });
        // Xử lý sự kiện khi người dùng nhấn vào "llCreateFolder"
        binding.llCreateFolder.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.llCreateFolder, "transition");
            startActivity(new Intent(requireContext(), CreateFolderActivity.class), options.toBundle());
            dismiss();
        });

        // Xử lý sự kiện khi người dùng nhấn vào "llCreateSet"
        binding.llCreateSet.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.llCreateSet, "transition");
            startActivity(new Intent(requireContext(), CreateSetActivity.class), options.toBundle());
            dismiss();
        });
    }

    // Lớp con định nghĩa hoạt ảnh chuyển đổi khi fragment xuất hiện
    private static class CustomEnterTransition extends androidx.transition.Slide {
        public CustomEnterTransition() {
            setSlideEdge(android.view.Gravity.BOTTOM);// Hoạt ảnh xuất hiện từ dưới lên
        }
    }

    // Lớp con định nghĩa hoạt ảnh chuyển đổi khi fragment biến mất
    private static class CustomExitTransition extends androidx.transition.Slide {
        public CustomExitTransition() {
            setSlideEdge(android.view.Gravity.BOTTOM);// Hoạt ảnh biến mất từ dưới ra ngoài
        }
    }

}
