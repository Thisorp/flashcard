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
import com.kewwi.quickmem.databinding.ItemFolderCopyBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.folder.ViewFolderActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
//quản lý hiển thị các folder trong recyclerview
//Adapter này sử dụng một layout khác là ItemFolderCopyBinding để hiển thị thông tin thư mục.

//FolderAdapter: Adapter quản lý hiển thị các folder trong RecyclerView.
public class FolderCopyAdapter extends RecyclerView.Adapter<FolderCopyAdapter.FolderViewHolder> {
    private final Context context;//context để truy cập tài nguyên
    private final ArrayList<Folder> folders;//danh sách các folder

    //hàm khởi tạo adapter với context và danh sách folder
    public FolderCopyAdapter(Context context, ArrayList<Folder> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public FolderCopyAdapter.FolderViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);//lấy layout inflater từ context
        ItemFolderCopyBinding binding = ItemFolderCopyBinding.inflate(inflater, parent, false);//liên kết các thành phần UI trong layout item_folder_copy.xml.
        return new FolderViewHolder(binding.getRoot());//trả về viewholder đã gắn layout
    }

    @Override
    //gán dữ liệu từ folder vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull FolderCopyAdapter.FolderViewHolder holder, int position) {
        Folder folder = folders.get(position);//lấy folder tại vị trí tương ứng
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy thông tin người dùng từ SharePreferences
        String avatar = userSharePreferences.getAvatar();//lấy avatar
        String username = userSharePreferences.getUserName();//lấy tên người dùng

        //gán dữ liệu vào textview
        holder.binding.folderNameTv.setText(folder.getName());//đặt tên thư mục vào textview
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
    public class FolderViewHolder extends RecyclerView.ViewHolder {
        private final ItemFolderCopyBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public FolderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);//gắn view vào viewholder
            binding = ItemFolderCopyBinding.bind(itemView);//ràng buộc layout với item view
        }
    }
}
