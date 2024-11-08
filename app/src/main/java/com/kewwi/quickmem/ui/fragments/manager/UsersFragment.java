// Tên file: UsersFragment.java
// Chức năng chính: Fragment này hiển thị danh sách người dùng (ngoại trừ các tài khoản quản trị có role = 0) và cho phép tìm kiếm, lọc, cũng như đăng xuất tài khoản.
// Cấu trúc bao gồm thiết lập RecyclerView để hiển thị người dùng, thêm chức năng tìm kiếm và thao tác đăng xuất với một cảnh báo xác nhận.
package com.kewwi.quickmem.ui.fragments.manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.adapter.user.UsersAdapter;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.FragmentUsersBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.auth.signin.SignInActivity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class UsersFragment extends Fragment {
    // Sử dụng binding để liên kết layout XML với Fragment
    private FragmentUsersBinding binding;
    // DAO để truy cập dữ liệu người dùng
    private UserDAO userDAO;
    // Adapter để hiển thị danh sách người dùng
    private UsersAdapter usersAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo đối tượng DAO để truy cập dữ liệu người dùng
        userDAO = new UserDAO(requireActivity());
        // Cho phép tùy chọn menu
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Liên kết layout XML và trả về view gốc
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        assert activity != null;
        activity.setSupportActionBar(binding.toolbar);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Lấy danh sách người dùng (loại trừ tài khoản quản trị) và hiển thị trong RecyclerView
        List<User> listUsers = userDAO.getAllUser().stream().filter(user -> user.getRole() != 0).collect(Collectors.toList());
        binding.usersRv.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));
        usersAdapter = new UsersAdapter(requireActivity(), listUsers);
        binding.usersRv.setAdapter(usersAdapter);
        usersAdapter.notifyDataSetChanged();


    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Khởi tạo menu và thanh tìm kiếm người dùng
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_setting_admin, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("Search users");
        // Cài đặt chức năng tìm kiếm người dùng theo tên khi nhập văn bản
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                List<User> listUsers = userDAO.getAllUser().stream().filter(user -> user.getRole() != 0).collect(Collectors.toList());
                List<User> listUsersFilter = listUsers.stream().filter(user -> user.getUsername().toLowerCase().contains(newText.toLowerCase())).collect(Collectors.toList());
                usersAdapter = new UsersAdapter(requireActivity(), listUsersFilter);
                binding.usersRv.setAdapter(usersAdapter);
                usersAdapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý sự kiện chọn mục từ menu
        if (item.getItemId() == R.id.log_out) {
            // Hiển thị cảnh báo xác nhận trước khi đăng xuất
            new AlertDialog.Builder(requireContext())
                    .setTitle("Sign out")
                    .setMessage("Are you sure?")
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        new UserSharePreferences(requireContext()).clear();
                        startActivity(new Intent(getActivity(), SignInActivity.class));
                        UserSharePreferences userSharePreferences = new UserSharePreferences(requireActivity());
                        userSharePreferences.clear();
                        requireActivity().finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        return true;
    }
}