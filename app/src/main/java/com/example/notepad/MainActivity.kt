package com.example.notepad

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        initSearchView()
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

    fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val list = myDbManager.readDbData(newText!!)
                adapter.updateAdapter(list)
                return true
            }

        })
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

    fun fillAdapter() {
        val list = myDbManager.readDbData("")
        adapter.updateAdapter(list)
        if (list.size > 0){
            binding.text.visibility = View.GONE
        }
        else {
            binding.text.visibility = View.VISIBLE
        }
    }

    //function for swipe & delete
    private fun getSwapMg(): ItemTouchHelper{
        return ItemTouchHelper(object: ItemTouchHelper.
            SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //removes from recycler view at adapterPosition
                adapter.removeItem(viewHolder.adapterPosition, myDbManager)

            }

        }
        )
    }

    private fun init(){

        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
}