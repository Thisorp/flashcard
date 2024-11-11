package com.kewwi.quickmem.ui.activities.learn

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.kewwi.quickmem.R
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivityTrueFalseFlashCardsBinding
import com.kewwi.quickmem.databinding.DialogCorrectBinding
import com.kewwi.quickmem.databinding.DialogWrongBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener

// Activity để thực hiện quiz flashcards với câu hỏi đúng/sai
class TrueFalseFlashCardsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityTrueFalseFlashCardsBinding.inflate(layoutInflater) }
    private val cardDAO by lazy { CardDAO(this) }
    private lateinit var cardList: ArrayList<Card>
    private var progress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // Thiết lập thanh công cụ và sự kiện back
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Thiết lập câu hỏi và tiến trình ban đầu
        setUpQuestion()
        setUpProgressBar()
    }

    // Cài đặt thanh tiến trình với số thẻ tối đa
    private fun setUpProgressBar(): Int {
        val id = intent.getStringExtra("id") // Lấy id từ intent
        cardList = cardDAO.getCardByIsLearned(id, 0)
        binding.timelineProgress.max = cardList.size // Cập nhật giá trị tối đa của thanh tiến trình
        return cardList.size
    }

    // Cài đặt câu hỏi mới từ danh sách thẻ
    private fun setUpQuestion() {
        val id = intent.getStringExtra("id")// Lấy id từ intent
        cardList = cardDAO.getCardByIsLearned(id, 0) // Lấy danh sách thẻ chưa học
        val cardListAll = cardDAO.getAllCardByFlashCardId(id) // Lấy tất cả thẻ trong flashcard

        // Kiểm tra nếu không còn thẻ nào, kết thúc quiz
        if (cardList.size == 0) {
            finishQuiz()
        }
// Nếu có thẻ trong danh sách, chọn thẻ ngẫu nhiên để làm câu hỏi
        if (cardList.isNotEmpty()) {
            val randomCard = cardList.random() // Chọn thẻ ngẫu nhiên
            cardListAll.remove(randomCard) // Xóa thẻ đã chọn khỏi danh sách thẻ còn lại

            // Chọn câu trả lời sai ngẫu nhiên từ các thẻ còn lại
            val incorrectAnswer = cardListAll.shuffled().take(1)

            // Ngẫu nhiên chọn đúng hoặc sai để hiển thị câu hỏi
            val random = (0..1).random()
            if (random == 0) {
                binding.questionTv.text = randomCard.front// Câu hỏi
                binding.answerTv.text = randomCard.back  // Đáp án đúng
            } else {
                binding.questionTv.text = randomCard.front
                binding.answerTv.text = incorrectAnswer[0].back //Đáp án sai
            }

            // Xử lý khi người dùng chọn "True"
            binding.trueBtn.setOnClickListener {
                if (random == 0) {
                    correctDialog(randomCard.back) // Hiển thị đúng
                    cardDAO.updateIsLearnedCardById(randomCard.id, 1)// Đánh dấu thẻ là đã học
                    setUpQuestion() // Cập nhật câu hỏi tiếp theo
                    progress++
                    increaseProgress() // Cập nhật tiến độ
                } else {
                    wrongDialog(randomCard.back, randomCard.front, incorrectAnswer[0].back)
                    setUpQuestion() // Cập nhật câu hỏi tiếp theo
                }
            }

            // Xử lý khi người dùng chọn "False"
            binding.falseBtn.setOnClickListener {
                if (random == 1) {
                    correctDialog(randomCard.back)
                    cardDAO.updateIsLearnedCardById(randomCard.id, 1)
                    setUpQuestion()
                    progress++
                    increaseProgress()
                } else {
                    wrongDialog(randomCard.back, randomCard.front, incorrectAnswer[0].back)
                    setUpQuestion()
                }
            }
        }
    }

    // Tăng giá trị thanh tiến trình sau mỗi câu hỏi
    private fun increaseProgress() {
        binding.timelineProgress.progress = progress
    }

    // Kết thúc bài kiểm tra và hiển thị hộp thoại thông báo
    private fun finishQuiz() { //1 quiz, 2 learn
        binding.timelineProgress.progress = setUpProgressBar()
        runOnUiThread {

            PopupDialog.getInstance(this)
                .setStyle(Styles.SUCCESS)
                .setHeading(getString(R.string.finish))
                .setDescription(getString(R.string.finish_quiz))
                .setDismissButtonText(getString(R.string.ok))
                .setNegativeButtonText(getString(R.string.cancel))
                .setPositiveButtonText(getString(R.string.ok))
                .setCancelable(false)
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog?) {
                        super.onDismissClicked(dialog)
                        dialog?.dismiss()
                        finish() // Đóng Activity khi người dùng nhấn "OK"
                    }
                })
        }

    }

    // Hiển thị dialog khi câu trả lời đúng
    private fun correctDialog(answer: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogCorrectBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.questionTv.text = answer
        dialog.setOnDismissListener { // Có thể thêm hiệu ứng hoặc hành động sau khi đóng hộp thoại
        }

        builder.show() // Hiển thị dialog

    }

    // Hiển thị dialog khi câu trả lời sai
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
            builder.dismiss() // Đóng dialog khi nhấn "Continue"
        }
        builder.setOnDismissListener { // Có thể thêm hiệu ứng hoặc hành động sau khi đóng hộp thoại

        }
        builder.show() // Hiển thị dialog
    }
}