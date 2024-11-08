package com.kewwi.quickmem.adapter.flashcard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.data.dao.UserDAO
import com.kewwi.quickmem.data.model.FlashCard
import com.kewwi.quickmem.databinding.ItemSetFolderBinding
import com.squareup.picasso.Picasso

//hiển thị danh sách các thẻ liên quan đén 1 lớp học và..
//..quản lý việc thêm/xóa các bộ thẻ vào/ra lớp học

// SetClassAdapter: Là adapter quản lý và hiển thị các bộ thẻ trong RecyclerView.
class SetClassAdapter(
    private val flashCardList: ArrayList<FlashCard>,//danh sách các bộ thẻ
    private val classId: String//id của lớp học
) : RecyclerView.Adapter<SetClassAdapter.SetClassViewHolder>() {
    //quản lý các view của item trong recyclerview ( quản lý view cho từng bộ thẻ trong danh sách
    class SetClassViewHolder(
        val binding: ItemSetFolderBinding//liên kết layout đại diện cho mỗi bộ thẻ
    ) : RecyclerView.ViewHolder(//gắn view cho item trong recyclerview
        binding.root
    )

    //tạo viewholder cho mỗi item trong recyclerview(tạo viewholder cho mỗi bộ thẻ khi recyclerview cần)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetClassViewHolder {
        //liên kết layout với viewholder
        val layoutInflater =
            LayoutInflater.from(parent.context)//lấy layout inflater từ context của parent
        val binding = ItemSetFolderBinding.inflate(
            layoutInflater,
            parent,
            false
        )//gắn view cho item trong recyclerview
        return SetClassViewHolder(binding)//trả về viewholder đã được tạo
    }

    //trả về số lượng bộ thẻ trong danh sách
    override fun getItemCount(): Int {
        return flashCardList.size
    }

    @SuppressLint("SetTextI18n")
    //gán dữ liệu từ bộ thẻ vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: SetClassViewHolder, position: Int) {
        val flashCard = flashCardList[position]//lấy bộ thẻ tại vị trí tương ứng
        val cardDAO =
            CardDAO(holder.itemView.context)//lấy đối tượng CardDAO để đếm số lượng thẻ trong tập hợp
        val userDAO =
            UserDAO(holder.itemView.context)//lấy đối tượng UserDAO để lấy thông tin người dùng
        val user = userDAO.getUserById(flashCard.user_id)//lấy thông tin người dùng

        //gán dữ liệu người tạo bộ thẻ và số lượng thuật ngữ vào textview
        holder.binding.setNameTv.text = flashCard.name
        holder.binding.termCountTv.text =
            cardDAO.countCardByFlashCardId(flashCard.id).toString() + " terms"

        //gán ảnh đại diện của người tạo vào imageview và gán tên người tạo vào textview
        Picasso.get().load(user.avatar).into(holder.binding.avatarIv)
        holder.binding.userNameTv.text = user.username//gán tên người tạo vào textview

        val groupDAO = GroupDAO(holder.itemView.context)//truy cập dữ liệu lớp học

        //kiểm tra xem bộ thẻ có nằm trong lớp học hay không
        //nếu có thì đổi màu nền của item để đánh dấu
        if (groupDAO.isFlashCardInClass(classId, flashCard.id)) {
            holder.binding.setFolderItem.background =
                AppCompatResources.getDrawable(
                    //nền sau khi chọn
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_select
                )
        } else {
            //nếu không, sẽ ở trạng thái chưa chọn
            holder.binding.setFolderItem.background =
                AppCompatResources.getDrawable(
                    //nền khi chưa chọn
                    holder.itemView.context, com.kewwi.quickmem.R.drawable.background_unselect
                )
        }

        //khi người dùng nhấp vào item, kiểm tra trạng thái, thêm/xóa bộ thẻ vào/ra lớp học
        holder.binding.setFolderItem.setOnClickListener {
            if (groupDAO.isFlashCardInClass(classId, flashCard.id)) {
                //nếu đã có trong lớp học, xóa khỏi lớp học và đổi nền thành chưa chọn
                groupDAO.removeFlashCardFromClass(classId, flashCard.id)
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        holder.itemView.context,
                        com.kewwi.quickmem.R.drawable.background_unselect
                    )
            } else {
                //nếu chưa có trong lớp học, thêm vào lớp học và đổi nền thành đã chọn
                groupDAO.addFlashCardToClass(classId, flashCard.id)
                holder.binding.setFolderItem.background =
                    AppCompatResources.getDrawable(
                        holder.itemView.context,
                        com.kewwi.quickmem.R.drawable.background_select
                    )
            }
        }
    }
}