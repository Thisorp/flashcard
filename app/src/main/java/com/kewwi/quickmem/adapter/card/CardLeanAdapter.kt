package com.kewwi.quickmem.adapter.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ItemLearnSetBinding


//quả lý hiển thị danh sách thẻ trong chế độ học tập
class CardLeanAdapter(
    private var cardList: List<Card> //danh sách card dc truyền vào adaper
) : RecyclerView.Adapter<CardLeanAdapter.ViewHolder>() {

    //flippedStates để lưu trạng tháicaur mỗi thẻ ( đã lật hoặc chưa lật)
    private var flippedStates = MutableList(cardList.size) { false }

    //hàm tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //layoutInflater để tạo view từ xml layout item_learn_set
        val layoutInflater = LayoutInflater.from(parent.context)

        //gắn layout vào viewholder
        val binding = ItemLearnSetBinding.inflate(layoutInflater, parent, false)

        //trả về viewholder đã gắn layout
        return ViewHolder(binding)
    }

    //gắn dữ liệu vào viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //lấy thẻ tại vị trí hiện tại
        val card = cardList[position]

        //gán dữ liệu vào view
        holder.bind(card)
    }

    //trả về số lượng thẻ trong danh sách
    override fun getItemCount(): Int {
        return cardList.size
    }

    //cập nhật danh sách các thẻ
    fun setCards(cards: List<Card>) {
        this.cardList = cards

        //cập nhật lại trạng thái của thẻ
        flippedStates = MutableList(cards.size) { false }
    }

    //trả về danh sahcs các thẻ hiện tại
    fun getCards(): List<Card> {
        return cardList
    }

    //trả về tổng số thẻ trong danh sách
    fun getCount(): Int {
        return cardList.size
    }

    //quả lý các view của tuwngf item trong recyclerview
    inner class ViewHolder(private val binding: ItemLearnSetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //gán dữ liệu cảu 1 thẻ vào view
        fun bind(card: Card) {
            //gán văn bản cảu thẻ vào textview
            binding.backTv.text = card.front
            binding.frontTv.text = card.back

            //thiết lập kiểu lật của thẻ( flip từ phải sang trái)
            binding.cardViewFlip.setFlipTypeFromRight()

            //thiết lập thời gian lật của thẻ(500 milliseconds)
            binding.cardViewFlip.setFlipDuration(500)

            //thiết lập chế độ lật của thẻ(lật từ ngang sang dọc)
            binding.cardViewFlip.setToHorizontalType()

            //thiết lập sự kiện khi thẻ được nhấp vào
            binding.cardViewFlip.setOnClickListener {
                binding.cardViewFlip.flipTheView()//gọi hàm lật thẻ
            }
        }
    }
}