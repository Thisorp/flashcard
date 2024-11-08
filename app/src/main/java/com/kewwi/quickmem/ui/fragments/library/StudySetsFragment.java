// Tên file: StudySetsFragment.java
// Chức năng chính: Fragment này hiển thị danh sách các bộ thẻ ghi nhớ (flashcard) của người dùng.
// Người dùng có thể tạo bộ thẻ mới và xem danh sách các bộ thẻ hiện có trong giao diện này.
package com.kewwi.quickmem.ui.fragments.library;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kewwi.quickmem.adapter.flashcard.SetCopyAdapter;
import com.kewwi.quickmem.data.dao.FlashCardDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.databinding.FragmentStudySetsBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.create.CreateSetActivity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class StudySetsFragment extends Fragment {
    // Biến binding để liên kết layout XML
    private FragmentStudySetsBinding binding;
    // Danh sách các flashcard và DAO để truy cập dữ liệu flashcard
    private ArrayList<FlashCard> flashCards;
    private FlashCardDAO flashCardDAO;
    // Adapter để hiển thị danh sách flashcard trong RecyclerView
    private SetCopyAdapter setsAdapter;
    // ID của người dùng hiện tại
    private String idUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo DAO để truy cập dữ liệu flashcard
        flashCardDAO = new FlashCardDAO(requireActivity());
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        binding = FragmentStudySetsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Thiết lập giao diện người dùng
        setupView();
    }

    private void setupView() {
        UserSharePreferences userSharePreferences = new UserSharePreferences(requireActivity());
        idUser = userSharePreferences.getId();
        // Thiết lập sự kiện cho nút tạo bộ flashcard mới
        binding.createSetBtn.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateSetActivity.class)));
        // Lấy danh sách flashcard từ cơ sở dữ liệu cho người dùng hiện tại
        flashCards = flashCardDAO.getAllFlashCardByUserId(idUser);
        updateVisibility();// Cập nhật giao diện dựa trên số lượng flashcard
        setupRecyclerView();// Thiết lập RecyclerView
    }

    private void updateVisibility() {
        // Hiển thị thông báo khi không có flashcard nào, nếu có flashcard thì hiển thị danh sách
        if (flashCards.isEmpty()) {
            binding.setsCl.setVisibility(View.VISIBLE);// Hiển thị thông báo không có bộ thẻ
            binding.setsRv.setVisibility(View.GONE);
        } else {
            binding.setsCl.setVisibility(View.GONE);
            binding.setsRv.setVisibility(View.VISIBLE);// Hiển thị danh sách bộ thẻ
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        // Khởi tạo LinearLayoutManager và gán adapter cho RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        binding.setsRv.setLayoutManager(linearLayoutManager);
        setsAdapter = new SetCopyAdapter(requireActivity(), flashCards);
        binding.setsRv.setAdapter(setsAdapter);
        setsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới dữ liệu khi người dùng quay lại fragment này
        refreshData();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshData() {
        // Xóa và cập nhật lại danh sách flashcard từ cơ sở dữ liệu, sau đó thông báo RecyclerView cập nhật
        flashCards.clear();
        flashCards.addAll(flashCardDAO.getAllFlashCardByUserId(idUser));
        setsAdapter.notifyDataSetChanged();
        updateVisibility();// Cập nhật giao diện dựa trên số lượng flashcard mới
    }
}