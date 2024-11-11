package com.kewwi.quickmem.ui.activities.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.kewwi.quickmem.R
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.dao.FolderDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivityQuizFolderBinding
import com.kewwi.quickmem.databinding.DialogCorrectBinding
import com.kewwi.quickmem.databinding.DialogWrongBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.*

// Activity này thực hiện các chức năng cho quiz trong một folder
class QuizFolderActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityQuizFolderBinding.inflate(layoutInflater) // Liên kết giao diện
    }
    private val cardDAO by lazy {
        CardDAO(this) // Truy cập dữ liệu thẻ học
    }

    private lateinit var correctAnswer: String // Câu trả lời đúng của câu hỏi
    private var progress = 0 // Tiến độ quiz
    private val askedCards = mutableListOf<Card>() // Lưu trữ các thẻ đã hỏi
    private lateinit var id: String // ID của folder chứa các thẻ
    private val job = Job() // Job để quản lý coroutine
    private val scope = CoroutineScope(Dispatchers.Main + job) // Scope cho các coroutine
    private val folderDAO by lazy {
        FolderDAO(this) // Truy cập dữ liệu các folder
    }
    private var dialogCorrect: AlertDialog.Builder? = null // Thông báo khi trả lời đúng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Thiết lập hành động quay lại
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Trở lại màn hình trước
        }

        setNextQuestion() // Lấy câu hỏi tiếp theo
        setUpProgressBar() // Thiết lập thanh tiến độ

    }

    // Kiểm tra câu trả lời người dùng
    @OptIn(DelicateCoroutinesApi::class)
    private fun checkAnswer(selectedAnswer: String, cardId: String): Boolean {
        return if (selectedAnswer == correctAnswer) {
            correctDialog(correctAnswer) // Hiển thị dialog trả lời đúng
            GlobalScope.launch(Dispatchers.IO) {
                cardDAO.updateIsLearnedCardById(cardId, 1) // Cập nhật thẻ là đã học
            }
            setNextQuestion()// Lấy câu hỏi tiếp theo
            progress++ // Tăng tiến độ
            increaseProgress() // Cập nhật thanh tiến độ
            true
        } else {
            wrongDialog(correctAnswer, binding.tvQuestion.text.toString(), selectedAnswer)
            setNextQuestion() // Lấy câu hỏi tiếp theo
            false
        }
    }

    // Cập nhật thanh tiến độ
    private fun increaseProgress() {
        binding.timelineProgress.progress = progress
    }

    // Thiết lập thanh tiến độ, trả về giá trị tối đa
    private fun setUpProgressBar(): Int {
        id = intent.getStringExtra("id") ?: "" // Lấy ID folder từ intent
        val randomCard = cardDAO.getAllCardByFlashCardId(id) // Lấy tất cả các thẻ trong folder
        for (folder in folderDAO.getAllFlashCardIdByFolderId(id)) {
            randomCard.addAll(cardDAO.getAllCardByFlashCardId(folder)) // Thêm thẻ từ các folder con
        }
        val max = randomCard.size
        binding.timelineProgress.max = max // Thiết lập giá trị tối đa cho thanh tiến độ
        return max
    }

    // Lấy câu hỏi tiếp theo
    private fun setNextQuestion() {
        scope.launch {
            id = intent.getStringExtra("id") ?: "" // Lấy ID folder từ intent
            val cards = cardDAO.getCardByIsLearned(id, 0) // Lấy thẻ chưa học trong folder
            val randomCard = cardDAO.getAllCardByFlashCardId(id) // Lấy tất cả các thẻ trong folder


            //Lấy thêm thẻ từ các folder con
            for (folder in folderDAO.getAllFlashCardIdByFolderId(id)) {//ok
                cards.addAll(cardDAO.getCardByIsLearned(folder, 0))// Thêm thẻ chưa học từ các folder con
                randomCard.addAll(cardDAO.getAllCardByFlashCardId(folder)) // Thêm tất cả thẻ từ các folder con
            }

            if (cards.isEmpty()) {
                finishQuiz(1) // Nếu không còn thẻ nào, kết thúc quiz
                return@launch
            }

            val correctCard = cards.random() // Chọn ngẫu nhiên thẻ đúng
            Log.d("TAG", "setNextQuestion: $correctCard")
            randomCard.remove(correctCard) // Loại bỏ thẻ đúng khỏi danh sách thẻ sai

            val incorrectCards = randomCard.shuffled().take(3) // Lấy ngẫu nhiên 3 thẻ sai

            val allCards = (listOf(correctCard) + incorrectCards).shuffled() // Trộn thẻ đúng và thẻ sai
            Log.d("QuizFolderActivity", "allCards size: ${allCards.size}")
            allCards.forEach { card ->
                Log.d("QuizFolderActivity", "Card back: ${card.back}")
            }
            val question = correctCard.front// Câu hỏi là mặt trước của thẻ đúng
            correctAnswer = correctCard.back // Câu trả lời là mặt sau của thẻ đúng

            // Cập nhật giao diện
            withContext(Dispatchers.Main) {
                binding.tvQuestion.text = question
                binding.optionOne.text = allCards[0].back
                binding.optionTwo.text = allCards[1].back
                binding.optionThree.text = allCards[2].back
                binding.optionFour.text = allCards[3].back

                // Lắng nghe sự kiện chọn đáp án
                binding.optionOne.setOnClickListener {
                    checkAnswer(binding.optionOne.text.toString(), correctCard.id)
                }

                binding.optionTwo.setOnClickListener {
                    checkAnswer(binding.optionTwo.text.toString(), correctCard.id)
                }

                binding.optionThree.setOnClickListener {
                    checkAnswer(binding.optionThree.text.toString(), correctCard.id)
                }

                binding.optionFour.setOnClickListener {
                    checkAnswer(binding.optionFour.text.toString(), correctCard.id)
                }

                askedCards.add(correctCard) // Lưu lại thẻ đã hỏi

            }
        }
    }

    // Kết thúc quiz và hiển thị thông báo
    private fun finishQuiz(status: Int) { //1 quiz, 2 learn

        binding.timelineProgress.progress = setUpProgressBar() // Cập nhật tiến độ
        runOnUiThread {
            if (status == 1) {
                PopupDialog.getInstance(this)
                    .setStyle(Styles.SUCCESS)
                    .setHeading(getString(R.string.finish))
                    .setDescription(getString(R.string.finish_quiz))
                    .setDismissButtonText(getString(R.string.ok))
                    .setCancelable(true)
                    .showDialog(object : OnDialogButtonClickListener() {
                        override fun onDismissClicked(dialog: Dialog?) {
                            super.onDismissClicked(dialog)
                            dialog?.dismiss()
                            dialogCorrect?.create()?.dismiss()
                            finish() // Đóng activity khi người dùng nhấn OK
                        }
                    })
            }
        }

    }

    // Hiển thị thông báo khi trả lời đúng
    private fun correctDialog(answer: String) {
        dialogCorrect = AlertDialog.Builder(this)
        val dialogBinding = DialogCorrectBinding.inflate(layoutInflater)
        dialogCorrect!!.setView(dialogBinding.root)
        dialogCorrect!!.setCancelable(true)
        val builder = dialogCorrect!!.create()
        dialogBinding.questionTv.text = answer
        dialogCorrect!!.setOnDismissListener {
            //  startAnimations() (Chưa có logic animation ở đây)
        }

        builder.show()

    }

    // Hiển thị thông báo khi trả lời sai
    private fun wrongDialog(answer: String, question: String, userAnswer: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogWrongBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.questionTv.text = question
        dialogBinding.explanationTv.text = answer
        dialogBinding.yourExplanationTv.text = userAnswer
        dialogBinding.continueTv.setOnClickListener {
            builder.dismiss() // Đóng dialog khi người dùng nhấn "Tiếp tục"
        }
        builder.setOnDismissListener {
            //startAnimations()
        }
        builder.show()
    }

    // Hàm xử lý animation chuyển câu hỏi
    private fun startAnimations() {
        // Danh sách các view (thẻ) cần thực hiện animation
        val views =
            listOf(
                binding.optionOne,// Đáp án 1
                binding.optionTwo, // Đáp án 2
                binding.optionThree, // Đáp án 3
                binding.optionFour, // Đáp án 4
                binding.tvQuestion // Câu hỏi
            )
        // Thời gian animation (1 giây)
        val duration = 1000L
        // Giá trị cuối cùng của vị trí (di chuyển ra ngoài màn hình)
        val endValue = -binding.optionOne.width.toFloat()

        // Lặp qua tất cả các view và áp dụng animation
        views.forEach { view ->
            // Tạo một ObjectAnimator để thay đổi thuộc tính translationX của view
            val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, endValue)
            animator.duration = duration // Thiết lập thời gian animation
            animator.addListener(object : AnimatorListenerAdapter() {
                // Khi animation kết thúc
                override fun onAnimationEnd(animation: Animator) {
                    // Đặt lại vị trí của view về ban đầu (0)
                    view.translationX = 0f
                    // Nếu là đáp án cuối cùng, lấy câu hỏi tiếp theo
                    if (view == binding.optionFour) {
                        setNextQuestion()
                    }
                }
            })
            // Bắt đầu animation
            animator.start()
        }
    }

    // Hủy bỏ coroutine và đóng dialog khi activity bị hủy
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Hủy bỏ coroutine để tránh memory leak
        dialogCorrect?.create()?.dismiss() // Đóng dialog nếu nó còn mở
    }
}