package com.azeemba.pancakes

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.room.Room
import com.azeemba.pancakes.databinding.ActivityVisitListViewBinding
import com.google.android.material.snackbar.Snackbar
import java.util.logging.Logger


class VisitListView : AppCompatActivity() {
    private lateinit var binding: ActivityVisitListViewBinding
    private lateinit var db: Storage

    fun makeDb(): Storage {
        return Room.databaseBuilder(
            applicationContext,
            Storage::class.java, "azeemba.pancakes.storage"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = makeDb()

        binding = ActivityVisitListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        val itemDecoration: ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        val visitList = mutableListOf<Visit>()
        val adapter = VisitListAdapter(visitList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        Thread(Runnable {
            val visits = db.visitDao().getAll()
            visitList.addAll(visits)
//            adapter.notifyItemRangeInserted(0, visitList.size)
            adapter.notifyDataSetChanged()
            Logger.getAnonymousLogger().info("Adapter notified! ${visitList.size}")
        }).start()
    }

}