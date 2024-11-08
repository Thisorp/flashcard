package com.kewwi.quickmem.ui.activities.classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kewwi.quickmem.ui.activities.set.AddFlashCardToClassActivity;
import com.kewwi.quickmem.ui.activities.group.AddMemberActivity;
import com.kewwi.quickmem.ui.activities.group.EditClassActivity;
import com.kewwi.quickmem.R;
import com.kewwi.quickmem.adapter.group.MyViewClassAdapter;
import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.databinding.ActivityViewClassBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.google.android.material.tabs.TabLayout;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;
import com.saadahmedsoft.popupdialog.PopupDialog;
import com.saadahmedsoft.popupdialog.Styles;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//File này quản lý Activity để hiển thị thông tin của một lớp học (Class)
// và cung cấp các chức năng liên quan đến quản lý lớp,
// như thêm thành viên, thêm flashcards, chỉnh sửa, xóa và rời lớp.


public class ViewClassActivity extends AppCompatActivity {
    private ActivityViewClassBinding binding;// Binding layout XML
    private GroupDAO groupDAO;// DAO để quản lý lớp học (Group)
    int currentTabPosition = 0;// Vị trí tab hiện tại trong ViewPager
    private String id;// ID của lớp học
    private UserSharePreferences userSharePreference;// Lưu thông tin người dùng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewClassBinding.inflate(getLayoutInflater());
        final View view = binding.getRoot();
        setContentView(view);// Thiết lập view cho Activity này

        setSupportActionBar(binding.toolbar);// Thiết lập toolbar

        // Quay lại khi bấm nút điều hướng
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Thiết lập adapter cho ViewPager để quản lý các tab
        MyViewClassAdapter myViewClassAdapter = new MyViewClassAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(myViewClassAdapter);

// Thiết lập TabLayout với ViewPager
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        // Lắng nghe sự kiện khi chuyển tab
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Lưu vị trí tab hiện tại
                currentTabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Xử lý làm mới dữ liệu khi người dùng kéo xuống
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            binding.swipeRefreshLayout.setRefreshing(false);// Tắt icon refresh
            myViewClassAdapter.notifyDataSetChanged();// Cập nhật dữ liệu trong ViewPager
        });

    }

    @SuppressLint("SetTextI18n")
    private void setUpData() {
        id = getIntent().getStringExtra("id");// Lấy ID lớp học từ Intent
        groupDAO = new GroupDAO(this);// Khởi tạo DAO để thao tác với lớp học
        Group group = groupDAO.getGroupById(id);// Lấy thông tin lớp học từ ID
        binding.classNameTv.setText(group.getName());// Hiển thị tên lớp
        binding.termCountTv.setText(groupDAO.getNumberFlashCardInClass(id) + " sets");// Hiển thị số lượng bộ thẻ trong lớp
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu cho Activity
        getMenuInflater().inflate(R.menu.menu_view_set, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Xử lý các hành động trong menu
        if (item.getItemId() == R.id.menu) {
            new BottomSheetMenuDialogFragment.Builder(this)
                    .setSheet(R.menu.menu_view_class)// Inflate menu từ file XML
                    .setTitle("Class")// Tiêu đề cho BottomSheet
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @Nullable Object o) {

                        }

                        @Override
                        public void onSheetItemSelected(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @NotNull MenuItem menuItem, @Nullable Object o) {
                            // Xử lý từng hành động dựa trên ID của menuItem được chọn
                            if (menuItem.getItemId() == R.id.add_member) {
                                // Thêm thành viên vào lớp
                                if (isOwner()) {
                                    //Nếu người dùng là chủ lớp, cho phép thêm thành viên
                                    holderAddMember();
                                } else {
                                    // Hiển thị popup lỗi nếu không phải chủ lớp
                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("Error!")
                                            .setDescription("You are not the owner of this class!")
                                            .setPositiveButtonText("OK")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            } else if (menuItem.getItemId() == R.id.add_sets) {
                                // Thêm flashcard vào lớp
                                if (isOwner()) {
                                    // Nếu là chủ lớp, cho phép thêm flashcard
                                    handleAddSets();
                                } else {

                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("Error!")
                                            .setDescription("You are not the owner of this class!")
                                            .setPositiveButtonText("OK")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                }

                            } else if (menuItem.getItemId() == R.id.edit_class) {
                                // Chỉnh sửa lớp
                                if (isOwner()) {
                                    handleEditClass();// Nếu là chủ lớp, cho phép chỉnh sửa lớp
                                } else {
                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("Error!")
                                            .setDescription("You are not the owner of this class!")
                                            .setPositiveButtonText("OK")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                }

                            } else if (menuItem.getItemId() == R.id.delete_class) {
                                //xóa lớp
                                if (isOwner()) {
                                    handleDeleteClass();
                                } else {
                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("Error!")
                                            .setDescription("You are not the owner of this class!")
                                            .setPositiveButtonText("OK")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                }

                            } else if (menuItem.getItemId() == R.id.leave_class) {
                                // rời khỏi lớp
                                if (!isOwner()) {
                                    // Nếu không phải chủ lớp, cho phép rời khỏi lớp
                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.STANDARD)
                                            .setHeading("Are you sure?")
                                            .setDescription("You will loss access to this class!")
                                            .setPositiveButtonText("Yes")
                                            .setPopupDialogIcon(R.drawable.baseline_logout_24)
                                            .setNegativeButtonText("Cancel")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onPositiveClicked(Dialog dialog) {
                                                    super.onPositiveClicked(dialog);
                                                    UserDAO userDAO = new UserDAO(ViewClassActivity.this);
                                                    if (userDAO.removeUserFromClass(userSharePreference.getId(), userSharePreference.getClassId()) > 0L) {
                                                        PopupDialog.getInstance(ViewClassActivity.this)
                                                                .setStyle(Styles.SUCCESS)
                                                                .setHeading("Leave!")
                                                                .setDescription("Your class has been leave!.")
                                                                .setDismissButtonText("OK")
                                                                .setCancelable(true)
                                                                .showDialog(new OnDialogButtonClickListener() {
                                                                    @Override
                                                                    public void onDismissClicked(Dialog dialog) {
                                                                        super.onDismissClicked(dialog);
                                                                        dialog.dismiss();
                                                                        finish();
                                                                    }
                                                                });
                                                    } else {

                                                        PopupDialog.getInstance(ViewClassActivity.this)
                                                                .setStyle(Styles.FAILED)
                                                                .setHeading("Error!")
                                                                .setDescription("Something went wrong!")
                                                                .setPositiveButtonText("OK")
                                                                .setCancelable(true)
                                                                .showDialog(new OnDialogButtonClickListener() {
                                                                    @Override
                                                                    public void onDismissClicked(Dialog dialog) {
                                                                        super.onDismissClicked(dialog);
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                    }
                                                }

                                                @Override
                                                public void onNegativeClicked(Dialog dialog) {
                                                    super.onNegativeClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                } else {

                                    PopupDialog.getInstance(ViewClassActivity.this)
                                            .setStyle(Styles.FAILED)
                                            .setHeading("Error!")
                                            .setDescription("You are the owner of this class!")
                                            .setPositiveButtonText("OK")
                                            .setCancelable(true)
                                            .showDialog(new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            }
                        }

                        @Override
                        public void onSheetDismissed(@NotNull BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, @Nullable Object o, int i) {

                        }
                    })
                    .setCloseTitle(getString(R.string.close))// Tiêu đề cho nút đóng BottomSheet
                    .setAutoExpand(true)// Tự động mở rộng BottomSheet
                    .setCancelable(true)// Cho phép hủy BottomSheet
                    .show(getSupportFragmentManager());
        }
        return super.onOptionsItemSelected(item);
    }

    // Hàm thêm flashcard vào lớp
    private void handleAddSets() {
        Intent intent = new Intent(this, AddFlashCardToClassActivity.class);
        intent.putExtra("flashcard_id", id);// Gửi ID của lớp qua Activity thêm flashcard
        startActivity(intent);// Bắt đầu Activity
        finish();// Kết thúc Activity hiện tại
    }

    // Hàm chỉnh sửa lớp học
    private void handleEditClass() {
        Intent intent = new Intent(this, EditClassActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    // Hàm xóa lớp học
    private void handleDeleteClass() {
        PopupDialog.getInstance(this)
                .setStyle(Styles.STANDARD)
                .setHeading("Are you sure?")
                .setDescription("You will not be able to recover this class!")
                .setPositiveButtonText("Yes")
                .setPopupDialogIcon(R.drawable.ic_delete)
                .setNegativeButtonText("Cancel")
                .setCancelable(true)
                .showDialog(new OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveClicked(Dialog dialog) {
                        super.onPositiveClicked(dialog);
                        if (groupDAO.deleteClass(id) > 0L) {
                            PopupDialog.getInstance(ViewClassActivity.this)
                                    .setStyle(Styles.SUCCESS)
                                    .setHeading("Deleted!")
                                    .setDescription("Your class has been deleted.")
                                    .setDismissButtonText("OK")
                                    .setCancelable(true)
                                    .showDialog(new OnDialogButtonClickListener() {
                                        @Override
                                        public void onDismissClicked(Dialog dialog) {
                                            super.onDismissClicked(dialog);
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                        } else {
                            PopupDialog.getInstance(ViewClassActivity.this)
                                    .setStyle(Styles.FAILED)
                                    .setHeading("Error!")
                                    .setDescription("Something went wrong!")
                                    .setPositiveButtonText("OK")
                                    .setCancelable(true)
                                    .showDialog(new OnDialogButtonClickListener() {
                                        @Override
                                        public void onDismissClicked(Dialog dialog) {
                                            super.onDismissClicked(dialog);
                                            dialog.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onNegativeClicked(Dialog dialog) {
                        super.onNegativeClicked(dialog);
                        dialog.dismiss();
                    }
                });
    }

    // Hàm thêm thành viên vào lớp
    private void holderAddMember() {
        Intent intent = new Intent(this, AddMemberActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
        finish();
    }

    // Kiểm tra xem người dùng có phải chủ lớp hay không
    private boolean isOwner() {
        userSharePreference = new UserSharePreferences(this);
        String currentUserId = userSharePreference.getId();
        String ownerId = groupDAO.getGroupById(id).getUser_id();
        return currentUserId.equals(ownerId);
    }

    @Override
    protected void onResume() {
        // Khi Activity được khởi động lại (resume), dữ liệu sẽ được tải
        // và hiển thị lại thông qua hàm setUpData()
        super.onResume();
        setUpData();
    }
}