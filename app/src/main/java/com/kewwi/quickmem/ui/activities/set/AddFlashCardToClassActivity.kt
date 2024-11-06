package com.kewwi.quickmem.ui.activities.set

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.adapter.flashcard.SetClassAdapter
import com.kewwi.quickmem.data.dao.FlashCardDAO
import com.kewwi.quickmem.data.dao.GroupDAO
import com.kewwi.quickmem.databinding.ActivityAddFlashcardToClassBinding
import com.kewwi.quickmem.preferen.UserSharePreferences
import com.kewwi.quickmem.ui.activities.classes.ViewClassActivity

class AddFlashCardToClassActivity : AppCompatActivity() {
    // Khởi tạo đối tượng binding để truy cập các thành phần giao diện từ layout
    private val binding by lazy {
        ActivityAddFlashcardToClassBinding.inflate(layoutInflater)
    }
    // Khởi tạo đối tượng DAO cho lớp học (GroupDAO) và thẻ flashcard (FlashCardDAO)
    private val classDAO by lazy {
        GroupDAO(this)
    }
    private val flashCardDAO by lazy {
        FlashCardDAO(this)
    }
    private val userSharePreferences by lazy {
        UserSharePreferences(this)
    }
    // Khai báo adapter cho RecyclerView
    private lateinit var adapter: SetClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Cấu hình thanh công cụ và RecyclerView
        setupToolbar()
        setupRecyclerView()
    }

    // Phương thức cấu hình thanh công cụ
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar) // Đặt toolbar làm thanh công cụ của Activity
        // Thiết lập sự kiện khi người dùng nhấn nút quay lại (navigation button)
        binding.toolbar.setNavigationOnClickListener {
            val id = intent.getStringExtra("flashcard_id") // Lấy ID thẻ flashcard từ intent
            val intent = Intent(this, ViewClassActivity::class.java) // Tạo Intent để mở ViewClassActivity
            intent.putExtra("id", id) // Truyền ID thẻ flashcard vào Activity tiếp theo
            startActivity(intent) // Khởi chạy ViewClassActivity
            finish() // Kết thúc AddFlashCardToClassActivity
        }
    }

    // Phương thức cấu hình RecyclerView để hiển thị danh sách các thẻ flashcard
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val id = intent.getStringExtra("flashcard_id") // Lấy ID thẻ flashcard từ intent
        // Lấy danh sách tất cả thẻ flashcard của người dùng từ cơ sở dữ liệu
        val flashCards = flashCardDAO.getAllFlashCardByUserId(userSharePreferences.id)

        // Khởi tạo adapter và gán vào RecyclerView
        adapter = SetClassAdapter(flashCards, id!!) // Truyền danh sách flashcard và ID thẻ flashcard vào adapter
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) // Cấu hình LayoutManager cho RecyclerView
        binding.classRv.layoutManager = linearLayoutManager // Gán LayoutManager cho RecyclerView
        binding.classRv.adapter = adapter // Gán adapter cho RecyclerView
        adapter.notifyDataSetChanged() // Thông báo cho adapter rằng dữ liệu đã thay đổi và cần cập nhật
    }
}