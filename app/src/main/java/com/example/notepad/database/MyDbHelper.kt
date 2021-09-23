package com.example.notepad.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.notepad.database.MyDbNameClass.DATABASE_NAME
import com.example.notepad.database.MyDbNameClass.DATABASE_VERSION

class MyDbHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(MyDbNameClass.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(MyDbNameClass.SQL_DELETE_TABLE)
        onCreate(db)
    }
}