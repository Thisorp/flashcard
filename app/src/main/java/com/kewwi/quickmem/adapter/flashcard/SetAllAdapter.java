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
import com.kewwi.quickmem.data.dao.UserDAO;
import com.kewwi.quickmem.data.model.FlashCard;
import com.kewwi.quickmem.data.model.User;
import com.kewwi.quickmem.databinding.ItemSetAllBinding;
import com.kewwi.quickmem.ui.activities.set.ViewSetActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//quản lý hiển thị các tập hợp thẻ trong recyclerview và xử lý sự kiện khi người  dùng tương tác

// SetAllAdapter: Là adapter quản lý và hiển thị các tập hợp thẻ trong RecyclerView.
public class SetAllAdapter extends RecyclerView.Adapter<SetAllAdapter.SetsViewHolder> {

    private final Context context;//sử dụng trong các thao tác chuyển đổi ngữ cảnh
    private final ArrayList<FlashCard> sets;//danh sách các tập hợp thẻ sẽ đc hiển thị

    //hàm khởi tạo adapter với context và danh sách tập hợp thẻ
    public SetAllAdapter(Context context, ArrayList<FlashCard> sets) {
        this.context = context;
        this.sets = sets;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public SetAllAdapter.SetsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        //liệt kê ItemSetAllBinding với viewholder
        ItemSetAllBinding binding = ItemSetAllBinding.inflate(inflater, parent, false);
        return new SetsViewHolder(binding.getRoot());
    }

    @SuppressLint("SetTextI18n")
    @Override
    //gán dữ liệu từ tập hợp thẻ vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull SetAllAdapter.SetsViewHolder holder, int position) {

        FlashCard set = sets.get(position);//lấy tập hợp thẻ tại vị trí tương ứng
        CardDAO cardDAO = new CardDAO(context);//lấy đối tượng CardDAO để đếm số lượng thẻ trong tập hợp
        int count = cardDAO.countCardByFlashCardId(set.getId());//đếm số lượng thẻ trong tập hợp
        UserDAO userDAO = new UserDAO(context);//lấy đối tượng UserDAO để lấy thông tin người dùng
        User user = userDAO.getUserById(set.getUser_id());//lấy thông tin người dùng

        //gán dữ liệu tên bộ thẻ, số lượng, thuật ngữ và tên người tạo vào textview
        holder.binding.setNameTv.setText(set.getName());
        holder.binding.termCountTv.setText(count + " terms");
        holder.binding.userNameTv.setText(user.getUsername());

        //gán ảnh đại diện của người tạo vào imageview
        Picasso.get().load(user.getAvatar()).into(holder.binding.avatarIv);

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
        private final ItemSetAllBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public SetsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemSetAllBinding.bind(itemView);//ràng buộc layout với item view
        }
    }

}
