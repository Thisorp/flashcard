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
import com.kewwi.quickmem.databinding.ItemSetBinding;
import com.kewwi.quickmem.preferen.UserSharePreferences;
import com.kewwi.quickmem.ui.activities.set.ViewSetActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
//hiển thị danh sách các FlashCard (bộ thẻ)
// với tùy chọn hiển thị theo kiểu thư viện (library) hoặc bình thường.

// SetsAdapter: Là adapter quản lý và hiển thị các bộ thẻ trong RecyclerView.
public class SetsAdapter extends RecyclerView.Adapter<SetsAdapter.SetsViewHolder> {

    private final Context context;//truy cập tài nguyên
    private final ArrayList<FlashCard> sets;//danh sách các bộ thẻ
    private final Boolean isLibrary;//kiểm tra chế độ hiển thị thư viện

    //hàm khởi tạo adapter với context, danh sách bộ thẻ và chế độ hiển thị
    public SetsAdapter(Context context, ArrayList<FlashCard> sets, Boolean isLibrary) {
        this.context = context;
        this.sets = sets;
        this.isLibrary = isLibrary;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public SetsAdapter.SetsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);//lấy layout inflater từ context của parent
        ItemSetBinding binding = ItemSetBinding.inflate(inflater, parent, false);//gắn view cho item trong recyclerview
        return new SetsViewHolder(binding.getRoot());//trả về viewholder đã được tạo
    }

    @SuppressLint("SetTextI18n")//để bỏ qua lỗi warning
    @Override
    //gán dữ liệu từ bộ thẻ vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull SetsAdapter.SetsViewHolder holder, int position) {
        if (isLibrary) {
            //hiển thị chế độ thư viện
            ViewGroup.LayoutParams params = holder.binding.setCv.getLayoutParams();//lấy layout params của item
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;//đặt chiều rộng bằng MATCH_PARENT

        }
        FlashCard set = sets.get(position);//lấy bộ thẻ tại vị trí tương ứng
        UserSharePreferences userSharePreferences = new UserSharePreferences(context);//lấy đối tượng UserSharePreferences để lấy thông tin người dùng
        CardDAO cardDAO = new CardDAO(context);//lấy đối tượng CardDAO để đếm số lượng thẻ trong tập hợp
        int count = cardDAO.countCardByFlashCardId(set.getId());//đếm số lượng thẻ trong tập hợp
        String avatar = userSharePreferences.getAvatar();//lấy ảnh người dùng
        String userNames = userSharePreferences.getUserName();//lấy thông tin người dùng

        //gán dữ liệu tên bộ thẻ, số lượng, thuật ngữ và tên người tạo vào textview
        holder.binding.setNameTv.setText(set.getName());//hiển thị tên bộ thẻ
        holder.binding.termCountTv.setText(count + " terms");//hiển thị số lượng thuật ngữ
        holder.binding.userNameTv.setText(userNames);//hiển thị tên người tạo
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
    public static class SetsViewHolder extends RecyclerView.ViewHolder {
        private final ItemSetBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public SetsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemSetBinding.bind(itemView);//ràng buộc layout với item view
        }
    }

}
