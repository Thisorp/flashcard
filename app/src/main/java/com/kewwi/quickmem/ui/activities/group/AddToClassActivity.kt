package com.kewwi.quickmem.ui.activities.group

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.group.ClassSelectAdapter
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.databinding.ActivityAddToClassBinding
import com.kewwi.quickmem.preferen.UserSharePreferences
import com.kewwi.quickmem.ui.activities.create.CreateClassActivity

//cho phép người dùng thêm một flashcard vào một lớp học có sẵn
// Nó quản lý việc hiển thị danh sách các lớp học của người dùng và cung cấp tùy chọn tạo lớp học mới.

class AddToClassActivity : AppCompatActivity() {
    // Sử dụng View Binding để truy cập các view trong layout
    private val binding by lazy {
        ActivityAddToClassBinding.inflate(layoutInflater)
    }

    // DAO để làm việc với dữ liệu nhóm (group), trong trường hợp này là các lớp học
    private val classDAO by lazy {
        GroupDAO(this)
    }

    //Thiết lập giao diện và chuẩn bị dữ liệu khi Activity được khởi tạo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Thiết lập thanh công cụ và các thành phần khác
        setupToolbar()
        setupNewClassIntent()
        setupRecyclerView()
    }

    //Thiết lập thanh công cụ (toolbar) với hành động quay lại
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //Thiết lập sự kiện click để chuyển đến màn hình tạo lớp học mới
    private fun setupNewClassIntent() {
        binding.createNewClassTv.setOnClickListener {
            // Chuyển đến CreateClassActivity khi người dùng nhấn vào nút "Tạo lớp học mới"
            Intent(this, CreateClassActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    //Thiết lập RecyclerView để hiển thị danh sách các lớp học của người dùng
    private fun setupRecyclerView() {
        // Lấy thông tin người dùng từ UserSharePreferences (lưu trữ thông tin người dùng)
        val userSharePreferences = UserSharePreferences(this)

        // Lấy danh sách các lớp học mà người dùng sở hữu từ classDAO
        val classList = classDAO.getClassesOwnedByUser(userSharePreferences.id)

        // Thiết lập Adapter để hiển thị danh sách lớp và truyền flashcard_id từ Intent
        val adapter = ClassSelectAdapter(classList, intent.getStringExtra("flashcard_id")!!)

        // Thiết lập LayoutManager cho RecyclerView (hiển thị theo chiều dọc)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.classRv.layoutManager = linearLayoutManager

        // Gán Adapter cho RecyclerView
        binding.classRv.adapter = adapter

        // Thông báo cho Adapter rằng dữ liệu đã thay đổi
        adapter.notifyDataSetChanged()
    }

    //Tạo menu tùy chọn khi nhấn vào biểu tượng menu (menu_tick)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Nạp file menu từ resource
        menuInflater.inflate(R.menu.menu_tick, menu)
        return true
    }

    // Xử lý các hành động khi chọn item trong menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Nếu người dùng nhấn vào nút "done" để hoàn thành
            R.id.done -> {
                // Quay lại màn hình trước và hiển thị thông báo "Done"
                onBackPressedDispatcher.onBackPressed()
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                true
            }

            // Xử lý các hành động khác (nếu có)
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Khi Activity được kích hoạt lại, cập nhật danh sách lớp học trong RecyclerView
    override fun onResume() {
        super.onResume()
        setupRecyclerView()
    }

}