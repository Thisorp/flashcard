package com.kewwi.quickmem.ui.activities.folder

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.folder.FolderSelectAdapter
import com.kewwi.quickmem.data.dao.FolderDAO
import com.kewwi.quickmem.databinding.ActivityAddToFolderBinding
import com.kewwi.quickmem.preferen.UserSharePreferences
import com.kewwi.quickmem.ui.activities.create.CreateFolderActivity

//// Lớp AddToFolderActivity: cho phép người dùng thêm flashcard vào các thư mục có sẵn
// hoặc tạo mới thư mục
class AddToFolderActivity : AppCompatActivity() {
    // Khởi tạo binding để liên kết giao diện và logic, folderDAO để thao tác với cơ sở dữ liệu thư mục
    private val binding by lazy { ActivityAddToFolderBinding.inflate(layoutInflater) }
    private val folderDAO by lazy { FolderDAO(this) }
    private lateinit var adapter: FolderSelectAdapter

    // Phương thức onCreate: khởi tạo activity, thiết lập giao diện và dữ liệu
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)// Thiết lập giao diện từ layout

        // Gọi các phương thức thiết lập thanh công cụ, danh sách thư mục và tạo thư mục mới
        setupToolbar()
        setupRecyclerView()
        setupCreateNewFolder()
    }

    // Thiết lập thanh công cụ với nút quay lại
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()// Quay lại màn hình trước
        }
    }

    // Thiết lập RecyclerView để hiển thị danh sách thư mục từ cơ sở dữ liệu
    private fun setupRecyclerView() {
        // Lấy thông tin người dùng từ SharedPreferences
        val userSharePreferences = UserSharePreferences(this)
        // Lấy danh sách tất cả thư mục của người dùng từ cơ sở dữ liệu
        val folders = folderDAO.getAllFolderByUserId(userSharePreferences.id)
        // Thiết lập adapter để hiển thị danh sách thư mục
        adapter = FolderSelectAdapter(folders, intent.getStringExtra("flashcard_id")!!)
        // Thiết lập LayoutManager cho RecyclerView (hiển thị dọc)
        val linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        // Gắn LayoutManager và Adapter vào RecyclerView
        binding.folderRv.layoutManager = linearLayoutManager
        binding.folderRv.adapter = adapter
        adapter.notifyDataSetChanged()// Thông báo adapter cập nhật dữ liệu
    }

    // Thiết lập sự kiện khi người dùng muốn tạo thư mục mới
    private fun setupCreateNewFolder() {
        binding.createNewFolderTv.setOnClickListener {
            // Chuyển sang màn hình CreateFolderActivity để tạo thư mục mới
            startActivity(Intent(this, CreateFolderActivity::class.java))
        }
    }

    // Thiết lập menu với biểu tượng dấu tick (để hoàn tất hành động thêm vào thư mục)
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tick, menu)
        return true
    }
    // Xử lý sự kiện khi người dùng chọn các mục trong menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.done) {
            // Khi người dùng nhấn "done", quay trở lại màn hình trước đó
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    // Khi activity được gọi lại (onResume), thiết lập lại danh sách thư mục (để cập nhật nếu có thay đổi)
    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }
}