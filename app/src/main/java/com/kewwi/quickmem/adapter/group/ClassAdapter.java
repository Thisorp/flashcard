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
import com.kewwi.quickmem.databinding.ItemClassBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.classes.ViewClassActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
//dùng để hiển thị danh sách các lớp học trong RecyclerView.
// Mỗi lớp đại diện cho một nhóm người dùng và các thẻ  liên quan.

//ClassAdapter: Adapter quản lý hiển thị các lớp học trong RecyclerView.
public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private final Context context;//context để truy cập tài nguyên
    private final ArrayList<Group> classes;//danh sách các lớp học

    //hàm khởi tạo adapter với context và danh sách lớp học
    public ClassAdapter(Context context, ArrayList<Group> classes) {
        this.context = context;
        this.classes = classes;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public ClassAdapter.ClassViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //lấy layout inflater từ context
        LayoutInflater inflater = LayoutInflater.from(context);
        //liên kết layout item_class.xml với View.
        ItemClassBinding binding = ItemClassBinding.inflate(inflater, parent, false);
        //trả về viewholder đã gắn layout
        return new ClassViewHolder(binding.getRoot());
    }

    @SuppressLint("SetTextI18n")//xóa thông báo lỗi
    @Override
    //gán dữ liệu từ lớp học vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull ClassAdapter.ClassViewHolder holder, int position) {
        Group group = classes.get(position);//lấy lớp học tại vị trí tương ứng
        holder.binding.classNameTv.setText(group.getName());//đặt tên lớp học vào textview
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy thông tin người dùng từ SharePreferences

        //kiểm tra xem người dùng có phải là quản trị viên của lớp học hay không
        if (Objects.equals(group.getUser_id(), userSharePreferences.getId())) {
            //nếu là quản trị viên, hiển thị thông báo
            holder.binding.isAdminTv.setVisibility(View.VISIBLE);
        }
        //lấy số lượng thành viên và thẻ trong lớp học
        GroupDAO groupDAO = new GroupDAO(context);

        //lấy số lượng thành viên và thẻ trong lớp học
        int numberMember = groupDAO.getNumberMemberInClass(group.getId()) + 1;

        //lấy số lượng thẻ trong lớp học
        int numberSet = groupDAO.getNumberFlashCardInClass(group.getId());

        //đặt số lượng thành viên và thẻ vào textview
        holder.binding.numberUserTv.setText(numberMember + " members");

        //đặt số lượng thẻ vào textview
        holder.binding.numberSetTv.setText(numberSet + " sets");

        //khi nhấp vào lớp học, chuyển sang màn hình xem nội dung lớp học
        holder.itemView.setOnClickListener(v -> {
            userSharePreferences.setClassId(group.getId());//lưu id lớp học vào SharePreferences

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
        private final ItemClassBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public ClassViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);//gắn view vào viewholder
            binding = ItemClassBinding.bind(itemView);//ràng buộc layout với item view
        }
    }
}
