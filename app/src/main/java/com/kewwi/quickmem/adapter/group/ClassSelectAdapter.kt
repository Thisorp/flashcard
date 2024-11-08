package com.kewwi.quickmem.adapter.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.data.model.Group
import com.kewwi.quickmem.databinding.ItemSelectClassBinding

//dùng để quản lý danh sách các lớp học và kiểm tra xem flashcard có nằm trong lớp học hay không.
// Khi người dùng chọn hoặc bỏ chọn một lớp học, flashcard sẽ được thêm hoặc xóa khỏi lớp đó.


//ClassAdapter: Adapter quản lý hiển thị các lớp học trong RecyclerView.
class ClassSelectAdapter(
    private val classList: ArrayList<Group>,//danh sách các lớp học
    private val flashCardId: String//id của flashcard
) : RecyclerView.Adapter<ClassSelectAdapter.ClassSelectViewHolder>() {
    //quản lý các view của từng item trong recyclerview
    class ClassSelectViewHolder(
        val binding: ItemSelectClassBinding//liên kết layout với viewholder
    ) : RecyclerView.ViewHolder(binding.root)

    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassSelectViewHolder {
        //lấy layout inflater từ context
        val layoutInflater = LayoutInflater.from(parent.context)
        //liên kết layout item_select_class.xml với View.
        val binding = ItemSelectClassBinding.inflate(layoutInflater, parent, false)
        //trả về viewholder đã gắn layout
        return ClassSelectViewHolder(binding)
    }

    //gán dữ liệu từ lớp học vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: ClassSelectViewHolder, position: Int) {
        val group = classList[position]//lấy lớp học tại vị trí tương ứng
        val groupDAO =
            GroupDAO(holder.itemView.context)//lấy thông tin người dùng từ SharePreferences
        holder.binding.classNameTv.text = group.name//đặt tên lớp học vào textview
        updateBackground(
            holder,
            group,
            groupDAO
        )//cập nhật background dựa trên flashcard có nằm trong lớp học hay không

        //khi người dùng chọn hoặc bỏ chọn một lớp học, flashcard sẽ được thêm hoặc xóa khỏi lớp đó.
        holder.binding.cardView.setOnClickListener {
            //kiểm tra xem flashcard có nằm trong lớp học hay không
            if (groupDAO.isFlashCardInClass(group.id, flashCardId)) {
                //nếu nằm trong lớp học, xóa flashcard khỏi lớp đó
                groupDAO.removeFlashCardFromClass(group.id, flashCardId)
            } else {
                //nếu không nằm trong lớp học, thêm flashcard vào lớp đó
                groupDAO.addFlashCardToClass(group.id, flashCardId)
            }
            //cập nhật background dựa trên flashcard có nằm trong lớp học hay không
            updateBackground(holder, group, groupDAO)
        }


    }

    //trả về số lượng lớp học trong danh sách
    override fun getItemCount(): Int {
        return classList.size
    }

    //cập nhật background dựa trên flashcard có nằm trong lớp học hay không
    private fun updateBackground(holder: ClassSelectViewHolder, group: Group, groupDAO: GroupDAO) {
        //kiểm tra xem flashcard có nằm trong lớp học hay không
        if (groupDAO.isFlashCardInClass(group.id, flashCardId)) {
            //nếu nằm trong lớp học, đặt background là background_select
            holder.binding.cardView.background =
                AppCompatResources.getDrawable(
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_select
                )
        } else {
            //nếu không nằm trong lớp học, đặt background là background_unselect
            holder.binding.cardView.background =
                AppCompatResources.getDrawable(
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_unselect
                )
        }
    }
}