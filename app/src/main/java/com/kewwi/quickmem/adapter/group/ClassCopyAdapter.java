package com.kewwi.quickmem.adapter.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.databinding.ItemClassCopyBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.classes.ViewClassActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
//dùng để hiển thị danh sách các lớp học trong RecyclerView.

//ClassAdapter: Adapter quản lý hiển thị các lớp học trong RecyclerView.
public class ClassCopyAdapter extends RecyclerView.Adapter<ClassCopyAdapter.ClassViewHolder> {
    private final Context context;//context để truy cập tài nguyên
    private final ArrayList<Group> classes;//danh sách các lớp học

    //hàm khởi tạo adapter với context và danh sách lớp học
    public ClassCopyAdapter(Context context, ArrayList<Group> classes) {
        this.context = context;
        this.classes = classes;
    }

    //tạo viewholder cho mỗi item trong recyclerview
    @NonNull
    @NotNull
    @Override
    public ClassCopyAdapter.ClassViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //lấy layout inflater từ context
        LayoutInflater inflater = LayoutInflater.from(context);
        //liên kết layout item_class_copy.xml với View.
        ItemClassCopyBinding binding = ItemClassCopyBinding.inflate(inflater, parent, false);
        //trả về viewholder đã gắn layout
        return new ClassViewHolder(binding.getRoot());
    }

    //gán dữ liệu từ lớp học vào viewholder cho từng vị trí trong danh sách
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull @NotNull ClassCopyAdapter.ClassViewHolder holder, int position) {
        Group group = classes.get(position);//lấy lớp học tại vị trí tương ứng
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy thông tin người dùng từ SharePreferences

        //kiểm tra xem người dùng có phải là quản trị viên của lớp học hay không
        if (Objects.equals(group.getUser_id(), userSharePreferences.getId())) {
            //nếu là quản trị viên, hiển thị textview"admin"
            holder.binding.isAdminTv.setVisibility(View.VISIBLE);
        }
        //đặt tên lớp học vào textview
        holder.binding.classNameTv.setText(group.getName());//đặt tên lớp học vào textview
        GroupDAO groupDAO = new GroupDAO(context);//lấy thông tin người dùng từ SharePreferences
        int numberMember = groupDAO.getNumberMemberInClass(group.getId()) + 1;//lấy số lượng thành viên và thẻ trong lớp học
        int numberSet = groupDAO.getNumberFlashCardInClass(group.getId());//lấy số lượng thẻ trong lớp học

        //đặt số lượng thành viên và thẻ vào textview
        holder.binding.numberUserTv.setText(numberMember + " members");//
        holder.binding.numberSetTv.setText(numberSet + " sets");

        //khi nhấp vào lớp học, chuyển sang màn hình xem nội dung lớp học
        holder.itemView.setOnClickListener(v -> {
            //lưu id lớp học vào SharePreferences
            userSharePreferences.setClassId(group.getId());

            //mở màn hình xem nội dung lớp học
            Intent intent = new Intent(context, ViewClassActivity.class);
            //gửi id lớp học
            intent.putExtra("id", group.getId());
            //chuyển sang màn hình xem nội dung lớp học
            context.startActivity(intent);
        });
    }

    //trả về số lượng lớp học trong danh sách
    @Override
    public int getItemCount() {
        return classes.size();
    }

    //quản lý các view của từng item trong recyclerview
    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        ItemClassCopyBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public ClassViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);//gắn view vào viewholder
            //ràng buộc layout với item view
            binding = ItemClassCopyBinding.bind(itemView);
        }
    }
}
