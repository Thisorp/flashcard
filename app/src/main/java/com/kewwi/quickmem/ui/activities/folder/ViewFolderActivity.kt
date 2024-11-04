package com.kewwi.quickmem.ui.activities.folder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kewwi.quickmem.ui.activities.set.AddFlashCardActivity
import com.kewwi.quickmem.ui.activities.learn.QuizFolderActivity
import com.kewwi.quickmem.R
import com.kewwi.quickmem.adapter.flashcard.SetFolderViewAdapter
import com.kewwi.quickmem.data.dao.FolderDAO
import com.kewwi.quickmem.data.model.FlashCard
import com.kewwi.quickmem.databinding.ActivityViewFolderBinding
import com.kewwi.quickmem.databinding.DialogCreateFolderBinding
import com.kewwi.quickmem.preferen.UserSharePreferences
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.saadahmedsoft.popupdialog.PopupDialog
import com.saadahmedsoft.popupdialog.Styles
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener
import com.squareup.picasso.Picasso

// Lớp ViewFolderActivity: Quản lý các hành động trong giao diện xem chi tiết thư mục,
// như xem các flashcard trong thư mục,
// sửa đổi hoặc xóa thư mục.
class ViewFolderActivity : AppCompatActivity(), BottomSheetListener {
    // Binding để liên kết logic với giao diện người dùng, folderDAO để thao tác với cơ sở dữ liệu thư mục
    private val binding by lazy { ActivityViewFolderBinding.inflate(layoutInflater) }
    private val folderDAO by lazy { FolderDAO(this) }
    private lateinit var adapter: SetFolderViewAdapter

    // onCreate: Khởi tạo activity và thiết lập giao diện người dùng
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Gọi các phương thức thiết lập toolbar, chi tiết thư mục, danh sách flashcard và nút học
        setupToolbar()
        setupToolbar()
        setupFolderDetails()
        setupRecyclerView()
        setupLearnButton()
    }

    // setupToolbar: Thiết lập thanh công cụ với nút quay lại
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // setupFolderDetails: Hiển thị thông tin chi tiết về thư mục và người dùng (tên, ảnh đại diện, số flashcard)
    @SuppressLint("SetTextI18n")
    private fun setupFolderDetails() {
        val id = intent.getStringExtra("id")
        val userSharePreferences = UserSharePreferences(this)
        val folder = folderDAO.getFolderById(id)
        binding.folderNameTv.text = folder.name
        Picasso.get().load(userSharePreferences.avatar).into(binding.avatarIv)
        binding.userNameTv.text = userSharePreferences.userName
        binding.termCountTv.text = folderDAO.getAllFlashCardByFolderId(id).size.toString() + " flashcards"
    }

    // setupRecyclerView: Hiển thị danh sách các flashcard trong thư mục
    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val id = intent.getStringExtra("id")
        adapter = SetFolderViewAdapter(folderDAO.getAllFlashCardByFolderId(id) as ArrayList<FlashCard>, false)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.setRv.layoutManager = linearLayoutManager
        binding.setRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    // setupLearnButton: Thiết lập nút để bắt đầu học các flashcard trong thư mục
    private fun setupLearnButton() {
        val id = intent.getStringExtra("id")
        binding.learnThisFolderBtn.setOnClickListener {
            val newIntent = Intent(this, QuizFolderActivity::class.java)
            newIntent.putExtra("id", id)
            startActivity(newIntent)
        }
    }

    // onCreateOptionsMenu: Thiết lập menu để quản lý các hành động trong thư mục
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.vew_folder_menu, menu)
        return true
    }
    // onOptionsItemSelected: Xử lý các lựa chọn trong menu (ví dụ: mở menu dưới dạng bottom sheet)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu) {
            BottomSheetMenuDialogFragment.Builder(
                context = this,
                sheet = R.menu.folder_menu,
                title = "Folder Menu",
                listener = this
            ).show(supportFragmentManager, "Menu")
        }
        return super.onOptionsItemSelected(item)
    }

    //Hiển thị và ghi log khi bottom sheet được hiển thị hoặc bị đóng
    override fun onSheetDismissed(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?, dismissEvent: Int) {
        Log.d("TAG", "onSheetDismissed: ")
    }


    // onSheetItemSelected: Xử lý các lựa chọn từ bottom sheet (sửa, xóa, thêm flashcard, chia sẻ thư mục)
    @SuppressLint("SetTextI18n")
    override fun onSheetItemSelected(bottomSheet: BottomSheetMenuDialogFragment, item: MenuItem, `object`: Any?) {
        when (item.itemId) {
            R.id.edit_folder -> {
                handleEditFolder() // Sửa thư mục
            }

            R.id.delete_folder -> {

                handleDeleteFolder()// Xóa thư mục

            }

            R.id.add_set -> {
                handleAddSet()// Thêm flashcard vào thư mục

            }

            R.id.share -> {

            }
        }
    }

    // handleAddSet: Mở màn hình thêm flashcard vào thư mục hiện tại
    private fun handleAddSet() {
        val id = intent.getStringExtra("id")
        val newIntent = Intent(this, AddFlashCardActivity::class.java)
        newIntent.putExtra("id_folder", id)
        startActivity(newIntent)
    }

    // handleDeleteFolder: Hiển thị popup xác nhận và xử lý xóa thư mục
    private fun handleDeleteFolder() {
        PopupDialog.getInstance(this)
            .setStyle(Styles.STANDARD)
            .setHeading("Delete Folder")
            .setDescription("Are you sure you want to delete this folder?")
            .setPopupDialogIcon(R.drawable.ic_delete)
            .setCancelable(true)
            .showDialog(
                object : OnDialogButtonClickListener() {
                    override fun onPositiveClicked(dialog: Dialog?) {
                        super.onPositiveClicked(dialog)
                        // Kiểm tra và xóa thư mục từ cơ sở dữ liệu
                        if (folderDAO.deleteFolder(intent.getStringExtra("id")) > 0L) {
                            PopupDialog.getInstance(this@ViewFolderActivity)
                                .setStyle(Styles.SUCCESS)
                                .setHeading(getString(R.string.success))
                                .setDescription(getString(R.string.delete_set_success))
                                .setCancelable(false)
                                .setDismissButtonText(getString(R.string.ok))
                                .showDialog(object : OnDialogButtonClickListener() {
                                    override fun onDismissClicked(dialog: Dialog) {
                                        super.onDismissClicked(dialog)
                                        finish()
                                    }
                                })
                        } else {
                            PopupDialog.getInstance(this@ViewFolderActivity)
                                .setStyle(Styles.FAILED)
                                .setHeading(getString(R.string.error))
                                .setDescription(getString(R.string.delete_set_error))
                                .setCancelable(true)
                                .showDialog(object : OnDialogButtonClickListener() {
                                    override fun onPositiveClicked(dialog: Dialog) {
                                        super.onPositiveClicked(dialog)
                                    }
                                })
                        }
                    }

                    override fun onNegativeClicked(dialog: Dialog?) {
                        super.onNegativeClicked(dialog)
                        dialog?.dismiss()
                    }
                }
            )
    }

    // handleEditFolder: Hiển thị dialog để sửa tên và mô tả thư mục, cập nhật dữ liệu trong cơ sở dữ liệu
    @SuppressLint("SetTextI18n")
    private fun handleEditFolder() {

        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogCreateFolderBinding.inflate(layoutInflater)

        // Lấy dữ liệu thư mục hiện tại để hiển thị
        val id = intent.getStringExtra("id")
        val folder = folderDAO.getFolderById(id)
        dialogBinding.folderEt.setText(folder.name)
        dialogBinding.descriptionEt.setText(folder.description)

        builder.setView(dialogBinding.root)
        builder.setCancelable(true)
        val dialog = builder.create()

        dialogBinding.folderEt.requestFocus()
        dialogBinding.cancelTv.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.okTv.setOnClickListener {
            val name = dialogBinding.folderEt.text.toString()
            val description = dialogBinding.descriptionEt.text.toString()

            // Kiểm tra tên thư mục, cập nhật cơ sở dữ liệu và giao diện
            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter folder name", Toast.LENGTH_SHORT).show()
            } else {
                folder.name = name
                folder.description = description
                if (folderDAO.updateFolder(folder) > 0L) {
                    Toast.makeText(this, "Update folder successfully", Toast.LENGTH_SHORT).show()
                    //refresh data folder
                    binding.folderNameTv.text = folder.name
                    binding.termCountTv.text = folderDAO.getAllFlashCardByFolderId(id).size.toString() + " flashcards"
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Update folder failed", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        dialog.show()

    }

    //Hiển thị và ghi log khi bottom sheet được hiển thị hoặc bị đóng
    override fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?) {
        Log.d("TAG", "onSheetShown: ")
    }

    // onResume: Cập nhật danh sách flashcard và chi tiết thư mục khi quay lại màn hình
    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()

        //refresh data adapter
        val id = intent.getStringExtra("id")
        adapter = SetFolderViewAdapter(folderDAO.getAllFlashCardByFolderId(id) as ArrayList<FlashCard>, false)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.setRv.layoutManager = linearLayoutManager
        binding.setRv.adapter = adapter
        adapter.notifyDataSetChanged()

        // Cập nhật lại chi tiết thư mục khi quay lại màn hình
        setupFolderDetails()

    }
}