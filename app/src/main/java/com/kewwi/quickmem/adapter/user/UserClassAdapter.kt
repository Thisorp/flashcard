package com.kewwi.quickmem.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.data.dao.UserDAO
import com.kewwi.quickmem.data.model.User
import com.kewwi.quickmem.databinding.ItemUserBinding
import com.squareup.picasso.Picasso

//được sử dụng để hiển thị danh sách người dùng trong một lớp học.
// Adapter này quản lý các item người dùng và cho phép thêm/xóa người dùng khỏi lớp học.

//UserAdapter: Adapter quản lý hiển thị danh sách người dùng trong lớp học.
class UserClassAdapter(
    private val userList: ArrayList<User>,//danh sách người dùng
    private val isAddMember: Boolean = false,//kiểm tra xem danh sách người dùng có phải là danh sách người dùng để thêm vào lớp học hay không
    private val classId: String = "",//id của lớp học
) : RecyclerView.Adapter<UserClassAdapter.UserClassViewHolder>() {

    //quản lý các view của từng item trong recyclerview
    class UserClassViewHolder(
        val binding: ItemUserBinding//Sử dụng view binding cho item người dùng.
    ) : RecyclerView.ViewHolder(
        binding.root//View gốc của item.
    )

    //tạo viewholder cho mỗi item trong recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserClassViewHolder {
        //lấy layout inflater từ context
        val layoutInflater = LayoutInflater.from(parent.context)
        //liên kết layout item_user.xml với View.
        val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
        //trả về viewholder đã gắn layout
        return UserClassViewHolder(binding)
    }

    //gán dữ liệu từ người dùng vào viewholder cho từng vị trí trong danh sách
    override fun onBindViewHolder(holder: UserClassViewHolder, position: Int) {
        val user = userList[position]//lấy người dùng tại vị trí tương ứng
        val userDAO = UserDAO(holder.itemView.context)//Tạo đối tượng DAO để truy cập dữ liệu User.
        holder.binding.userNameTv.text = user.username//hiển thị tên người dùng

        //kiểm tra và hiển thị avatar của người dùng
        if (user.avatar != null && user.avatar.isNotEmpty()) {
            //nếu avatar không rỗng và không null, tải và hiển thị avatar
            Picasso.get().load(user.avatar).into(holder.binding.userIv)
        } else {
            //nếu avatar rỗng hoặc null, hiển thị avatar mặc định
            Picasso.get().load("https://i.imgur.com/2xW3YHZ.png").into(holder.binding.userIv)
        }

        //lấy thông tin lớp học
        val groupDAO = GroupDAO(holder.itemView.context)
        val group = groupDAO.getGroupById(classId)

        //Hiển thị biểu tượng admin nếu người dùng là chủ sở hữu lớp học.
        if (group.user_id == user.id) {
            holder.binding.isAdminTv.visibility = ViewGroup.VISIBLE
        }

        // Nếu chế độ thêm thành viên được bật, kiểm tra trạng thái của người dùng trong lớp.
        if (isAddMember) {
            //cập nhật background dựa trên trạng thái của người dùng trong lớp
            updateBackground(holder, user, UserDAO(holder.itemView.context))

            //khi người dùng click vào item, thêm hoặc xóa người dùng khỏi lớp
            holder.binding.constraintLayout.setOnClickListener {
                //kiểm tra trạng thái của người dùng trong lớp
                if (userDAO.checkUserInClass(user.id, classId)) {
                    //nếu người dùng đã có trong lớp, xóa người dùng khỏi lớp
                    holder.binding.constraintLayout.background =
                        AppCompatResources.getDrawable(
                            holder.itemView.context,
                            com.kewwi.quickmem.R.drawable.background_unselect
                        )
                    userDAO.removeUserFromClass(user.id, classId)//xóa người dùng khỏi lớp
                } else {
                    //nếu người dùng chưa có trong lớp, thêm người dùng vào lớp
                    holder.binding.constraintLayout.background =
                        AppCompatResources.getDrawable(
                            holder.itemView.context,
                            com.kewwi.quickmem.R.drawable.background_select
                        )
                    userDAO.addUserToClass(user.id, classId)//thêm người dùng vào lớp
                }
            }

        }

    }

    //cập nhật background dựa trên trạng thái của người dùng trong lớp
    private fun updateBackground(holder: UserClassViewHolder, user: User, userDAO: UserDAO) {

        //kiểm tra trạng thái của người dùng trong lớp
        if (userDAO.checkUserInClass(user.id, classId)) {
            //kiểm tra trạng thái của người dùng trong lớp, nếu người dùng đã có trong lớp, hiển thị background được chọn
            holder.binding.constraintLayout.background =
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    com.kewwi.quickmem.R.drawable.background_select
                )
        } else {
            //nếu người dùng chưa có trong lớp, hiển thị background mặc định
            holder.binding.constraintLayout.background =
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    com.kewwi.quickmem.R.drawable.background_unselect
                )
        }
    }

    //trả về số lượng người dùng trong danh sách
    override fun getItemCount(): Int {
        return userList.size
    }
}