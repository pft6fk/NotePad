package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.database.MyDbManager
import com.example.notepad.database.RecyclerAdapter
import com.example.notepad.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val myDbManager = MyDbManager(this)
    lateinit var binding: ActivityMainBinding
    val adapter = RecyclerAdapter(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root
        init()
        myDbManager.openDb()

        binding.add.setOnClickListener{
            openEditActivity()
        }
        setContentView(view)
    }

    private fun openEditActivity(){
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)
    }


    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        //fills date from DB
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun fillAdapter(){

        adapter.updateAdapter(myDbManager.readDbData())
    }

    private fun init(){
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}