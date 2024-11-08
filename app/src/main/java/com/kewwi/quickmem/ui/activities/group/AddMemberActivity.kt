package com.kewwi.quickmem.ui.activities.group

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.user.UserClassAdapter
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.data.dao.UserDAO
import com.kewwi.quickmem.data.model.User
import com.kewwi.quickmem.databinding.ActivityAddMemberBinding
import com.kewwi.quickmem.ui.activities.classes.ViewClassActivity
//thêm thành viên vào một nhóm
//quản lý việc hiển thị danh sách người dùng, tìm kiếm, và hoàn tất việc thêm thành viên.

class AddMemberActivity : AppCompatActivity() {
    // Sử dụng View Binding để truy cập các view trong layout
    private val binding by lazy { ActivityAddMemberBinding.inflate(layoutInflater) }
    // Tạo instance của UserDAO để làm việc với dữ liệu người dùng
    private val userDAO by lazy { UserDAO(this) }
    // Adapter để hiển thị danh sách người dùng
    private lateinit var userAdapter: UserClassAdapter
    // Danh sách các người dùng
    private lateinit var users: ArrayList<User>
    // DAO cho việc quản lý nhóm
    private val groupDAO by lazy { GroupDAO(this) }

    // Hàm được gọi khi Activity được tạo
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Thiết lập toolbar và xử lý sự kiện quay lại
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Lấy thông tin nhóm từ Intent bằng ID được truyền vào
        val group = groupDAO.getGroupById(intent.getStringExtra("id")!!)

        // Lấy danh sách tất cả người dùng (chỉ cần thông tin cơ bản)
        users = userDAO.getAllUserJustNeed()
        // Loại bỏ người dùng đã là thành viên khỏi danh sách
        users.removeIf(
            fun(user: User): Boolean {
                return user.id == group.user_id
            }
        )

        // Thiết lập RecyclerView để hiển thị danh sách người dùng
        userAdapter = UserClassAdapter(users, true, intent.getStringExtra("id")!!)
        // Thiết lập LayoutManager cho RecyclerView (hiển thị theo chiều dọc)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.memberRv.layoutManager = layoutManager
        // Gán Adapter cho RecyclerView
        binding.memberRv.adapter = userAdapter
        // Thiết lập kích thước cố định cho RecyclerView
        binding.memberRv.setHasFixedSize(true)
        // Thông báo Adapter rằng dữ liệu đã thay đổi
        userAdapter.notifyDataSetChanged()

    }

    // Tạo menu tùy chọn khi nhấn vào biểu tượng menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_user, menu)
        return true
    }

    // Xử lý các sự kiện khi một mục trong menu được chọn
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Nếu người dùng nhấn vào nút tìm kiếm
        if (item.itemId == R.id.action_search) {
            // Khởi tạo và xử lý tìm kiếm
            val searchView = item.actionView as androidx.appcompat.widget.SearchView
            searchView.queryHint = "Search"
            searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                // Xử lý khi người dùng gửi truy vấn tìm kiếm
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                // Xử lý khi nội dung tìm kiếm thay đổi
                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {

                    if (newText != null) {
                        // Chuyển chuỗi tìm kiếm sang chữ thường
                        val searchQuery = newText.lowercase()
                        // Lọc danh sách người dùng dựa trên tên người dùng
                        val userSearch = users.filter {
                            it.username.lowercase().contains(searchQuery)
                        }
                        // Cập nhật Adapter với danh sách kết quả tìm kiếm
                        userAdapter = UserClassAdapter(userSearch as ArrayList<User>)
                        binding.memberRv.adapter = userAdapter
                        // Thông báo cho Adapter rằng dữ liệu đã thay đổi
                        userAdapter.notifyDataSetChanged()
                    }
                    return true
                }
            })
        }
        // Nếu người dùng nhấn vào nút hoàn tất
        else if (item.itemId == R.id.done) {
            // Kiểm tra xem Intent có chứa ID của nhóm hay không
            if (intent.hasExtra("id")) {
                val id = intent.getStringExtra("id")!!
                // Chuyển sang ViewClassActivity và truyền ID nhóm
                val intent = Intent(this, ViewClassActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
                finish()
            } else {
                // handle the case where the intent does not have an extra with the key "id"
                Toast.makeText(this, "Error: missing id", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}