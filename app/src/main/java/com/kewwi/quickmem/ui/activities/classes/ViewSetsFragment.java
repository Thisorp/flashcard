package com.kewwi.quickmem.ui.activities.classes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kewwi.quickmem.ui.activities.set.AddFlashCardToClassActivity;
import com.kewwi.quickmem.adapter.flashcard.SetCopyAdapter;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.databinding.FragmentViewSetsBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class ViewSetsFragment extends Fragment {
    private FragmentViewSetsBinding binding;// Biến liên kết với giao diện của Fragment
    private UserSharePreferences userSharePreferences;// Lưu trữ thông tin người dùng
    private final ArrayList<FlashCard> flashCards = new ArrayList<>(); // Danh sách thẻ
    private FlashCardDAO flashCardDAO;// Quản lý dữ liệu thẻ
    private GroupDAO groupDAO; // Quản lý dữ liệu nhóm/lớp học

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo các DAO để tương tác với cơ sở dữ liệu
        flashCardDAO = new FlashCardDAO(requireActivity());
        groupDAO = new GroupDAO(requireActivity());
        userSharePreferences = new UserSharePreferences(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Khởi tạo view từ FragmentViewSetsBinding và trả về view gốc
        binding = FragmentViewSetsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userSharePreferences = new UserSharePreferences(requireActivity());
        // Lắng nghe sự kiện khi nút "Thêm bộ thẻ ghi nhớ" được nhấn
        binding.addSetsBtn.setOnClickListener(view1 -> {

        });
        // Gọi các phương thức thiết lập hiển thị và lấy dữ liệu
        fetchFlashCards();
        setupVisibility();
        setupRecyclerView();

        // Mở màn hình thêm thẻ ghi nhớ mới vào lớp khi nút thêm được nhấn
        binding.addSetsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddFlashCardToClassActivity.class);
            intent.putExtra("flashcard_id", userSharePreferences.getClassId());
            startActivity(intent);
        });
    }

    // Phương thức lấy các thẻ có trong lớp
    private void fetchFlashCards() {
        ArrayList<String> listId = groupDAO.getAllFlashCardInClass(userSharePreferences.getClassId());
        for (String id : listId) {
            flashCards.add(flashCardDAO.getFlashCardById(id));// Lấy từng thẻ ghi nhớ theo ID
        }
    }

    // Phương thức thiết lập hiển thị dựa trên danh sách thẻ
    private void setupVisibility() {
        if (flashCards.isEmpty()) {
            // Nếu không có thẻ ghi nhớ nào
            binding.setsLl.setVisibility(View.VISIBLE);// Hiển thị giao diện "không có bộ thẻ"
            binding.setsRv.setVisibility(View.GONE);// Ẩn RecyclerView
        } else {
            // Nếu có thẻ ghi nhớ
            binding.setsLl.setVisibility(View.GONE);// Ẩn giao diện "không có bộ thẻ"
            binding.setsRv.setVisibility(View.VISIBLE);// Hiển thị RecyclerView
        }
    }

    // Phương thức thiết lập RecyclerView để hiển thị danh sách thẻ
    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        SetCopyAdapter setsAdapter = new SetCopyAdapter(requireActivity(), flashCards);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.setsRv.setLayoutManager(linearLayoutManager);// Sắp xếp danh sách theo chiều dọc
        binding.setsRv.setAdapter(setsAdapter);// Gắn Adapter cho RecyclerView
        binding.setsRv.setHasFixedSize(true);// Cải thiện hiệu suất khi danh sách có kích thước cố định
        setsAdapter.notifyDataSetChanged();// Thông báo cho Adapter rằng dữ liệu đã thay đổi
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại giao diện khi Fragment được hiển thị trở lại
        setupRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Cập nhật lại giao diện khi Fragment bị tạm dừng
        setupRecyclerView();
    }
}