// Tên file: FoldersFragment.java
// Chức năng chính: Fragment này hiển thị danh sách các thư mục của người dùng và cho phép tạo thư mục mới nếu cần.
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

import com.kewwi.quickmem.adapter.folder.FolderCopyAdapter;
import com.kewwi.quickmem.data.dao.FolderDAO;
import com.kewwi.quickmem.data.model.Folder;
import com.kewwi.quickmem.databinding.FragmentFoldersBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateFolderActivity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FoldersFragment extends Fragment {
    // Biến binding để liên kết layout XML
    private FragmentFoldersBinding binding;
    // Đối tượng để truy cập thông tin người dùng đã lưu
    private UserSharePreferences userSharePreferences;
    // Danh sách các thư mục và adapter cho RecyclerView
    private ArrayList<Folder> folders;
    private FolderCopyAdapter folderAdapter;
    // DAO để tương tác với dữ liệu thư mục trong cơ sở dữ liệu
    private FolderDAO folderDAO;
    // ID của người dùng hiện tại
    private String idUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo UserSharePreferences và lấy ID người dùng
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
        // Khởi tạo FolderDAO để lấy dữ liệu thư mục
        folderDAO = new FolderDAO(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentFoldersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập thông tin người dùng và thiết lập các thành phần UI
        setupUserPreferences();
        setupCreateButton();
        setupFolders();
        setupRecyclerView();
    }

    private void setupUserPreferences() {
        // Lấy lại ID người dùng từ UserSharePreferences để dùng khi cần
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
    }

    private void setupCreateButton() {
        // Xử lý sự kiện khi nhấn nút tạo mới thư mục, chuyển đến màn hình CreateFolderActivity
        binding.createSetBtn.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateFolderActivity.class)));
    }

    private void setupFolders() {
        // Lấy danh sách thư mục từ CSDL dựa trên ID người dùng
        folders = folderDAO.getAllFolderByUserId(idUser);
        // Kiểm tra danh sách thư mục trống hay không để cập nhật giao diện
        if (folders.isEmpty()) {
            binding.folderCl.setVisibility(View.VISIBLE);// Hiển thị thông báo không có thư mục
            binding.foldersRv.setVisibility(View.GONE);
        } else {
            binding.folderCl.setVisibility(View.GONE);
            binding.foldersRv.setVisibility(View.VISIBLE);// Hiển thị danh sách thư mục
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        // Khởi tạo adapter cho RecyclerView để hiển thị danh sách thư mục
        folderAdapter = new FolderCopyAdapter(requireActivity(), folders);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        binding.foldersRv.setLayoutManager(linearLayoutManager1);
        binding.foldersRv.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi người dùng quay lại fragment này
        refreshData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        // Lấy lại danh sách thư mục từ CSDL và cập nhật RecyclerView
        folders = folderDAO.getAllFolderByUserId(idUser);

        folderAdapter = new FolderCopyAdapter(requireActivity(), folders);
        binding.foldersRv.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();

        // Cập nhật giao diện dựa trên trạng thái danh sách thư mục
        if (folders.isEmpty()) {
            binding.folderCl.setVisibility(View.VISIBLE);// Hiển thị thông báo nếu trống
            binding.foldersRv.setVisibility(View.GONE);
        } else {
            binding.folderCl.setVisibility(View.GONE);
            binding.foldersRv.setVisibility(View.VISIBLE);// Hiển thị danh sách nếu có dữ liệu
        }
    }
}