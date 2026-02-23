package com.nonthakorn.myinventory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "Inventory.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "products"
        const val COL_ID = "id"
        const val COL_NAME = "name"
        const val COL_PRICE = "price"
        const val COL_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NAME TEXT NOT NULL,
                $COL_PRICE REAL NOT NULL,
                $COL_QUANTITY INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // CREATE
    fun insertProduct(name: String, price: Double, quantity: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, name)
            put(COL_PRICE, price)
            put(COL_QUANTITY, quantity)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    // READ ALL
    fun getAllProducts(): List<Product> {
        val list = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, "$COL_ID DESC")
        with(cursor) {
            while (moveToNext()) {
                list.add(
                    Product(
                        id = getInt(getColumnIndexOrThrow(COL_ID)),
                        name = getString(getColumnIndexOrThrow(COL_NAME)),
                        price = getDouble(getColumnIndexOrThrow(COL_PRICE)),
                        quantity = getInt(getColumnIndexOrThrow(COL_QUANTITY))
                    )
                )
            }
            close()
        }
        db.close()
        return list
    }

    // UPDATE
    fun updateProduct(product: Product): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NAME, product.name)
            put(COL_PRICE, product.price)
            put(COL_QUANTITY, product.quantity)
        }
        val rows = db.update(TABLE_NAME, values, "$COL_ID = ?", arrayOf(product.id.toString()))
        db.close()
        return rows
    }

    // DELETE
    fun deleteProduct(id: Int): Int {
        val db = writableDatabase
        val rows = db.delete(TABLE_NAME, "$COL_ID = ?", arrayOf(id.toString()))
        db.close()
        return rows
    }
}
