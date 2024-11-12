// Tên file: ProfileFragment.java
// Chức năng chính: Định nghĩa fragment hiển thị hồ sơ người dùng, bao gồm tên, ảnh đại diện, và điều hướng tới cài đặt hồ sơ.
package com.kewwi.quickmem.ui.fragments.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.databinding.FragmentProfileBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.profile.SettingsActivity;
import com.kewwi.quickmem.ui.activities.profile.FirebaseSettingActivity;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {
    // Biến binding để liên kết layout XML
    private FragmentProfileBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không có xử lý đặc biệt khi khởi tạo
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Khởi tạo đối tượng UserSharePreferences để lấy thông tin người dùng
        UserSharePreferences userSharePreferences = new UserSharePreferences(requireActivity());
        // Thiết lập tên người dùng trên giao diện
        binding.nameProfileTv.setText(userSharePreferences.getUserName());
        // Sử dụng thư viện Picasso để tải ảnh đại diện người dùng từ URL
        Picasso.get().load(userSharePreferences.getAvatar()).into(binding.profileIv);

        // Xử lý sự kiện khi nhấn nút cài đặt, điều hướng tới SettingsActivity
        binding.settingsBtn.setOnClickListener(view12 -> startActivity(new Intent(requireActivity(), SettingsActivity.class)));

        // Xử lý sự kiện khi nhấn nút cài đặt, điều hướng tới SettingsActivity
        binding.settingsBtn.setOnClickListener(view12 -> startActivity(new Intent(requireActivity(), FirebaseSettingActivity.class)));


    }
}