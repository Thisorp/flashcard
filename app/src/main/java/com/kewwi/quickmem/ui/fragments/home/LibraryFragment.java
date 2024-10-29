// Tên file: LibraryFragment.java
// Chức năng chính: Định nghĩa fragment thư viện với viewPager và tab layout. Cho phép người dùng tạo mới bộ flashcard, thư mục hoặc lớp học
//                  dựa trên tab đang chọn và quyền truy cập của người dùng.
package com.kewwi.quickmem.ui.fragments.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.adapter.viewpager.MyViewPagerAdapter;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.FragmentLibraryBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateClassActivity;
import com.kewwi.quickmem.ui.activities.create.CreateFolderActivity;
import com.kewwi.quickmem.ui.activities.create.CreateSetActivity;
import com.google.android.material.tabs.TabLayout;
import org.jetbrains.annotations.NotNull;


public class LibraryFragment extends Fragment {
    // Biến binding để liên kết layout XML
    private FragmentLibraryBinding binding;
    // Biến quản lý thông tin người dùng, ID, và tab đang chọn
    private UserSharePreferences userSharePreferences;
    private int currentTabPosition = 0;
    private String idUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo SharePreferences và lấy ID người dùng
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentLibraryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập viewPager, tab layout, thông tin người dùng và nút thêm (+)
        setupViewPager();
        setupTabLayout();
        setupUserPreferences();
        setupAddButton();
    }

    // Thiết lập ViewPager để chuyển đổi giữa các tab với MyViewPagerAdapter
    private void setupViewPager() {
        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(
                getChildFragmentManager(),
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        );
        binding.viewPager.setAdapter(myViewPagerAdapter);
    }

    // Thiết lập TabLayout để liên kết với ViewPager và cập nhật visibility cho nút thêm (+)
    private void setupTabLayout() {
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();// Lưu vị trí tab hiện tại
                updateAddButtonVisibility();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // Cập nhật thông tin người dùng và thiết lập hiển thị nút thêm nếu người dùng có quyền
    private void setupUserPreferences() {
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
        UserDAO userDAO = new UserDAO(getContext());
        User user = userDAO.getUserById(idUser);
        if (user.getRole() == 2) {// Kiểm tra nếu người dùng có vai trò đặc biệt (giáo viên)
            updateAddButtonVisibility();
        }
    }

    // Thiết lập hành động cho nút thêm (+) dựa trên tab đang chọn (bộ flashcard, thư mục, hoặc lớp học)
    private void setupAddButton() {
        binding.addBtn.setOnClickListener(view1 -> {
            if (currentTabPosition == 0) {
                startActivity(new Intent(getActivity(), CreateSetActivity.class));
            } else if (currentTabPosition == 1) {
                startActivity(new Intent(getActivity(), CreateFolderActivity.class));
            } else if (currentTabPosition == 2) {
                startActivity(new Intent(getActivity(), CreateClassActivity.class));
            }
        });
    }

    // Cập nhật hiển thị của nút thêm (+) dựa trên quyền người dùng và tab hiện tại
    private void updateAddButtonVisibility() {
        if (userSharePreferences.getRole() == 2 && currentTabPosition == 2) {
            binding.addBtn.setVisibility(View.GONE);// Ẩn nếu tab lớp học và người dùng có quyền đặc biệt
        } else {
            binding.addBtn.setVisibility(View.VISIBLE);// Hiển thị trong các trường hợp khác
        }
    }
}