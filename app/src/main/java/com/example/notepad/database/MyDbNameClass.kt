package com.example.notepad.database

import android.provider.BaseColumns

object MyDbNameClass: BaseColumns {
    const val TABLE_NAME = "my_table"
    const val COLUMN_TITLE = "title"
    const val COLUMN_NAME_CONTENT = "content"

    const val DATABASE_VERSION = 3
    const val DATABASE_NAME = "MyDb.db"
    const val COLUMN_NAME_IMAGE_URI = "uri"
    const val COLUMN_NAME_TIME = "time"


    const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "$COLUMN_TITLE TEXT," +
            "$COLUMN_NAME_CONTENT TEXT," +
            "$COLUMN_NAME_TIME TEXT," +
            "$COLUMN_NAME_IMAGE_URI TEXT)"
}