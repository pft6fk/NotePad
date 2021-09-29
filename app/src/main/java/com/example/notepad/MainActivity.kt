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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var job: Job? = null
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

    private fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                fillAdapter(newText!!)
                return true
            }

        })
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        //fills date from DB
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun fillAdapter(text: String) {
        //it will stop searching if job is NOT null(i.e. if user starts to search new text, it will stop
        //searching old one and will search new)
        job?.cancel()
        //if it is null, it will start searching text
        job = CoroutineScope(Dispatchers.Main).launch{
            val list = myDbManager.readDbData(text)
            adapter.updateAdapter(list)
            if (list.size > 0){
                binding.text.visibility = View.GONE
            }
            else {
                binding.text.visibility = View.VISIBLE
            }
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