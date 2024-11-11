package com.kewwi.quickmem.ui.activities.group

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kewwi.quickmem.R
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.databinding.ActivityEditClassBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

//cho phép người dùng chỉnh sửa thông tin của một lớp học (group)
// Nó quản lý việc hiển thị và cập nhật tên lớp, mô tả, trạng thái riêng tư, và thời gian cập nhật.

class EditClassActivity : AppCompatActivity() {
    //// Sử dụng View Binding để truy cập các view trong layout
    private val binding by lazy { ActivityEditClassBinding.inflate(layoutInflater) }

    // DAO để làm việc với dữ liệu nhóm (group)
    private val groupDAO by lazy { GroupDAO(this) }

    //Thiết lập giao diện và chuẩn bị dữ liệu khi Activity được khởi tạo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Thiết lập thanh công cụ và các thành phần khác
        setupToolbar()
        initializeClassDetails()
    }

    //Thiết lập thanh công cụ (toolbar) với hành động quay lại
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    //Lấy thông tin chi tiết của lớp học từ cơ sở dữ liệu và hiển thị lên giao diện
    private fun initializeClassDetails() {
        val classId = intent.getStringExtra("id")// Lấy ID lớp học từ Intent
        val group = groupDAO.getGroupById(classId)// Lấy dữ liệu nhóm từ GroupDAO bằng ID

        // Hiển thị tên lớp và mô tả lên EditText
        binding.classEt.setText(group.name)
        binding.descriptionEt.setText(group.description)

        // Thiết lập trạng thái riêng tư của lớp học (1 là private, 0 là public)
        binding.privateSt.isChecked = group.status == 1
    }

    //Tạo menu tùy chọn khi nhấn vào biểu tượng menu (menu_tick)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Nạp file menu từ resource
        menuInflater.inflate(R.menu.menu_tick, menu)
        return true
    }

    //Xử lý các hành động khi chọn item trong menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Nếu người dùng nhấn vào nút "done" để hoàn tất cập nhật
        if (item.itemId == R.id.done) {
            updateClass()// Gọi hàm cập nhật lớp học
        }
        return super.onOptionsItemSelected(item)
    }

    //Hàm xử lý cập nhật thông tin lớp học vào cơ sở dữ liệu
    private fun updateClass() {
        // Lấy tên lớp học từ EditText và kiểm tra xem có trống hay không
        val name = binding.classEt.text.toString().trim()
        if (name.isEmpty()) {
            binding.classEt.error = "Please enter class name"
            return
        }
        // Lấy mô tả và trạng thái riêng tư từ giao diện
        val description = binding.descriptionEt.text.toString().trim()
        val id = intent.getStringExtra("id")// Lấy ID lớp học từ Intent
        val status = if (binding.privateSt.isChecked) 1 else 0// Kiểm tra trạng thái private

        // Lấy đối tượng lớp học từ cơ sở dữ liệu
        val group = groupDAO.getGroupById(id)

        // Cập nhật các trường thông tin của lớp học
        group.name = name
        group.description = description
        group.status = status
        group.updated_at = getCurrentDate()// Lấy ngày hiện tại

        // Cập nhật lớp học trong cơ sở dữ liệu và kiểm tra kết quả
        if (groupDAO.updateClass(group) > 0L) {
            onBackPressedDispatcher.onBackPressed()// Quay lại màn hình trước đó
            Toast.makeText(this, "Update class successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Update class failed", Toast.LENGTH_SHORT).show()
        }
    }

    // Lấy ngày hiện tại dưới dạng chuỗi theo định dạng "dd/MM/yyyy"
    private fun getCurrentDate(): String {
        // Nếu phiên bản Android từ O (API 26) trở lên thì dùng LocalDate
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCurrentDateNewApi()
        } else {
            getCurrentDateOldApi() // Nếu phiên bản cũ hơn thì dùng SimpleDateFormat
        }
    }

    //Lấy ngày hiện tại bằng LocalDate (dành cho Android từ API 26 trở lên)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getCurrentDateNewApi(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return currentDate.format(formatter)
    }

    //Lấy ngày hiện tại bằng SimpleDateFormat (dành cho Android trước API 26)
    private fun getCurrentDateOldApi(): String {
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }
}