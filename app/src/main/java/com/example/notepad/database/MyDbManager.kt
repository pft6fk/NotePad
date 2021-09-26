package com.example.notepad.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    //open data base
    fun openDb(){
        db = myDbHelper.writableDatabase
    }


    //insert the data
    fun insertDb(title: String, content: String, uri: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
        }

        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    //removes the data from DB
    fun removeItemFromDb(id: String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    //close the DataBase
    fun closeDb(){
        myDbHelper.close()
    }

    fun readDbData() : ArrayList<ListItem>{
        val dataList = ArrayList<ListItem>()

        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)

        while(cursor?.moveToNext()!!){

            val dataID = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_TITLE))
            val dataContent = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataImageUri = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))

            //передаём значения 4х столбцов через отдельный класс
            val item = ListItem()
            item.id = dataID
            item.title = dataTitle
            item.content = dataContent
            item.uri = dataImageUri

            dataList.add(item)
        }

        //do not forget close the cursor!
        cursor.close()
        return dataList
    }
}