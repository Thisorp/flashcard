package com.kewwi.quickmem.ui.activities.set

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.flashcard.SetFolderViewAdapter
import com.kewwi.quickmem.data.dao.FlashCardDAO
import com.kewwi.quickmem.data.model.FlashCard
import com.kewwi.quickmem.databinding.ActivityAddFlashCardBinding
import com.kewwi.quickmem.preferen.UserSharePreferences

class AddFlashCardActivity : AppCompatActivity() {
    // Khởi tạo đối tượng binding  (lazy initialization)
    private val binding by lazy {
        ActivityAddFlashCardBinding.inflate(layoutInflater)
    }

    // Khởi tạo các đối tượng cần thiết như adapter, shared preferences, DAO và danh sách flashcard
    private lateinit var adapter: SetFolderViewAdapter
    private val userSharePreferences by lazy {
        UserSharePreferences(this)
    }
    private lateinit var flashCardDAO: FlashCardDAO
    private lateinit var flashCardList: ArrayList<FlashCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)  // Đặt layout của Activity bằng binding

        // Cấu hình toolbar và recycler view
        setupToolbar()
        setupRecyclerView()
    }

    // Phương thức cấu hình toolbar với nút quay lại
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)  // Đặt toolbar tùy chỉnh làm thanh công cụ của ứng dụng
        // Thiết lập sự kiện cho nút điều hướng (nút quay lại)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()  // Quay lại khi nút quay lại được nhấn
        }
    }

    // Phương thức cấu hình RecyclerView để hiển thị danh sách FlashCard
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        flashCardDAO = FlashCardDAO(this)  // Khởi tạo đối tượng DAO để truy xuất dữ liệu FlashCard
        // Lấy danh sách tất cả flashcard của người dùng từ cơ sở dữ liệu
        flashCardList = flashCardDAO.getAllFlashCardByUserId(userSharePreferences.id)

        // Tạo một adapter và thiết lập cho RecyclerView
        adapter = SetFolderViewAdapter(flashCardList, true, intent.getStringExtra("id_folder")!!)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.flashcardRv.layoutManager = linearLayoutManager  // Đặt LayoutManager cho RecyclerView
        binding.flashcardRv.adapter = adapter  // Gán adapter cho RecyclerView
        adapter.notifyDataSetChanged()  // Thông báo cho adapter rằng dữ liệu đã thay đổi
    }

    // Phương thức tạo menu với các mục lựa chọn (ví dụ: "Done")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tick, menu)  // Tạo menu từ tài nguyên menu_tick.xml
        return true
    }

    // Phương thức xử lý khi người dùng chọn một mục trong menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                onBackPressedDispatcher.onBackPressed()  // Quay lại khi nhấn nút "Done"
                Toast.makeText(this, "Added to folder", Toast.LENGTH_SHORT).show()  // Hiển thị thông báo đã thêm vào thư mục
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Phương thức gọi lại khi Activity được quay lại (resume), tái cấu hình RecyclerView
    override fun onResume() {
        super.onResume()
        setupRecyclerView()  // Cập nhật RecyclerView khi Activity được quay lại
    }
}
