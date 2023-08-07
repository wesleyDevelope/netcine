package com.example.netcine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netcine.model.Category
import com.example.netcine.util.CategoryTask

class MainActivity : AppCompatActivity(),CategoryTask.Callback {
 private lateinit var progress: ProgressBar
 private lateinit var adapter:CategoryAdapter
    val categories = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progress = findViewById(R.id.progress_main)


         adapter = CategoryAdapter(categories){id ->
             val intent = Intent(this@MainActivity,MovieActivity::class.java)
             intent.putExtra("id",id)
             startActivity(intent)
         }
        val rv: RecyclerView = findViewById(R.id.rv_main)
        rv.layoutManager = LinearLayoutManager(this,)
        rv.adapter = adapter

        CategoryTask(this).execute("https://api.tiagoaguiar.co/netflixapp/home?apiKey=7dc126a3-f765-42ea-8c46-71e3f2096d57")
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }



    override fun onResult(categories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(categories)
        this.adapter.notifyDataSetChanged()
        progress.visibility = View.GONE
    }

    override fun onFailure(message: String) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show()
        progress.visibility = View.GONE
    }
}


