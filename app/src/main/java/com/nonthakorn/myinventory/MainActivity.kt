package com.nonthakorn.myinventory

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nonthakorn.myinventory.DatabaseHelper
import com.nonthakorn.myinventory.Product
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "MyInventory - คลังสินค้า"

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ProductAdapter(
            mutableListOf(),
            onEditClick = { product ->
                val intent = Intent(this, AddEditActivity::class.java).apply {
                    putExtra("product_id", product.id)
                    putExtra("product_name", product.name)
                    putExtra("product_price", product.price)
                    putExtra("product_quantity", product.quantity)
                }
                startActivity(intent)
            },
            onDeleteClick = { product ->
                AlertDialog.Builder(this)
                    .setTitle("ยืนยันการลบ")
                    .setMessage("ต้องการลบ '${product.name}' ออกจากระบบ?")
                    .setPositiveButton("ลบ") { _, _ ->
                        dbHelper.deleteProduct(product.id)
                        loadProducts()
                        Toast.makeText(this, "ลบสินค้าเรียบร้อย", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("ยกเลิก", null)
                    .show()
            }
        )
        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fabAdd)
        fab.setOnClickListener {
            startActivity(Intent(this, AddEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadProducts()
    }

    private fun loadProducts() {
        val products = dbHelper.getAllProducts()
        adapter.updateList(products)
    }
}
