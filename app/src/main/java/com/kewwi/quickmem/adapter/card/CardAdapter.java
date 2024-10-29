package com.kewwi.quickmem.adapter.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.model.Card;
import com.kewwi.quickmem.databinding.ItemCardAddBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//class CardAdapter để quản lý danh sách card trong recyclerview
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    //context đại diện cho ngữ canhr của activity hoặc fragment
    private final Context context;

    //danh sách card
    private final ArrayList<Card> cards;

    //hàm khởi tạo adapter với context và danh sách card
    public CardAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @NonNull
    @NotNull
    @Override

    public CardAdapter.CardViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        //dùng layouotinflater để chuyển layout từ xml thành view
        LayoutInflater inflater = LayoutInflater.from(context);

        // dùng viewbinding ràng buộc layout item_card_add với code
        ItemCardAddBinding binding = ItemCardAddBinding.inflate(inflater, parent, false);

        //trả về viewholder chứa layout đã ràng buộc
        return new CardViewHolder(binding.getRoot());
    }

    @Override
    //gán dữ liệu từ đối tượng card vào view của viewholder
    public void onBindViewHolder(@NonNull @NotNull CardAdapter.CardViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //lấy đối tượng card tại vị trí tương ứng
        Card card = cards.get(position);

        //xóa các textwatcher cũ để tránh lỗi khi tái sử dụng view
        holder.removeTextWatchers();

        //kiểm tra nếu vị trí > 1 thì yêu cầu focus vào edittext của thẻ trước
        if (position > 1) {
            holder.binding.termEt.requestFocus();
        }

        //đặt dữ liệu từ đối tượng card vào edittext
        holder.binding.termEt.setText(card.getFront());
        holder.binding.definitionEt.setText(card.getBack());


        //tạo textwatcher cho phần mặt trước của thẻ
        TextWatcher frontWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //cập nhật giá trị mặt trc của thẻ trc khi văn bản thay đổi
                card.setFront(s.toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //cập nhật mặt trc của thẻ khi văn bản đang thay đổi
                card.setFront(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //cập nhật mặt trc của thẻ sau khi văn bản thay đổi
                card.setFront(s.toString().trim());
            }
        };

        //tạo textwatcher cho phần mặt sau
        TextWatcher backWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //cập nhật mặt sau trc khi thay đổi văn bản
                card.setBack(s.toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //cập nhật mặt sau khi văn bản đang thay đổi
                card.setBack(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //cập nhật mặt sau sau khi văn bản thay đổi
                card.setBack(s.toString().trim());
            }
        };

        //gán các textwatcher cho edittext của thẻ
        holder.setTextWatchers(frontWatcher, backWatcher);

    }

    @Override
    //trả về tổng số thẻ trong danh sách
    public int getItemCount() {
        return cards.size();
    }

    //lưu trữ view cảu từng item trong recyclerview
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        private final ItemCardAddBinding binding;//dùng viewbinding để truy cập các view trong layout item_card_add
        private TextWatcher frontWatcher;//các textwatcher để lưu trữ các thay đổi văn bản trong edittexxt
        private TextWatcher backWatcher;//các textwatcher để lưu trữ các thay đổi văn bản trong edittexxt

        //hàm khởi tạo viewholder với view được truyền vào, dùng ietmview để ràng buộc các view
        public CardViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            this.binding = ItemCardAddBinding.bind(itemView);
        }

        //hàm xóa các textwatcher cũ để tránh nhiều textwatcher trùng lặp
        public void removeTextWatchers() {
            if (frontWatcher != null) {
                binding.termEt.removeTextChangedListener(frontWatcher);
            }
            if (backWatcher != null) {
                binding.definitionEt.removeTextChangedListener(backWatcher);
            }
        }

        //gán mới các textwatcher cho edittexxt
        public void setTextWatchers(TextWatcher frontWatcher, TextWatcher backWatcher) {
            this.frontWatcher = frontWatcher;
            this.backWatcher = backWatcher;

            //gán các textwatcher để lưu trữ thay đổi văn bản
            binding.termEt.addTextChangedListener(frontWatcher);
            binding.definitionEt.addTextChangedListener(backWatcher);
        }
    }
}
