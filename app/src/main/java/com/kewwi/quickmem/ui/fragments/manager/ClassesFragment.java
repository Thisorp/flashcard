// Tên file: ClassesFragment.java
// Chức năng chính: Fragment này hiển thị danh sách các lớp học hiện có và cho phép người dùng xem danh sách lớp.
// Cấu trúc bao gồm liên kết đến dữ liệu lớp học, và cài đặt RecyclerView để hiển thị các lớp học trên giao diện.
package com.kewwi.quickmem.ui.fragments.manager;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.adapter.group.ClassesAdapter;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.databinding.FragmentClassesBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClassesFragment extends Fragment {
    // Sử dụng binding để liên kết layout XML với Fragment
    private FragmentClassesBinding binding;
    // Lưu trữ các thiết lập người dùng
    private UserSharePreferences userSharePreferences;
    // DAO để truy cập dữ liệu nhóm lớp
    private GroupDAO groupDAO;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo đối tượng DAO để truy cập dữ liệu lớp học
        groupDAO = new GroupDAO(getActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentClassesBinding.inflate(inflater, container, false);
        // Thiết lập Toolbar cho Fragment như một Toolbar của Activity
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(binding.toolbar);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy danh sách tất cả các lớp học từ cơ sở dữ liệu
        List<Group> listClasses = new ArrayList<>(groupDAO.getAllClasses());
        // Thiết lập LinearLayoutManager cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        binding.classesRv.setLayoutManager(linearLayoutManager);
        // Khởi tạo và gán adapter cho RecyclerView để hiển thị danh sách lớp học
        ClassesAdapter classesAdapter = new ClassesAdapter(requireActivity(), listClasses);
        binding.classesRv.setAdapter(classesAdapter);
        // Thông báo cho adapter cập nhật dữ liệu
        classesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Xóa binding để tránh rò rỉ bộ nhớ
        binding = null;
    }
}