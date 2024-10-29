package com.kewwi.quickmem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.R
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ItemSelectDefineBinding

//hiển thị danh sách các thẻ (cards) định nghĩa trong RecyclerView

//DefineListAdapter: Adapter quản lý hiển thị danh sách các thẻ định nghĩa.
class DefineListAdapter(
    private val cardList: List<Card>,//danh sách các thẻ (cards)
) : RecyclerView.Adapter<DefineListAdapter.DefineViewHolder>() {
    // ViewHolder cho mỗi mục trong danh sách
    class DefineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Sử dụng View Binding để truy cập các thành phần giao diện trong item_select_define.xml
        val binding = ItemSelectDefineBinding.bind(itemView)
    }

    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefineViewHolder {
        // Sử dụng LayoutInflater để inflate layout item_select_define.xml
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_define, parent, false)
        return DefineViewHolder(view)//trả về viewholder mới được tạo
    }

    //trả về số lượng thẻ (cards)
    override fun getItemCount(): Int {
        return cardList.size
    }

    //gán dữ liệu từ thẻ (cards) vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: DefineViewHolder, position: Int) {
        val card = cardList[position]//lấy thẻ (cards) tại vị trí tương ứng
        holder.binding.wordTv.text = card.front//Gán dữ liệu (mặt trước của thẻ) vào TextView wordTv

    }
}