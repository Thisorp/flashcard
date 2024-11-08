package com.kewwi.quickmem.adapter.folder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.dao.FolderDAO
import com.kewwi.quickmem.data.model.Folder
import com.kewwi.quickmem.databinding.ItemFolderSelectBinding

//quản lý hiển thị các folder trong recyclerview
//cho phép người dùng thêm hoặc xóa thẻ vào thư mục trong danh sách

//FolderAdapter: Adapter quản lý hiển thị các folder trong RecyclerView
class FolderSelectAdapter(
    // danh sách các folder
    private val folderList: ArrayList<Folder>,
    private val flashcardId: String
) : RecyclerView.Adapter<FolderSelectAdapter.FolderSelectViewHolder>() {
    //quản lý các view của từng item trong recyclerview
    class FolderSelectViewHolder(
        val binding: ItemFolderSelectBinding//liên kết layout với viewholder
    ) : RecyclerView.ViewHolder(binding.root)

    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderSelectViewHolder {
        //lấy layout inflater từ context
        val layoutInflater = LayoutInflater.from(parent.context)

        //liên kết layout với viewholder
        val binding = ItemFolderSelectBinding.inflate(layoutInflater, parent, false)

        //trả về viewholder đã gắn layout
        return FolderSelectViewHolder(binding)
    }

    //gán dữ liệu từ folder vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: FolderSelectViewHolder, position: Int) {
        val folder = folderList[position]//lấy folder tại vị trí tương ứng
        val folderDAO =
            FolderDAO(holder.itemView.context)//lấy thông tin người dùng từ SharePreferences
        holder.binding.folderNameTv.text = folder.name//đặt tên thư mục vào textview
        updateBackground(holder, folder, folderDAO)//cập nhật màu nền của thư mục

        //khi nhấp vào folder, thêm hoặc xóa thẻ vào thư mục
        holder.binding.folderCv.setOnClickListener {
            //kiểm tra xem thẻ có tồn tại trong thư mục hay không
            if (folderDAO.isFlashCardInFolder(folder.id, flashcardId)) {
                //nếu tồn tại, xóa thẻ khỏi thư mục
                folderDAO.removeFlashCardFromFolder(folder.id, flashcardId)
                //hiển thị thông báo
                Toast.makeText(
                    holder.itemView.context,
                    "Removed from ${folder.name}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                //nếu không tồn tại, thêm thẻ vào thư mục
                folderDAO.addFlashCardToFolder(folder.id, flashcardId)
                //hiển thị thông báo
                Toast.makeText(
                    holder.itemView.context,
                    "Added to ${folder.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //cập nhật màu nền của thư mục
            updateBackground(holder, folder, folderDAO)
        }
    }

    //cập nhật màu nền của thư mục
    private fun updateBackground(
        holder: FolderSelectViewHolder,
        folder: Folder,
        folderDAO: FolderDAO
    ) {
        //kiểm tra xem thẻ có tồn tại trong thư mục hay không
        if (folderDAO.isFlashCardInFolder(folder.id, flashcardId)) {
            //nếu tồn tại, đặt màu nền là màu đã chọn
            holder.binding.folderCv.background =
                AppCompatResources.getDrawable(
                    //lấy context từ viewholder
                    // đặt màu nền là màu đã chọn
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_select
                )
        } else {
            //nếu không tồn tại, đặt màu nền là màu chưa chọn
            holder.binding.folderCv.background =
                AppCompatResources.getDrawable(
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_unselect
                )
        }
    }

    //trả về số lượng folder trong danh sách
    override fun getItemCount(): Int {
        return folderList.size
    }
}