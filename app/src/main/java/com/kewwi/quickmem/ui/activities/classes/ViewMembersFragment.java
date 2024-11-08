package com.kewwi.quickmem.ui.activities.classes;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.kewwi.quickmem.adapter.user.UserClassAdapter;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.FragmentViewMembersBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ViewMembersFragment extends Fragment {
    private FragmentViewMembersBinding binding;// Binding layout fragment
    private UserSharePreferences userSharePreferences;// Lưu trữ thông tin người dùng
    private UserDAO userDAO;// Quản lý dữ liệu người dùng
    private GroupDAO groupDAO;// Quản lý dữ liệu nhóm/lớp học

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo các đối tượng dùng để tương tác với dữ liệu người dùng và nhóm
        userSharePreferences = new UserSharePreferences(requireActivity());
        userDAO = new UserDAO(requireContext());
        groupDAO = new GroupDAO(requireContext());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho Fragment và trả về view gốc
        binding = FragmentViewMembersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy dữ liệu nhóm hiện tại dựa trên ID nhóm từ UserSharePreferences
        Group group = groupDAO.getGroupById(userSharePreferences.getClassId());

        // Khởi tạo danh sách người dùng, bao gồm chủ nhóm và các thành viên
        ArrayList<User> users = new ArrayList<>();
        users.add(userDAO.getUserByIdClass(group.getUser_id()));// Thêm chủ nhóm
        users.addAll(userDAO.getListUserByIdClass(userSharePreferences.getClassId()));

        UserClassAdapter userClassAdapter = new UserClassAdapter(users, false, group.getId());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.membersRv.setLayoutManager(linearLayoutManager);// Sắp xếp danh sách theo chiều dọc
        binding.membersRv.setAdapter(userClassAdapter);// Gắn Adapter vào RecyclerView
        binding.membersRv.setHasFixedSize(true);// Cải thiện hiệu suất khi danh sách có kích thước cố định
        userClassAdapter.notifyDataSetChanged();// Thông báo cho Adapter cập nhật dữ liệu hiển thị

    }
}