package com.example.appwithkotlin

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.ContactsContract
import android.widget.Toast

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "k.db"
        private val TABLE = "EmpTable"
        private val KEY_ID = "id"
        private val KEY_TITLE = "title"
        private var KEY_STATUS = "status"

    }

    var con = context
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_STATUS + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE)
        onCreate(db)
    }

    fun insertData(title: String, s: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        // contentValues.put(KEY_ID, KEY_ID)
        contentValues.put(KEY_TITLE, title)
        contentValues.put(KEY_STATUS, s)

        val success = db.insert(TABLE, null, contentValues)
        db.close()
        return success
    }


    fun getAllData(): ArrayList<DataModal> {
        val stdList: ArrayList<DataModal> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var title: String
        var status: String

        if (cursor.moveToFirst()) {
            do {
                var std: DataModal
                id = cursor.getInt(0)
                title = cursor.getString(1)
                status = cursor.getString(2)
                if (status.equals("read")) {
                    std = DataModal(id = id.toString(), Title = title, readUnread = status, positionHold = true)

                } else {
                    std = DataModal(id = id.toString(), Title = title, readUnread = status, positionHold = false)

                }
                stdList.add(std)
            } while (cursor.moveToNext())
        }

        stdList.reverse()
        return stdList
    }


    fun getLastInertedId(): Int {
        var id: Int? = null
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT  * FROM $TABLE", null)
        if (cursor.moveToLast()) {
            //name = cursor.getString(column_index);//to get other values
            id = cursor.getInt(0) //to get id, 0 is the column index
        }
        return id!!
    }


    fun deleteData(id: String) {
        val db = this.writableDatabase
        val result = db.delete(TABLE, "ID=?", arrayOf(id)).toLong()
        if (result == -1L) {
            Toast.makeText(con, "failed to delete", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(con, "deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateData(id: String, readUnread: String) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_STATUS, readUnread)
        val result = db.update(TABLE, cv, "ID=?", arrayOf(id)).toLong()
        if (result == -1L) {
            Toast.makeText(con, "failed to update", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(con, "update successful", Toast.LENGTH_SHORT).show()
        }
    }
}