package com.kewwi.quickmem.ui.activities.learn

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import com.kewwi.quickmem.adapter.card.CardLeanAdapter
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivityLearnBinding
import com.yuyakaido.android.cardstackview.*

class LearnActivity : AppCompatActivity(), CardStackListener {
    private val binding: ActivityLearnBinding by lazy {
        ActivityLearnBinding.inflate(layoutInflater)
    }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardLeanAdapter(createCards()) }
    private val cardDAO by lazy { CardDAO(this) }

    private lateinit var size: String


    // Phương thức onCreate: Khởi tạo giao diện, thiết lập các sự kiện và quản lý thẻ
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Kiểm tra nếu không có thẻ để học
        if (createCards().isEmpty()) {
            showHide()// Ẩn các phần không cần thiết nếu không có thẻ
            Toast.makeText(this, "No card to learn", Toast.LENGTH_SHORT).show()
        }

        // Lắng nghe sự kiện quay lại trên thanh công cụ
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        // Cập nhật số lượng thẻ còn lại
        getSize()
        binding.cardsLeftTv.text = "Cards left: $size"

        setupCardStackView() // Khởi tạo giao diện thẻ
        setupButton() // Thiết lập các nút điều khiển

        // Xử lý khi nhấn nút "Keep Learning"
        binding.keepLearnBtn.setOnClickListener {
            if (createCards().isEmpty()) {
                Toast.makeText(this, "No card to learn", Toast.LENGTH_SHORT).show()
            } else {
                showContainer() // Hiển thị lại phần thẻ và các nút điều khiển
                binding.cardsLeftTv.text = "Cards left: ${size.toInt() - 1}"
                adapter.setCards(createCards())
                adapter.notifyDataSetChanged()
                getSize()
                binding.cardsLeftTv.text = "Cards left: $size"
            }
        }

        // Xử lý khi nhấn nút "Reset Learning"
        binding.resetLearnBtn.setOnClickListener {
            cardDAO.resetStatusCardByFlashCardId(intent.getStringExtra("id"))
            showContainer() // Hiển thị lại phần thẻ và các nút điều khiển
            adapter.setCards(createCards())
            adapter.notifyDataSetChanged()
            getSize()
            binding.cardsLeftTv.text = "Cards left: $size"
        }
    }

    // Phương thức xử lý khi kéo thẻ (không được sử dụng đầy đủ ở đây)
    override fun onCardDragging(direction: Direction?, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = $direction, r = $ratio")
    }

    // Xử lý khi thẻ bị quét (swiped) theo hướng trái hoặc phải
    @SuppressLint("SetTextI18n")
    override fun onCardSwiped(direction: Direction?) {
        val card = adapter.getCards()[manager.topPosition - 1]
        // Cập nhật số liệu "learn" hoặc "study" dựa trên hướng quét
        if (direction == Direction.Right) {
            val learnValue = binding.learnTv.text.toString().toInt() + 1
            binding.learnTv.text = learnValue.toString()
            card.status = 1 // Đánh dấu thẻ là đã học
            cardDAO.updateCardStatusById(card.id, card.status)
            size = size.toInt().minus(1).toString()
            binding.cardsLeftTv.text = "Cards left: ${size.toInt()}"
        } else if (direction == Direction.Left) {
            val learnValue = binding.studyTv.text.toString().toInt() + 1
            binding.studyTv.text = learnValue.toString()
            card.status = 2 // Đánh dấu thẻ là đã học nhưng chưa thuộc
            cardDAO.updateCardStatusById(card.id, card.status)
            size = size.toInt().minus(1).toString()
            binding.cardsLeftTv.text = "Cards left: ${size.toInt()}"
        }
        // Nếu đã quét hết thẻ, ẩn giao diện thẻ
        if (manager.topPosition == adapter.getCount()) {
            showHide()
        }


    }

    // Phương thức xử lý khi quay lại thẻ (rewind)
    @SuppressLint("SetTextI18n")
    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
        if (manager.topPosition < adapter.itemCount) {
            val card = adapter.getCards()[manager.topPosition]
            if (card.status == 1) { // Nếu thẻ đã được học, đặt lại trạng thái
                card.status = 0
                cardDAO.updateCardStatusById(card.id, card.status)
                if (binding.learnTv.text.toString().toInt() > 0) {
                    binding.learnTv.text = (binding.learnTv.text.toString().toInt() - 1).toString()
                }
            } else if (card.status == 2) { // Nếu thẻ đã được học nhưng chưa thuộc, đặt lại trạng thái
                card.status = 0
                cardDAO.updateCardStatusById(card.id, card.status)
                if (binding.studyTv.text.toString().toInt() > 0) {
                    binding.studyTv.text = (binding.studyTv.text.toString().toInt() - 1).toString()
                }
            }
        } else {
            Toast.makeText(this, "No card to rewind", Toast.LENGTH_SHORT).show()
        }
        size = size.toInt().plus(1).toString()
        binding.cardsLeftTv.text = "Cards left: ${size.toInt()}"
    }

    // Các phương thức khác của CardStackListener (không sử dụng trong ví dụ này)
    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {
    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    // Thiết lập các nút điều khiển (skip, rewind, like)
    @SuppressLint("SetTextI18n")
    private fun setupButton() {
        // Nút Skip: quét thẻ sang trái
        binding.skipButton.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()

        }

        // Nút Rewind: quay lại thẻ trước đó
        binding.rewindButton.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            binding.cardStackView.rewind()
        }

        // Nút Like: quét thẻ sang phải
        binding.likeButton.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()
        }
    }

    // Khởi tạo giao diện thẻ
    private fun setupCardStackView() {
        initialize()
    }

    // Tạo danh sách các thẻ từ dữ liệu
    private fun createCards(): List<Card> {
        val id: String? = intent.getStringExtra("id")
        return id?.let { cardDAO.getAllCardByStatus(it) } ?: emptyList()
    }


    // Khởi tạo các thông số quản lý thẻ
    private fun initialize() {
        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(1)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = adapter
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    // Ẩn các phần không cần thiết khi đã học hết thẻ
    private fun showHide() {
        // Kiểm tra trạng thái hiển thị của các phần tử trên giao diện
        val learn = binding.learnTv.visibility == View.VISIBLE
        val cardSlack = binding.cardStackView.visibility == View.VISIBLE
        val button = binding.buttonContainer.visibility == View.VISIBLE

        // Nếu tất cả các phần (learn, cardStack, button) đang hiển thị, thì ẩn các phần đó
        if (learn && cardSlack && button) {
            binding.leanLl.visibility = View.GONE
            binding.cardStackView.visibility = View.GONE
            binding.buttonContainer.visibility = View.GONE
            binding.reviewContainer.visibility = View.VISIBLE // Hiển thị phần review
            preview() // Hiển thị thông tin thống kê review
        }


    }

    // Phương thức showContainer: Hiển thị lại các phần cần thiết khi bắt đầu học lại
    private fun showContainer() {
        // Kiểm tra nếu phần thẻ đang bị ẩn, hiển thị lại các phần tử học
        if (binding.cardStackView.visibility == View.GONE) {
            binding.cardStackView.visibility = View.VISIBLE
            binding.buttonContainer.visibility = View.VISIBLE
            binding.leanLl.visibility = View.VISIBLE
            binding.reviewContainer.visibility = View.GONE
            binding.learnTv.text = "0" // Đặt lại số lượng thẻ đã học
            binding.studyTv.text = "0" // Đặt lại số lượng thẻ chưa học
        }
    }

    // Phương thức preview: Cập nhật các thông tin thống kê thẻ học (đã học, vẫn học, thẻ còn lại)
    private fun preview() {
        // Hiển thị số lượng thẻ theo các trạng thái
        binding.knowNumberTv.text = getCardStatus(1).toString() // Số thẻ đã học
        binding.stillLearnNumberTv.text = getCardStatus(2).toString() // Số thẻ đang học
        binding.termsLeftNumberTv.text = getCardStatus(0).toString() // Số thẻ chưa học
        // Tính toán và hiển thị tiến độ học thẻ
        val sum =
            (getCardStatus(1).toFloat() / (getCardStatus(0).toFloat() + getCardStatus(1).toFloat() + getCardStatus(2))) * 100
        binding.reviewProgress.setSpinningBarLength(sum) // Thiết lập chiều dài thanh tiến độ
        binding.reviewProgress.isEnabled = false // Không cho phép thay đổi thanh tiến độ
        binding.reviewProgress.isFocusableInTouchMode = false // Không cho phép thao tác trực tiếp
        binding.reviewProgress.setValueAnimated(sum, 1000) // Cập nhật giá trị thanh tiến độ với hiệu ứng động
    }

    // Phương thức getSize: Lấy số lượng thẻ còn lại từ cơ sở dữ liệu
    private fun getSize(): Int {
        size = createCards().size.toString()
        return size.toInt()
    }

    // Phương thức getCardStatus: Lấy số lượng thẻ theo trạng thái từ cơ sở dữ liệu
    private fun getCardStatus(status: Int): Int {
        val id = intent.getStringExtra("id")
        return cardDAO.getCardByStatus(id, status) // Lấy số lượng thẻ theo trạng thái (0: chưa học, 1: đã học, 2: đang học)
    }

    // Thiết lập menu (thêm tùy chọn vào menu thanh công cụ)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.kewwi.quickmem.R.menu.menu_tick, menu)
        return true
    }

    // Xử lý sự kiện khi người dùng chọn một mục trong menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == com.kewwi.quickmem.R.id.done) {
            showHide() // Ẩn hoặc hiển thị lại các phần học
        }
        return super.onOptionsItemSelected(item)
    }

}

