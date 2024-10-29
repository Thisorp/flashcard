package com.kewwi.quickmem.adapter.card

import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ItemViewTermsBinding
import kotlin.collections.ArrayList

//quản lý hiển thị và tương tác với temrs trong tập hợp thẻ

// ViewTermsAdapter: Là adapter quản lý và hiển thị các thẻ (Card) trong RecyclerView.
// Nó cung cấp chức năng chuyển văn bản thành giọng nói (Text-to-Speech) cho các thuật ngữ.
class ViewTermsAdapter(
    private val cards: ArrayList<Card>// danh sách các thẻ
) : RecyclerView.Adapter<ViewTermsAdapter.ViewHolder>() {
    private var textToSpeech: TextToSpeech? =
        null//đổi tượng text-to-speech để chuyển văn bản thành giọng nói

    //quả lý các view của item trong recyclerview
    class ViewHolder(val binding: ItemViewTermsBinding) : RecyclerView.ViewHolder(binding.root)

    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        //liên lết layout với viewholder
        val binding = ItemViewTermsBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    //gán dữ liệu vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = cards[position]//.lấy thẻ tại vị trí tương ứng

        val status = card.status//lấy trạng thái của thẻ
        // thay đổi màu của thẻ dựa trên trạng thái
        if (status == 1) {
            holder.binding.cardView.setCardBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_green_light))
        } else if (status == 2) {
            holder.binding.cardView.setCardBackgroundColor(holder.itemView.context.getColor(android.R.color.holo_orange_light))
        } else {
            holder.binding.cardView.setCardBackgroundColor(holder.itemView.context.getColor(android.R.color.white))
        }

        //gán dữ liệu vào textview
        holder.binding.termsTv.text = card.front
        holder.binding.definitionTv.text = card.back

        //khi nhấp vào nút text-to-speech, đọc văn bản của thuật ngữ
        holder.binding.soundIv.setOnClickListener {
            if (holder.binding.termsTv.text.toString().isNotEmpty()) {
                //cài đặt ngôn ngữ cho text-to-speech
                textToSpeech = TextToSpeech(holder.itemView.context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val result = textToSpeech?.setLanguage(textToSpeech?.voice?.locale)

                        //kiểm tra ngôn ngữ hỗ trợ và đọc văn bản
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Toast.makeText(
                                holder.itemView.context,
                                "Language not supported",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            //cấu hình âm lượng và đọc văn bản
                            textToSpeech?.speak(
                                holder.binding.termsTv.text.toString(),
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                null
                            )
                        }
                    } else {
                        Toast.makeText(
                            holder.itemView.context,
                            "Initialization failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    //trả về số lượng thẻ trong danh sách
    override fun getItemCount(): Int {

        return cards.size
    }

    //khi viewholder bị huỷ, dừng và hủy text-to-speech
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (textToSpeech != null) {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }
}