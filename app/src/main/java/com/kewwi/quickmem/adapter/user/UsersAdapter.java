package com.kewwi.quickmem.adapter.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.R;
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ItemUsersAdminBinding;
import com.squareup.picasso.Picasso;

import java.util.List;
//hiển thị danh sách người dùng

//UsersAdapter: Adapter quản lý hiển thị danh sách người dùng.
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    private final Context context;//ngữ cảnh hiện tại
    private final List<User> users;//danh sách người dùng
    private final UserDAO userDAO;//lớp DAO để truy cập dữ liệu người dùng

    //hàm khởi tạo adapter
    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        this.userDAO = new UserDAO(context);
    }

    //tạo viewholder cho mỗi item trong recyclerview
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //lấy layout inflater từ context
        LayoutInflater inflater = LayoutInflater.from(context);
        //liên kết layout item_users_admin.xml với View.
        ItemUsersAdminBinding binding = ItemUsersAdminBinding.inflate(inflater, parent, false);
        return new UsersViewHolder(binding.getRoot());
    }

    //gán dữ liệu từ người dùng vào viewholder cho từng vị trí trong danh sách
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);//lấy người dùng tại vị trí tương ứng
        //tải avatar
        Picasso.get().load(user.getAvatar()).placeholder(R.drawable.ic_user).into(holder.binding.avatarIv);
        //lấy vai trò của người dùng
        int role = user.getRole();
        //nếu vai trò khác 0 => ko phải admin
        if (role != 0) {
            //bảo đảm người dùng hiển thị
            holder.binding.getRoot().setVisibility(View.VISIBLE);
            //Đặt màu nền dựa trên trạng thái của người dùng:
            // trạng thái 0 (bị chặn) là xám, còn lại là màu trắng xám
            holder.binding.userCl.setBackgroundResource(user.getStatus() == 0 ? R.color.gray : R.color.white_gray);
            //cập nhật văn bản nút chặn  tùy  theo trạng thái
            holder.binding.blockTv.setText(user.getStatus() == 0 ? "Blocked" : "Block");
            //cập nhật trạng thái checkbox dựa trên trạng thái người dùng
            holder.binding.userCb.setChecked(user.getStatus() == 0);
            //cập nhật văn bản tên người dùng( cho phép chữ gạch ngang khi người  dùng bị chặn)
            int paintFlags = holder.binding.emailTv.getPaintFlags();
            paintFlags = user.getStatus() == 0 ? (paintFlags | Paint.STRIKE_THRU_TEXT_FLAG) : (paintFlags & (~Paint.STRIKE_THRU_TEXT_FLAG));
            //áp dụng định dạng chữ ngang cho textview liên quan đến người bị  chặn
            holder.binding.emailTv.setPaintFlags(paintFlags);
            holder.binding.userNameTv.setPaintFlags(paintFlags);
            holder.binding.roleTv.setPaintFlags(paintFlags);

            //đổi trạng thái người dùng khi checkbox dc chọn hoặc  bỏ
            int finalPaintFlags = paintFlags;//lưu trạng thái ban đầu

            holder.binding.userCb.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                //cập nhật  trạng thái người dùng trong cơ sở dữ liệu: 0 (bị chặn) hoặc 1 (không bị chặn)
                userDAO.updateStatusUser(user.getId(), isChecked ? 0 : 1);

                //cập nhật trạng thái màu nền và văn bản của checkbox theo trạng thái mới
                holder.binding.userCl.setBackgroundResource(isChecked ? R.color.gray : R.color.white_gray);
                holder.binding.blockTv.setText(isChecked ? "Blocked" : "Block");

                //áp dụng định dạng chữ ngang cho textview dựa trên trạng thái checkbox
                int flags = isChecked ? (finalPaintFlags | Paint.STRIKE_THRU_TEXT_FLAG) : (finalPaintFlags & (~Paint.STRIKE_THRU_TEXT_FLAG));
                holder.binding.emailTv.setPaintFlags(flags);
                holder.binding.userNameTv.setPaintFlags(flags);
                holder.binding.roleTv.setPaintFlags(flags);
            });

            //hiển thị thông tin người dùng
            holder.binding.userNameTv.setText(user.getUsername());
            holder.binding.emailTv.setText("Email: " + user.getEmail());
            holder.binding.roleTv.setText("Role: " + (role == 1 ? "Teacher" : "Student"));//vai trò của người dùng:"Teacher" nếu 1, "Student" nếu khác
        } else {
            //ẩn mục người dùng nếu vai trò là 0 (admin)  (role = 0)
            holder.binding.getRoot().setVisibility(View.GONE);
        }
    }

    //trả về số lượng người có vai trò khác 0
    @Override
    public int getItemCount() {
        int count = 0;
        //đếm số lượng người dùng có vai trò khác 0
        for (User user : users) {
            if (user.getRole() != 0) {
                count++;
            }
        }
        return count;
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        private final ItemUsersAdminBinding binding;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUsersAdminBinding.bind(itemView);
        }
    }
}