// Tên file: HomeFragment.java
// Chức năng chính: Định nghĩa fragment cho màn hình chính (Home) hiển thị flashcards, thư mục, và lớp học.
//                  Cho phép người dùng xem, làm mới, và quản lý các mục, với chức năng làm mới qua SwipeRefresh.
package com.kewwi.quickmem.ui.fragments.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kewwi.quickmem.adapter.group.ClassAdapter;
import com.kewwi.quickmem.adapter.folder.FolderAdapter;
import com.kewwi.quickmem.adapter.flashcard.SetsAdapter;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.dao.FolderDAO;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.data.model.Folder;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.databinding.FragmentHomeBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateSetActivity;
import com.kewwi.quickmem.ui.activities.search.ViewSearchActivity;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;


public class HomeFragment extends Fragment {
    // Biến binding để liên kết layout XML
    private FragmentHomeBinding binding;
    // Các biến quản lý dữ liệu, adapter và DAO để lấy dữ liệu từ CSDL
    private UserSharePreferences userSharePreferences;
    private SetsAdapter setsAdapter;
    private FolderAdapter folderAdapter;
    private ClassAdapter classAdapter;
    private ArrayList<FlashCard> flashCards;
    private ArrayList<Folder> folders;
    private ArrayList<Group> classes;
    private FlashCardDAO flashCardDAO;
    private FolderDAO folderDAO;
    private GroupDAO groupDAO;
    private String idUser;// ID người dùng

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo các biến cần thiết từ SharePreferences và DAO
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
        flashCardDAO = new FlashCardDAO(requireActivity());
        folderDAO = new FolderDAO(requireActivity());
        groupDAO = new GroupDAO(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo lại các tùy chọn người dùng và ID
        userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();

        // Thiết lập các recycler view và hiển thị dữ liệu ban đầu
        setupFlashCards();
        setupFolders();
        setupClasses();
        setupVisibility();
        setupSwipeRefreshLayout();
        setupSearchBar();
        setupCreateSetsButton();

        // Làm mới dữ liệu khi người dùng kéo xuống
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            binding.swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(requireActivity(), "Refreshed", Toast.LENGTH_SHORT).show();
        });
    }

    // Thiết lập flashcard, adapter và hiển thị dữ liệu trong recycler view
    @SuppressLint("NotifyDataSetChanged")
    private void setupFlashCards() {
        flashCards = flashCardDAO.getAllFlashCardByUserId(idUser);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false);
        binding.setsRv.setLayoutManager(linearLayoutManager);
        setsAdapter = new SetsAdapter(requireActivity(), flashCards, false);
        binding.setsRv.setAdapter(setsAdapter);
        setsAdapter.notifyDataSetChanged();
    }

    // Thiết lập folder, adapter và hiển thị dữ liệu trong recycler view
    @SuppressLint("NotifyDataSetChanged")
    private void setupFolders() {
        folders = folderDAO.getAllFolderByUserId(idUser);
        folderAdapter = new FolderAdapter(requireActivity(), folders);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false);
        binding.foldersRv.setLayoutManager(linearLayoutManager1);
        binding.foldersRv.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();
    }

    // Thiết lập lớp học, adapter và hiển thị dữ liệu trong recycler view
    @SuppressLint("NotifyDataSetChanged")
    private void setupClasses() {
        classes = groupDAO.getClassesOwnedByUser(idUser);
        classes.addAll(groupDAO.getClassesUserIsMemberOf(idUser));
        classAdapter = new ClassAdapter(requireActivity(), classes);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false);
        binding.classesRv.setLayoutManager(linearLayoutManager2);
        binding.classesRv.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();
    }

    // Cập nhật visibility của các layout dựa trên sự tồn tại của dữ liệu
    private void setupVisibility() {
        if (flashCards.isEmpty()) {
            binding.setsCl.setVisibility(View.GONE);
        } else {
            binding.setsCl.setVisibility(View.VISIBLE);
        }
        if (folders.isEmpty()) {
            binding.folderCl.setVisibility(View.GONE);
        } else {
            binding.folderCl.setVisibility(View.VISIBLE);
        }
        if (classes.isEmpty()) {
            binding.classCl.setVisibility(View.GONE);
        } else {
            binding.classCl.setVisibility(View.VISIBLE);
        }
    }

    // Thiết lập SwipeRefreshLayout để làm mới dữ liệu khi kéo xuống
    private void setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            binding.swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Thiết lập thanh tìm kiếm để mở màn hình tìm kiếm khi nhấn
    private void setupSearchBar() {
        binding.searchBar.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), ViewSearchActivity.class);
            startActivity(intent);
        });
    }

    // Thiết lập nút tạo bộ (sets) để mở CreateSetActivity khi nhấn
    private void setupCreateSetsButton() {
        binding.createSetsCl.setOnClickListener(v -> startActivity(new Intent(getActivity(), CreateSetActivity.class)));
    }

    // Làm mới tất cả các dữ liệu (flashcards, thư mục, lớp học) và cập nhật hiển thị
    private void refreshData() {
        refreshFlashCards();
        refreshFolders();
        refreshClasses();
        updateVisibility();
    }

    // Cập nhật flashcards và thông báo adapter dữ liệu đã thay đổi
    @SuppressLint("NotifyDataSetChanged")
    private void refreshFlashCards() {
        flashCards = flashCardDAO.getAllFlashCardByUserId(idUser);
        setsAdapter = new SetsAdapter(requireActivity(), flashCards, false);
        binding.setsRv.setAdapter(setsAdapter);
        setsAdapter.notifyDataSetChanged();
    }

    // Cập nhật thư mục và thông báo adapter dữ liệu đã thay đổi
    @SuppressLint("NotifyDataSetChanged")
    private void refreshFolders() {
        folders = folderDAO.getAllFolderByUserId(idUser);
        folderAdapter = new FolderAdapter(requireActivity(), folders);
        binding.foldersRv.setAdapter(folderAdapter);
        folderAdapter.notifyDataSetChanged();
    }

    // Cập nhật lớp học và thông báo adapter dữ liệu đã thay đổi
    @SuppressLint("NotifyDataSetChanged")
    private void refreshClasses() {
        classes = groupDAO.getClassesOwnedByUser(idUser);
        classes.addAll(groupDAO.getClassesUserIsMemberOf(idUser));
        classAdapter = new ClassAdapter(requireActivity(), classes);
        binding.classesRv.setAdapter(classAdapter);
        classAdapter.notifyDataSetChanged();
    }

    // Cập nhật visibility của các layout theo trạng thái dữ liệu
    private void updateVisibility() {
        if (flashCards.isEmpty()) {
            binding.setsCl.setVisibility(View.GONE);
        } else {
            binding.setsCl.setVisibility(View.VISIBLE);
        }
        if (folders.isEmpty()) {
            binding.folderCl.setVisibility(View.GONE);
        } else {
            binding.folderCl.setVisibility(View.VISIBLE);
        }
        if (classes.isEmpty()) {
            binding.classCl.setVisibility(View.GONE);
        } else {
            binding.classCl.setVisibility(View.VISIBLE);
        }

        // Hiển thị layout trống nếu không có dữ liệu nào
        if (flashCards.isEmpty() && folders.isEmpty() && classes.isEmpty()) {
            binding.emptyCl.setVisibility(View.VISIBLE);
        } else {
            binding.emptyCl.setVisibility(View.GONE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu mỗi khi fragment được hiển thị lại
        refreshData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Giải phóng binding khi fragment bị hủy
        binding = null;
    }
}
