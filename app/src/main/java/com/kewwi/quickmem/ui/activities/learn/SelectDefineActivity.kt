package com.kewwi.quickmem.ui.activities.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.adapter.DefineListAdapter
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivitySelectDefineBinding

// Activity cho phép người dùng chọn định nghĩa của các thẻ (cards)
class SelectDefineActivity : AppCompatActivity() {

    // Kết nối với view thông qua ViewBinding
    private val binding by lazy { ActivitySelectDefineBinding.inflate(layoutInflater) }
    // Khởi tạo DAO để truy vấn dữ liệu thẻ (cards)
    private val cardDAO by lazy { CardDAO(this) }
    lateinit var cardList: List<Card> // Danh sách thẻ (cards) sẽ được hiển thị

    // Khai báo adapter để kết nối danh sách thẻ với RecyclerView
    private lateinit var defineListAdapter: DefineListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Lấy id từ intent truyền qua
        val id = intent.getStringExtra("id")

        // Lấy danh sách thẻ (cards) từ database theo id
        cardList = cardDAO.getCardsByFlashCardId(id)

        // Cài đặt RecyclerView: sử dụng LinearLayoutManager để hiển thị danh sách theo chiều dọc
        binding.defineRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.defineRv.setHasFixedSize(true)
        // Khởi tạo adapter và gán nó cho RecyclerView
        defineListAdapter = DefineListAdapter(cardList)
        binding.defineRv.adapter = defineListAdapter
        // Cập nhật adapter sau khi danh sách thẻ được tải
        defineListAdapter.notifyDataSetChanged()


    }

    // Hàm này nhận vào danh sách thẻ và số lượng thẻ cần lấy
    // Trả về một danh sách thẻ mới có số lượng phần tử bằng 'num'
    private fun getCardDefine(cardList: List<Card>, num: Int): List<Card> {
        val cardDefineList = mutableListOf<Card>()
        // Lặp qua danh sách cardList và lấy ra số lượng thẻ cần thiết
        for (i in 0 until num) {
            cardDefineList.add(cardList[i])
        }
        return cardDefineList
    }
}