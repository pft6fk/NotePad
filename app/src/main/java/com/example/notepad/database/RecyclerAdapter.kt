package com.example.notepad.database

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.EditActivity
import com.example.notepad.R
import com.example.notepad.databinding.ItemRecyclerBinding

//we need context to switch the Activities
class RecyclerAdapter(listMain: ArrayList<ListItem>, contextMain: Context):
                                                RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    var listArray = listMain

    //this line makes context available to inner functions
    var context = contextMain

    class ViewHolder(itemView: View, contextHolder: Context) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRecyclerBinding.bind(itemView)
        val context = contextHolder

        //получаем все данные в функцию
        fun setData(item: ListItem){
            binding.textTitle.text = item.title

            //click listener чтоб открыть activity при нажатии на
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    //passing variables to other Activity
                    putExtra(MyItemConstants.I_TITLE_KEY, item.title)
                    putExtra(MyItemConstants.I_CONTENT_KEY, item.content)
                    putExtra(MyItemConstants.I_URI_KEY, item.uri)
                }

                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_recycler, parent, false), context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(listArray[position])
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    fun updateAdapter(listItems: List<ListItem>){
        listArray.clear()
        listArray.addAll(listItems)

        notifyDataSetChanged()
    }

    fun removeItem(pos: Int, dbManager: MyDbManager){
        //grabs and removes from DB
        dbManager.removeItemFromDb(listArray[pos].id.toString())

        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }

}