// Tên file: MyClassesFragment.java
// Chức năng chính: Fragment này hiển thị danh sách các lớp học của người dùng, bao gồm cả lớp mà người dùng sở hữu và lớp người dùng là thành viên.
// Người dùng cũng có thể tạo lớp mới thông qua giao diện này.
package com.kewwi.quickmem.ui.fragments.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.adapter.group.ClassCopyAdapter;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.FragmentMyClassesBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateClassActivity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyClassesFragment extends Fragment {

    // Biến binding để liên kết layout XML
    private FragmentMyClassesBinding binding;
    // Đối tượng để truy cập thông tin người dùng đã lưu
    private UserSharePreferences userSharePreferences;
    // Danh sách các lớp học và adapter cho RecyclerView
    private ArrayList<Group> classes;
    private GroupDAO groupDAO;
    private ClassCopyAdapter classAdapter;
    // ID của người dùng hiện tại
    private String idUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo UserSharePreferences và lấy ID người dùng
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
        // Khởi tạo GroupDAO để lấy dữ liệu lớp học
        groupDAO = new GroupDAO(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentMyClassesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập thông tin người dùng và các thành phần UI
        setupUserPreferences();
        setupCreateButton();
        setupClasses();
        setupRecyclerView();
    }

    private void setupUserPreferences() {
        // Lấy lại ID người dùng từ UserSharePreferences để dùng khi cần
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
    }

    private void setupCreateButton() {
        // Kiểm tra quyền của người dùng để hiển thị/ẩn nút tạo lớp học
        UserDAO userDAO = new UserDAO(getContext());
        User user = userDAO.getUserById(idUser);
        if (user.getRole() == 2) {// Nếu người dùng có vai trò cụ thể thì ẩn nút tạo lớp
            binding.createSetBtn.setVisibility(View.GONE);
        }
        // Thiết lập sự kiện cho nút tạo lớp học
        binding.createSetBtn.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateClassActivity.class)));
    }

    private void setupClasses() {
        // Lấy danh sách các lớp học người dùng sở hữu và là thành viên từ cơ sở dữ liệu
        classes = groupDAO.getClassesOwnedByUser(idUser);
        classes.addAll(groupDAO.getClassesUserIsMemberOf(idUser));
        // Cập nhật giao diện dựa trên trạng thái danh sách lớp học
        if (classes.isEmpty()) {
            binding.classesCl.setVisibility(View.VISIBLE);// Hiển thị thông báo nếu không có lớp nào
            binding.classesRv.setVisibility(View.GONE);
        } else {
            binding.classesCl.setVisibility(View.GONE);
            binding.classesRv.setVisibility(View.VISIBLE);// Hiển thị danh sách lớp học
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        // Khởi tạo adapter cho RecyclerView để hiển thị danh sách lớp học
        classAdapter = new ClassCopyAdapter(requireActivity(), classes);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        binding.classesRv.setLayoutManager(linearLayoutManager2);
        binding.classesRv.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi người dùng quay lại fragment này
        refreshData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        // Cập nhật danh sách lớp học và RecyclerView khi có thay đổi
        classes = groupDAO.getClassesOwnedByUser(idUser);
        classes.addAll(groupDAO.getClassesUserIsMemberOf(idUser));

        classAdapter = new ClassCopyAdapter(requireActivity(), classes);
        binding.classesRv.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();

        // Cập nhật giao diện dựa trên trạng thái danh sách lớp học
        if (classes.isEmpty()) {
            binding.classesCl.setVisibility(View.VISIBLE);// Hiển thị thông báo nếu không có lớp nào
            binding.classesRv.setVisibility(View.GONE);
        } else {
            binding.classesCl.setVisibility(View.GONE);
            binding.classesRv.setVisibility(View.VISIBLE);// Hiển thị danh sách lớp học
        }
    }
}