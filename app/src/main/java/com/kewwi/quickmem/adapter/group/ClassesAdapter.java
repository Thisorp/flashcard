package com.kewwi.quickmem.adapter.group;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.dao.GroupDAO;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.Group;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ItemClassesAdminBinding;
import com.squareup.picasso.Picasso;


import java.util.List;
//dùng để hiển thị danh sách các lớp học trong RecyclerView.
// Adapter này lấy dữ liệu từ danh sách các Group và hiển thị thông tin như tên lớp, số thành viên, số bộ flashcards, và thông tin người dùng (admin) quản lý lớp.

//ClassesAdapter: Adapter quản lý hiển thị các lớp học trong RecyclerView.
public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder> {
    private final Context context;//context để truy cập tài nguyên
    private final List<Group> classes;//danh sách các lớp học

    //hàm khởi tạo adapter với context và danh sách lớp học
    public ClassesAdapter(Context context, List<Group> classes) {
        this.context = context;
        this.classes = classes;
    }

    //tạo viewholder cho mỗi item trong recyclerview
    @NonNull
    @Override
    public ClassesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //lấy layout inflater từ context
        LayoutInflater inflater = LayoutInflater.from(context);
        //liên kết layout item_classes_admin.xml với View.
        ItemClassesAdminBinding binding = ItemClassesAdminBinding.inflate(inflater, parent, false);
        //trả về viewholder đã gắn layout
        return new ClassesViewHolder(binding.getRoot());
    }

    //gán dữ liệu từ lớp học vào viewholder cho từng vị trí trong danh sách
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ClassesViewHolder holder, int position) {
        Group group = classes.get(position);//lấy lớp học tại vị trí tương ứng

        holder.binding.classNameTv.setText("Class: " + group.getName());//đặt tên lớp học vào textview
        GroupDAO groupDAO = new GroupDAO(context);//lấy thông tin người dùng từ SharePreferences
        int numberMember = groupDAO.getNumberMemberInClass(group.getId()) + 1;//lấy số lượng thành viên và thẻ trong lớp học
        int numberSet = groupDAO.getNumberFlashCardInClass(group.getId());//lấy số lượng thẻ trong lớp học

        //đặt số lượng thành viên và thẻ vào textview
        holder.binding.numberUserTv.setText(numberMember + " members");
        holder.binding.numberSetTv.setText(numberSet + " sets");

        UserDAO userDAO = new UserDAO(context);//lấy thông tin người dùng từ SharePreferences
        User user = userDAO.getUserById(group.getUser_id());//lấy thông tin người dùng từ SharePreferences

        //in thông tin user_id ra log
        Log.d("User_id", group.getUser_id());
        //nếu thông tin user không null, hiển thị thông tin user
        if (user != null) {
            //đặt tên người dùng vào textview
            holder.binding.userNameTv.setText(user.getUsername());
            //in thông tin username ra log
            Log.d("Username", user.getUsername());
            Picasso.get().load(user.getAvatar()).into(holder.binding.avatarIv);
        }
    }

    @Override
    //trả về số lượng lớp học trong danh sách
    public int getItemCount() {
        return classes.size();
    }

    //quản lý các view của từng item trong recyclerview
    public static class ClassesViewHolder extends RecyclerView.ViewHolder {
        private final ItemClassesAdminBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public ClassesViewHolder(@NonNull View itemView) {
            super(itemView);//gắn view vào viewholder
            //ràng buộc layout với item view
            binding = ItemClassesAdminBinding.bind(itemView);
        }
    }
}
