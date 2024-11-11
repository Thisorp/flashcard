package com.kewwi.quickmem.adapter.flashcard

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.dao.FolderDAO
import com.kewwi.quickmem.data.dao.UserDAO
import com.kewwi.quickmem.data.model.FlashCard
import com.kewwi.quickmem.databinding.ItemSetFolderBinding
import com.kewwi.quickmem.ui.activities.set.ViewSetActivity
import com.squareup.picasso.Picasso

//hiển thị danh sách các FlashCard
// và quản lý việc chọn hoặc bỏ chọn FlashCard trong một thư mục.

// SetFolderViewAdapter: Là adapter quản lý và hiển thị các bộ thẻ trong RecyclerView.
class SetFolderViewAdapter(
    private val flashcardList: ArrayList<FlashCard>,//danh sách các bộ thẻ
    private val isSelect: Boolean = false,//kiểm tra xem danh sách có được chọn hay không
    private val folderId: String = ""//id của thư mục
) : RecyclerView.Adapter<SetFolderViewAdapter.SetFolderViewHolder>() {


    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetFolderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSetFolderBinding.inflate(layoutInflater, parent, false)
        return SetFolderViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")//để bỏ qua lỗi warning
    //gán dữ liệu từ bộ thẻ vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: SetFolderViewHolder, position: Int) {
        val flashcard = flashcardList[position]//lấy bộ thẻ tại vị trí tương ứng
        val userDAO =
            UserDAO(holder.itemView.context)//lấy đối tượng UserDAO để lấy thông tin người dùng
        val user = userDAO.getUserById(flashcard.user_id)//lấy thông tin người dùng
        val cardDAO =
            CardDAO(holder.itemView.context)//lấy đối tượng CardDAO để đếm số lượng thẻ trong tập hợp
        val count = cardDAO.countCardByFlashCardId(flashcard.id)//đếm số thẻ trong tập hợp
        val folderDAO =
            FolderDAO(holder.itemView.context)//lấy đối tượng FolderDAO để kiểm tra xem bộ thẻ có trong thư mục hay không

        //gán dữ liệu người tạo bộ thẻ và số lượng thuật ngữ vào textview
        Picasso.get().load(user.avatar)
            .into(holder.binding.avatarIv)//dùng picasso để tải ảnh người dùng và gán vào imageview
        holder.binding.userNameTv.text = user.username//hiển thị tên người tạo
        holder.binding.setNameTv.text = flashcard.name//hiển thị tên bộ thẻ
        holder.binding.termCountTv.text = "$count terms"//hiển thị số lượng thuật ngữ

        //nếu chết độ chọn thư mục bật, kiểm tra xem bộ thẻ có nằm trong thư mục hay không
        if (isSelect) {

            if (folderDAO.isFlashCardInFolder(folderId, flashcard.id)) {
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        //màu nền khi bộ thẻ thuộc thư mục
                        holder.itemView.context, com.kewwi.quickmem.R.drawable.background_select
                    )
            } else {
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        //màu nên khi không thuộc thư mục
                        holder.itemView.context, com.kewwi.quickmem.R.drawable.background_unselect
                    )
            }


        }

        //thiết lập sự kiện khi người dùng nhấp vào item
        holder.binding.setFolderItem.setOnClickListener {
            //nếu chế độ chọn thư mục bật, kiểm tra xem bộ thẻ có nằm trong thư mục hay không và thay đổi trạng thái khi nhập dữ liệu
            if (isSelect) {
                //nếu đã có trong thư mục, xóa khỏi thư mục và đổi nền thành chưa chọn
                if (folderDAO.isFlashCardInFolder(folderId, flashcard.id)) {
                    folderDAO.removeFlashCardFromFolder(folderId, flashcard.id)
                    holder.binding.setFolderItem.background =
                        AppCompatResources.getDrawable(
                            //đổi màu khi xóa bộ thẻ khỏi thư mục
                            holder.itemView.context,
                            com.kewwi.quickmem.R.drawable.background_unselect
                        )
                } else {
                    //nếu chưa có trong thư mục, thêm vào thư mục và đổi nền thành đã chọn
                    folderDAO.addFlashCardToFolder(folderId, flashcard.id)//thêm bộ thẻ vào thư mục
                    holder.binding.setFolderItem.background =
                        AppCompatResources.getDrawable(
                            //đổi màu khi thêm bộ thẻ vào thư mục
                            holder.itemView.context, com.kewwi.quickmem.R.drawable.background_select
                        )
                }
            } else {
                //nếu chế độ chọn thư mục tắt, chuyển sang màn hình chi tiết bộ thẻ
                Intent(holder.itemView.context, ViewSetActivity::class.java).also {
                    it.putExtra("id", flashcard.id)//gắn id của bộ thẻ vào intent
                    holder.itemView.context.startActivity(it)//chuyển sang màn hình chi tiết bộ thẻ
                }
            }

        }
    }

    //trả về số lượng bộ thẻ trong danh sách
    override fun getItemCount(): Int {
        return flashcardList.size
    }

    //quản lý các view của item trong recyclerview
    class SetFolderViewHolder(val binding: ItemSetFolderBinding) :
        RecyclerView.ViewHolder(binding.root)
}