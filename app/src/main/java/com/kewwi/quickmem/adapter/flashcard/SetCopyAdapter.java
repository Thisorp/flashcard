package com.kewwi.quickmem.adapter.flashcard;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.dao.CardDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.databinding.ItemSetCopyBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.set.ViewSetActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//quản lý hiển thị các tập hợp thẻ trong recyclerview và xử lý sự kiện khi người dùng tương tác
//hiển thị danh sách các FlashCard và quản lý việc sao chép chúng hoặc truy cập chi tiết bộ FlashCard.


// SetCopyAdapter: Là adapter quản lý và hiển thị các tập hợp thẻ trong RecyclerView.
public class SetCopyAdapter extends RecyclerView.Adapter<SetCopyAdapter.SetViewHolder> {
    private final Context context;//sử dụng trong các thao tác chuyển đổi ngữ cảnh(để truy cập tài nguyên)
    private final ArrayList<FlashCard> sets;//danh sách các tập hợp thẻ sẽ đc hiển thị
    CardDAO cardDAO;//để thao tác dữ liệu các thẻ

    //hàm khởi tạo adapter với context và danh sách tập hợp thẻ
    public SetCopyAdapter(Context context, ArrayList<FlashCard> sets) {
        this.context = context;
        this.sets = sets;
    }

    @NonNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public SetCopyAdapter.SetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);//lấy layout inflater từ context của parent
        ItemSetCopyBinding binding = ItemSetCopyBinding.inflate(inflater, parent, false);//gắn view cho item trong recyclerview
        return new SetViewHolder(binding.getRoot());//trả về viewholder đã được tạo
    }

    //gán dữ liệu từ tập hợp thẻ vào viewholder cho từng vị trí trong danh sách
    @SuppressLint("SetTextI18n")//để bỏ qua lỗi warning
    @Override
    public void onBindViewHolder(@NonNull SetViewHolder holder, int position) {

        FlashCard set = sets.get(position);//lấy tập hợp thẻ tại vị trí tương ứng
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy đối tượng UserSharePreferences để lấy thông tin người dùng
        cardDAO = new CardDAO(context);//lấy đối tượng CardDAO để đếm số lượng thẻ trong tập hợp
        int count = cardDAO.countCardByFlashCardId(set.getId());//đếm số lượng thẻ trong tập hợp
        String avatar = userSharePreferences.getAvatar();//lấy ảnh người dùng
        String userNames = userSharePreferences.getUserName();//lấy thông tin người dùng

        //gán dữ liệu tên bộ thẻ, số lượng, thuật ngữ và tên người tạo vào textview
        holder.binding.setNameTv.setText(set.getName());
        holder.binding.termCountTv.setText(count + " terms");
        holder.binding.userNameTv.setText(userNames);
        Picasso.get().load(avatar).into(holder.binding.avatarIv);//dùng picasso để tải ảnh người dùng và gán vào imageview
        holder.binding.createdDateTv.setText(set.getCreated_at());//gán ngày tạo vào textview

        //khi người dùng nhấp vào item, chuyển sang ViewActyviti (Xem chi tiết tập hợp thẻ)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewSetActivity.class);//tạo intent để chuyển sang ViewSetActivity
            intent.putExtra("id", set.getId());//gắn id của tập hợp thẻ vào intent

            context.startActivity(intent);//chuyển sang ViewSetActivity
        });

    }

    @Override
    //trả về số lượng các bộ thẻ trong danh sách
    public int getItemCount() {
        return sets.size();
    }

    //quản lý các view của từng item trong recyclerview
    public static class SetViewHolder extends RecyclerView.ViewHolder {
        private final ItemSetCopyBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public SetViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemSetCopyBinding.bind(itemView);//ràng buộc layout với item view
        }
    }
}