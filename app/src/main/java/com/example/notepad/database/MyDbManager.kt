package com.example.notepad.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MyDbManager(context: Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    //open data base
    fun openDb(){
        db = myDbHelper.writableDatabase
    }


    //insert the data
    fun insertDb(title: String, content: String, uri: String, time: String){
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)

        }

        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    //removes the data from DB
    fun removeItemFromDb(id: String){
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    //function for updating values in case if it was changed
    fun updateItem(title: String, content: String, uri: String, id: Int, time: String){
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }

        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }


    //close the DataBase
    fun closeDb(){
        myDbHelper.close()
    }

    //suspend is for Coroutines, withContext (IO) means that it will wait till the IO will end and then will work
    suspend fun readDbData(searchText: String) : ArrayList<ListItem> = withContext(Dispatchers.IO){
        val dataList = ArrayList<ListItem>()
        val selection = "${MyDbNameClass.COLUMN_TITLE} like ?"
        val cursor = db?.query(
            MyDbNameClass.TABLE_NAME,
            null,
            //searching from titles content like selection
            selection,
            //this is searching by letters
            arrayOf("%$searchText%"),
            null,
            null,
            null)

        while(cursor?.moveToNext()!!){

            val dataID = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID))
            val dataTitle = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_TITLE))
            val dataContent = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_CONTENT))
            val dataImageUri = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_IMAGE_URI))
            val dataTime = cursor.getString(cursor.getColumnIndex(MyDbNameClass.COLUMN_NAME_TIME))

            //передаём значения 4х столбцов через отдельный класс
            val item = ListItem()
            item.id = dataID
            item.title = dataTitle
            item.content = dataContent
            item.uri = dataImageUri
            item.time = dataTime

            dataList.add(item)
        }

        //do not forget close the cursor!
        cursor.close()
        //withContext is because we are using withContext
        return@withContext dataList
    }
}