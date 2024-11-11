package com.kewwi.quickmem.ui.activities.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kewwi.quickmem.R
import com.kewwi.quickmem.data.dao.CardDAO
import com.kewwi.quickmem.data.dao.FlashCardDAO
import com.kewwi.quickmem.data.model.Card
import com.kewwi.quickmem.databinding.ActivityQuizBinding
import com.kewwi.quickmem.databinding.DialogCorrectBinding
import com.kewwi.quickmem.databinding.DialogWrongBinding
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import kotlinx.coroutines.*

class QuizActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityQuizBinding.inflate(layoutInflater)
    }
    private val cardDAO by lazy {
        CardDAO(this)
    }
    private val flashCardDAO by lazy {
        FlashCardDAO(this)
    }

    private var progress = 0 // Biến theo dõi tiến độ quiz
    private lateinit var correctAnswer: String // Lưu câu trả lời đúng cho câu hỏi hiện tại
    private val askedCards = mutableListOf<Card>() // Lưu các thẻ đã được hỏi
    private lateinit var id: String // ID của bộ thẻ FlashCard
    private val job = Job() // Job để quản lý Coroutine
    private val scope = CoroutineScope(Dispatchers.Main + job) // CoroutineScope cho các hoạt động bất đồng bộ

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        id = intent.getStringExtra("id") ?: "" // Lấy ID bộ thẻ từ Intent
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Hiển thị nút quay lại trong thanh công cụ
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Quay lại khi nhấn nút quay lại
        }
        setNextQuestion() // Bắt đầu hiển thị câu hỏi đầu tiên
        val max = cardDAO.getCardByIsLearned(id, 0).size // Lấy số lượng thẻ chưa học
        binding.timelineProgress.max = max // Đặt giá trị tối đa cho tiến trình quiz
    }

    // Phương thức kiểm tra câu trả lời và xử lý kết quả
    private fun checkAnswer(selectedAnswer: String, cardId: String): Boolean {
        return if (selectedAnswer == correctAnswer) {
            correctDialog(correctAnswer)  // Hiển thị dialog khi trả lời đúng
            GlobalScope.launch(Dispatchers.IO) {
                cardDAO.updateIsLearnedCardById(cardId, 1) // Cập nhật trạng thái thẻ là đã học
            }
            setNextQuestion() // Chuyển sang câu hỏi tiếp theo
            progress++ // Tăng tiến trình quiz
            setUpProgressBar() // Cập nhật thanh tiến độ
            true
        } else {
            wrongDialog(correctAnswer, binding.tvQuestion.text.toString(), selectedAnswer) // Hiển thị dialog khi trả lời sai
            setNextQuestion() // Chuyển sang câu hỏi tiếp theo
            false
        }
    }

    // Cập nhật thanh tiến độ quiz
    private fun setUpProgressBar() {
        binding.timelineProgress.progress = progress // Đặt giá trị tiến trình cho thanh tiến độ
        Log.d("progresss", progress.toString()) // In ra giá trị tiến trình cho debugging
    }


    // Phương thức để lấy câu hỏi tiếp theo
    @OptIn(DelicateCoroutinesApi::class)
    private fun setNextQuestion() {
        scope.launch {
            val cards = cardDAO.getCardByIsLearned(id, 0) // Lấy các thẻ chưa học
            val randomCard = cardDAO.getAllCardByFlashCardId(id) // Lấy tất cả các thẻ của bộ thẻ

            if (cards.isEmpty()) { // Nếu không còn thẻ nào chưa học, kết thúc quiz
                finishQuiz()
                return@launch

            }

            val correctCard = cards.random() // Chọn ngẫu nhiên một thẻ chưa học
            randomCard.remove(correctCard) // Loại bỏ thẻ đúng khỏi danh sách thẻ còn lại

            val incorrectCards = randomCard.shuffled().take(3) // Lấy ngẫu nhiên 3 thẻ sai từ danh sách còn lại

            val allCards = (listOf(correctCard) + incorrectCards).shuffled() // Trộn 4 thẻ (1 đúng + 3 sai)
            val question = correctCard.front // Câu hỏi từ mặt trước của thẻ
            correctAnswer = correctCard.back // Câu trả lời đúng từ mặt sau của thẻ

            withContext(Dispatchers.Main) {
                binding.tvQuestion.text = question // Hiển thị câu hỏi
                binding.optionOne.text = allCards[0].back
                binding.optionTwo.text = allCards[1].back
                binding.optionThree.text = allCards[2].back
                binding.optionFour.text = allCards[3].back

                // Đặt sự kiện cho các lựa chọn câu trả lời
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

                askedCards.add(correctCard) // Thêm thẻ đã hỏi vào danh sách đã hỏi


            }
        }
    }

    // Kết thúc quiz và hiển thị thông báo
    private fun finishQuiz() { //1 quiz, 2 learn
        runOnUiThread {

            PopupDialog.getInstance(this)
                .setStyle(Styles.SUCCESS)
                .setHeading(getString(R.string.finish))
                .setDescription(getString(R.string.finish_quiz))
                .setDismissButtonText(getString(R.string.ok))
                .setNegativeButtonText(getString(R.string.cancel))
                .setPositiveButtonText(getString(R.string.ok))
                .setCancelable(true)
                .showDialog(object : OnDialogButtonClickListener() {
                    override fun onDismissClicked(dialog: Dialog?) {
                        super.onDismissClicked(dialog)
                        dialog?.dismiss()
                        finish() // Kết thúc activity quiz khi người dùng nhấn OK
                    }
                })
        }

    }

    // Hiển thị dialog khi trả lời đúng
    private fun correctDialog(answer: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogCorrectBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.questionTv.text = answer // Hiển thị câu trả lời đúng
        dialog.setOnDismissListener {
            // startAnimations()
        }


        builder.show()

    }

    // Hiển thị dialog khi trả lời sai
    private fun wrongDialog(answer: String, question: String, userAnswer: String) {
        val dialog = AlertDialog.Builder(this)
        val dialogBinding = DialogWrongBinding.inflate(layoutInflater)
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)
        val builder = dialog.create()
        dialogBinding.questionTv.text = question // Hiển thị câu hỏi
        dialogBinding.explanationTv.text = answer // Hiển thị câu trả lời đúng
        dialogBinding.yourExplanationTv.text = userAnswer // Hiển thị câu trả lời người dùng đã chọn
        dialogBinding.continueTv.setOnClickListener {
            builder.dismiss() // Đóng dialog khi người dùng nhấn "Tiếp tục"
        }
        builder.setOnDismissListener {
            //startAnimations()
        }
        builder.show()
    }

    // Phương thức để thực hiện các hiệu ứng hoạt hình khi câu hỏi mới xuất hiện
    private fun startAnimations() {
        val views =
            listOf(
                binding.optionOne,
                binding.optionTwo,
                binding.optionThree,
                binding.optionFour,
                binding.tvQuestion
            )
        val duration = 1000L // Thời gian hoạt hình
        val endValue = -binding.optionOne.width.toFloat() // Vị trí kết thúc của hoạt hình

        // Áp dụng hoạt hình cho các lựa chọn câu trả lời
        views.forEach { view ->
            val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, endValue)
            animator.duration = duration
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.translationX = 0f // Đặt lại vị trí của view sau khi hoạt hình kết thúc
                    if (view == binding.optionFour) {
                        setNextQuestion() // Chuyển sang câu hỏi tiếp theo khi hiệu ứng kết thúc
                    }
                }
            })
            animator.start() // Bắt đầu hiệu ứng
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Hủy Coroutine job khi activity bị hủy
    }

}