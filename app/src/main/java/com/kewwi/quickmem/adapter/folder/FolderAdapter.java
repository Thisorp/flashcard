package com.kewwi.quickmem.adapter.folder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.model.Folder;
import com.kewwi.quickmem.databinding.ItemFolderBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.folder.ViewFolderActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//quản lý hiển thị các folder trong recyclerview
//FolderAdapter: Adapter quản lý hiển thị các folder trong RecyclerView.
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {
    private final Context context;//context để truy cập tài nguyên
    private final ArrayList<Folder> folders;//danh sách các folder

    //hàm khởi tạo adapter với context và danh sách folder
    public FolderAdapter(Context context, ArrayList<Folder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public FolderAdapter.FolderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);//lấy layout inflater từ context
        ItemFolderBinding binding = ItemFolderBinding.inflate(inflater, parent, false);//liên kết layout với viewholder
        return new FolderViewHolder(binding.getRoot());//trả về viewholder đã gắn layout
    }

    @Override
    //gán dữ liệu từ folder vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull FolderAdapter.FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);//lấy folder tại vị trí tương ứng
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy thông tin người dùng từ SharePreferences
        String avatar = userSharePreferences.getAvatar();//lấy avatar
        String username = userSharePreferences.getUserName();//lấy tên người dùng

        //gán dữ liệu vào textview
        holder.binding.folderNameTv.setText(folder.getName());
        Picasso.get().load(avatar).into(holder.binding.avatarIv);//đặt avatar vào imageview
        holder.binding.userNameTv.setText(username);//đặt tên người dùng vào textview

        //khi nhấp vào folder, chuyển sang màn hình xem nội dung folder
        holder.binding.folderCl.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewFolderActivity.class);//mở màn hình xem nội dung folder
            intent.putExtra("id", folder.getId());//gửi id folder
            context.startActivity(intent);//chuyển sang màn hình xem nội dung folder
        });
    }

    @Override
    //trả về số lượng folder trong danh sách
    public int getItemCount() {
        return folders.size();
    }

    //quản lý các view của từng item trong recyclerview
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        private final ItemFolderBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public FolderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);//gắn view vào viewholder
            binding = ItemFolderBinding.bind(itemView);//ràng buộc layout với item view
        }
    }
}
