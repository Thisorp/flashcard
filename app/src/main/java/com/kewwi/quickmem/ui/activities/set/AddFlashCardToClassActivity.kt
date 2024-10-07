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
    private val binding by lazy {
        ActivityAddFlashcardToClassBinding.inflate(layoutInflater)
    }
    private val classDAO by lazy {
        GroupDAO(this)
    }
    private val flashCardDAO by lazy {
        FlashCardDAO(this)
    }
    private val userSharePreferences by lazy {
        UserSharePreferences(this)
    }
    private lateinit var adapter: SetClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            val id = intent.getStringExtra("flashcard_id")
            val intent = Intent(this, ViewClassActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        val id = intent.getStringExtra("flashcard_id")
        val flashCards = flashCardDAO.getAllFlashCardByUserId(userSharePreferences.id)
        adapter = SetClassAdapter(flashCards, id!!)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.classRv.layoutManager = linearLayoutManager
        binding.classRv.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}