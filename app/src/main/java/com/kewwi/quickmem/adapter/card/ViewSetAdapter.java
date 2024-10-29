package com.kewwi.quickmem.adapter.card;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kewwi.quickmem.data.model.Card;
import com.kewwi.quickmem.databinding.ItemViewSetBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

//quản lý hiển thị các  thẻ trong recyclerview, cung cấp chức năng text-to-speech
public class ViewSetAdapter extends RecyclerView.Adapter<ViewSetAdapter.ViewSetViewHolder> {
    private final Context context;//sử dụng trong  các thao tác text-to-speech
    private final ArrayList<Card> cards;//danh sách các thẻ sẽ đc hiển thị
    private TextToSpeech textToSpeech;//đối tượng text-to-speech để đọc nội dung thẻ

    //hàm khởi tạo adapter với context và danh sách thẻ
    public ViewSetAdapter(Context context, ArrayList<Card> cards) {
        this.context = context;
        this.cards = cards;
    }

    @NonNull
    @NotNull
    @Override
    //tạo viewholder cho mỗi item trong recyclerview
    public ViewSetAdapter.ViewSetViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        //liệt kê ItemViewSetBinding với viewholder
        ItemViewSetBinding binding = ItemViewSetBinding.inflate(inflater, parent, false);
        return new ViewSetViewHolder(binding.getRoot());
    }

    @Override
    //gán dữ liệu từ thẻ vào viewholder cho từng vị trí trong danh sách
    public void onBindViewHolder(@NonNull @NotNull ViewSetAdapter.ViewSetViewHolder holder, int position) {
        Card card = cards.get(position);//lấy thẻ tại vị trí tương ứng

        //gán dữ liệu vào textview
        holder.binding.backTv.setText(card.getFront());
        holder.binding.frontTv.setText(card.getBack());

        //thiết lập thời gian lật của thẻ
        holder.binding.cardViewFlip.setFlipDuration(450);

        //bật chức năng lật
        holder.binding.cardViewFlip.setFlipEnabled(true);

        //khi nhấp thẻ, lật thẻ và dùng text-to-speech nếu đang nói
        holder.binding.cardViewFlip.setOnClickListener(v -> {

            holder.binding.cardViewFlip.flipTheView();//gọi hàm lật thẻ
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();//dừng và tắt text-to-speech
            }

        });

        //khi nhấp vào nút text-to-speech, đọc nội dung thẻ
        holder.binding.soundIv.setOnClickListener(v -> {
            if (!holder.binding.backTv.getText().toString().isEmpty()) {
                textToSpeech = new TextToSpeech(context, status -> {
                    if (status == TextToSpeech.SUCCESS) {
                        //cài đặt ngôn ngữ cho text-to-speech
                        int result = textToSpeech.setLanguage(textToSpeech.getVoice().getLocale());
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(context, "Language not supported", Toast.LENGTH_SHORT).show();
                        } else {
                            //cấu hình âm lượng và đọc văn bản
                            Bundle params = new Bundle();
                            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);
                            textToSpeech.speak(holder.binding.backTv.getText().toString(), TextToSpeech.QUEUE_FLUSH, params, "UniqueID");
                        }
                    } else {
                        Toast.makeText(context, "Initialization failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    //trả về số lượng thẻ trong danh sách
    public int getItemCount() {
        return cards.size();
    }

    //quản lý các view của từng item trong recyclerview
    public static class ViewSetViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewSetBinding binding;//liên kết layout với viewholder

        //hàm khởi tạo nhận vào view của item và ràng buộc layout
        public ViewSetViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding = ItemViewSetBinding.bind(itemView);//ràng buộc layout với item view
        }
    }

    @Override
    //khi viewholder bị huỷ, dừng và hủy text-to-speech
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();//dừng và tắt text-to-speech
        }
    }
}