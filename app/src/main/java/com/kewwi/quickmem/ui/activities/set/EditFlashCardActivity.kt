package com.kewwi.quickmem.ui.activities.set

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.card.CardAdapter
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.dao.FlashCardDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivityEditFlashCardBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class EditFlashCardActivity : AppCompatActivity() {
    private val binding: ActivityEditFlashCardBinding by lazy {
        ActivityEditFlashCardBinding.inflate(layoutInflater)
    }
    private val flashCardDAO: FlashCardDAO by lazy {
        FlashCardDAO(this)
    }
    private val cardDAO: CardDAO by lazy {
        CardDAO(this)
    }
    private lateinit var cardAdapter: CardAdapter
    private var cards: ArrayList<Card> = ArrayList()// Danh sách thẻ FlashCard
    private var listIdCard: ArrayList<String> = ArrayList() // Lưu các ID của thẻ FlashCard đã bị xóa để có thể khôi phục
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Sự kiện nhấn nút quay lại trên thanh công cụ
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Lấy thông tin của FlashCard cần chỉnh sửa từ intent
        val flashCardId = intent.getStringExtra("flashcard_id")
        val flashCard = flashCardDAO.getFlashCardById(flashCardId)

        // Cập nhật giao diện với thông tin FlashCard
        binding.subjectEt.setText(flashCard.name)
        binding.descriptionEt.setText(flashCard.description)
        binding.privateSwitch.isChecked = flashCard.is_public == 1

        // Lấy các thẻ (card) của FlashCard này từ cơ sở dữ liệu
        cards = cardDAO.getCardsByFlashCardId(flashCardId)
        updateTotalCards()

        // Cài đặt adapter cho RecyclerView
        cardAdapter = CardAdapter(this, cards)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.cardsLv.layoutManager = layoutManager
        binding.cardsLv.adapter = cardAdapter

        // Sự kiện khi nhấn nút thêm thẻ mới
        binding.addFab.setOnClickListener {
            if (!checkTwoCardsEmpty()) {
                val newCard = Card()
                cards.add(newCard)
                cardAdapter.notifyItemInserted(cards.size - 1)

                // Cuộn đến thẻ mới thêm vào và yêu cầu focus
                binding.cardsLv.scrollToPosition(cards.size - 1)
                binding.cardsLv.post {
                    val viewHolder = binding.cardsLv.findViewHolderForAdapterPosition(cards.size - 1)
                    viewHolder?.itemView?.requestFocus()
                }
                updateTotalCards()
            } else {
                Toast.makeText(this, "Please enter question and answer", Toast.LENGTH_SHORT).show()
            }

        }
        // Xử lý vuốt để xóa thẻ FlashCard
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Lưu lại thẻ đã xóa và xóa khỏi RecyclerView
                    val deletedItem = cards[position]

                    // Removing item from recycler view
                    cards.removeAt(position)
                    updateTotalCards()
                    // Kiểm tra thẻ có tồn tại trong cơ sở dữ liệu không, nếu có thì lưu vào listIdCard để khôi phục
                    if (cardDAO.checkCardExist(deletedItem.id)) {
                        listIdCard.add(deletedItem.id)
                    }
                    cardAdapter.notifyItemRemoved(position)

                    // Hiển thị Snackbar để cung cấp lựa chọn khôi phục thẻ đã xóa
                    val snackbar = Snackbar.make(binding.root, "Item was removed from the list.", Snackbar.LENGTH_LONG)
                    snackbar.setAction("UNDO") { _ ->

                        // Khôi phục thẻ vào vị trí cũ
                        if (position >= 0 && position <= cards.size) {
                            cards.add(position, deletedItem)
                            updateTotalCards()

                            // Xóa ID trong danh sách khôi phục
                            if (listIdCard.contains(deletedItem.id)) {
                                listIdCard.remove(deletedItem.id)
                                Toast.makeText(
                                    this@EditFlashCardActivity,
                                    "Card deleted successfully" + deletedItem.id,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            cardAdapter.notifyItemInserted(position)
                        } else {
                            // Nếu vị trí không hợp lệ, thông báo lỗi
                            Toast.makeText(applicationContext, "Error restoring item", Toast.LENGTH_LONG).show()
                        }
                    }
                    snackbar.setActionTextColor(Color.Yellow.toArgb())
                    snackbar.show()
                }
                // Backup of removed item for undo purpose

            }

            // Tùy chỉnh hành động vuốt để hiển thị icon xóa
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_delete)
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX < 0) {  // Vuốt sang trái
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                    val background = ColorDrawable(Color.White.toArgb())
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    background.draw(c)
                } else { // Không vuốt
                    icon.setBounds(0, 0, 0, 0)
                }

                icon.draw(c)
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.cardsLv)
    }


    // Tạo menu với một mục "Done" để lưu các thay đổi
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_set, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Xử lý sự kiện khi người dùng nhấn nút "Done"
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.done -> {
                saveChange() // Lưu thay đổi thẻ FlashCard
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Lưu các thay đổi vào cơ sở dữ liệu
    private fun saveChange() {
        // Lấy dữ liệu từ giao diện người dùng (chủ đề và mô tả)
        val subject = binding.subjectEt.text.toString()
        val description = binding.descriptionEt.text.toString()

        // Kiểm tra xem người dùng đã nhập chủ đề chưa
        if (subject.isEmpty()) {
            binding.subjectTil.error = "Please enter subject"  // Thông báo lỗi nếu không nhập chủ đề
            binding.subjectEt.requestFocus()  // Đặt con trỏ vào ô nhập chủ đề
            return
        }

        // Kiểm tra số lượng thẻ có ít nhất 2 thẻ không
        if (cards.size < 2) {
            showToast("Please enter at least 2 cards")  // Thông báo nếu chưa có ít nhất 2 thẻ
            return
        }

        // Lấy ID của FlashCard từ intent
        val flashCardId = intent.getStringExtra("flashcard_id") ?: return

        // Kiểm tra tất cả các thẻ, đảm bảo mỗi thẻ có câu hỏi và câu trả lời đầy đủ
        for (card in cards) {
            // Nếu không có câu hỏi (front) hoặc câu trả lời (back), không tiếp tục
            if (card.front == null || card.back == null || card.front.isEmpty() || card.back.isEmpty()) {
                showToast("Please enter question and answer")  // Thông báo nếu câu hỏi hoặc câu trả lời thiếu
                updateCardView(cards.indexOf(card))  // Cập nhật lại giao diện thẻ đang thiếu
                return
            }

            // Cập nhật thông tin thẻ
            card.updated_at = getCurrentDate()  // Lấy ngày hiện tại để cập nhật
            card.flashcard_id = flashCardId

            // Kiểm tra xem thẻ đã tồn tại trong cơ sở dữ liệu chưa
            if (cardDAO.checkCardExist(card.id)) {
                // Nếu thẻ đã tồn tại, cập nhật thông tin thẻ
                if (cardDAO.updateCardById(card) <= 0) {
                    showToast("Error updating card")  // Thông báo nếu không thể cập nhật thẻ
                    return
                }
            } else {
                // Nếu thẻ chưa tồn tại, thêm thẻ mới vào cơ sở dữ liệu
                card.id = genUUID()  // Tạo ID mới cho thẻ
                card.created_at = getCurrentDate()  // Lấy ngày tạo thẻ
                if (cardDAO.insertCard(card) <= 0) {
                    showToast("Error inserting card")  // Thông báo nếu không thể thêm thẻ mới
                    return
                }
            }

            // Xóa các thẻ đã bị xóa (nếu có)
            for (card_id in listIdCard) {
                cardDAO.deleteCardById(card_id) >= 0
            }

            // Cập nhật thông tin của FlashCard (chủ đề, mô tả, quyền riêng tư)
            val flashCard = flashCardDAO.getFlashCardById(flashCardId)
            flashCard.name = subject
            flashCard.description = description
            flashCard.is_public = if (binding.privateSwitch.isChecked) 1 else 0
            flashCard.updated_at = getCurrentDate()  // Cập nhật thời gian sửa đổi
            if (flashCardDAO.updateFlashCard(flashCard) <= 0) {
                showToast("Error updating flashcard")  // Thông báo nếu không thể cập nhật FlashCard
                return
            }
        }

        // Thông báo thành công sau khi lưu tất cả thay đổi
        showToast("Flashcard updated successfully")
        finish()  // Đóng Activity hiện tại và quay lại màn hình trước
    }

    // Phương thức hiển thị thông báo dạng Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Phương thức cập nhật lại giao diện thẻ
    private fun updateCardView(position: Int) {
        cardAdapter.notifyItemChanged(position)  // Cập nhật thẻ tại vị trí đã thay đổi
        binding.cardsLv.scrollToPosition(position)  // Cuộn đến vị trí thẻ
        binding.cardsLv.post {
            // Đảm bảo thẻ đang được hiển thị là có focus
            val viewHolder = binding.cardsLv.findViewHolderForAdapterPosition(position)
            viewHolder?.itemView?.requestFocus()
        }
    }

    // Phương thức cập nhật số lượng thẻ hiển thị trong giao diện
    private fun updateTotalCards() {
        binding.totalCardsTv.text = String.format("Total Cards: %d", cards.size)
    }

    // Phương thức kiểm tra xem có ít nhất 2 thẻ trống không (không có câu hỏi và câu trả lời)
    fun checkTwoCardsEmpty(): Boolean {
        var emptyCount = 0
        for (card in cards) {
            if (card.front == null || card.front.isEmpty() || card.back == null || card.back.isEmpty()) {
                emptyCount++
                if (emptyCount == 2) {
                    return true  // Trả về true nếu có 2 thẻ trống
                }
            }
        }
        return false  // Trả về false nếu không có 2 thẻ trống
    }

    // Phương thức lấy ngày hiện tại theo hệ điều hành
    private fun getCurrentDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getCurrentDateNewApi()  // Nếu hệ điều hành mới, dùng phương thức mới
        } else {
            getCurrentDateOldApi()  // Nếu hệ điều hành cũ, dùng phương thức cũ
        }
    }

    // Phương thức tạo ID ngẫu nhiên cho thẻ
    private fun genUUID(): String {
        return UUID.randomUUID().toString()
    }

    // Phương thức lấy ngày hiện tại trên các thiết bị sử dụng API >= 26
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateNewApi(): String {
        val currentDate = LocalDate.now()  // Lấy ngày hiện tại
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")  // Định dạng ngày theo kiểu "dd/MM/yyyy"
        return currentDate.format(formatter)  // Trả về ngày đã được định dạng
    }

    // Phương thức lấy ngày hiện tại trên các thiết bị sử dụng API < 26
    private fun getCurrentDateOldApi(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())  // Định dạng ngày theo kiểu "dd/MM/yyyy"
        return sdf.format(Date())  // Trả về ngày hiện tại theo định dạng đã chỉ định
    }

}