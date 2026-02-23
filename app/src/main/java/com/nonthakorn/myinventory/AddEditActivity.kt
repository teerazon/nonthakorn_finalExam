package com.nonthakorn.myinventory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nonthakorn.myinventory.DatabaseHelper
import com.nonthakorn.myinventory.Product

class AddEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etQuantity: EditText
    private lateinit var btnSave: Button

    private var productId: Int = -1
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        dbHelper = DatabaseHelper(this)
        etName = findViewById(R.id.etName)
        etPrice = findViewById(R.id.etPrice)
        etQuantity = findViewById(R.id.etQuantity)
        btnSave = findViewById(R.id.btnSave)

        // Check if we are in edit mode
        intent?.let {
            productId = it.getIntExtra("product_id", -1)
            if (productId != -1) {
                isEditMode = true
                supportActionBar?.title = "แก้ไขสินค้า"
                etName.setText(it.getStringExtra("product_name"))
                etPrice.setText(it.getDoubleExtra("product_price", 0.0).toString())
                etQuantity.setText(it.getIntExtra("product_quantity", 0).toString())
            } else {
                supportActionBar?.title = "เพิ่มสินค้าใหม่"
            }
        }

        btnSave.setOnClickListener { saveProduct() }
    }

    private fun saveProduct() {
        val name = etName.text.toString().trim()
        val priceStr = etPrice.text.toString().trim()
        val quantityStr = etQuantity.text.toString().trim()

        if (name.isEmpty()) {
            etName.error = "กรุณากรอกชื่อสินค้า"
            return
        }
        if (priceStr.isEmpty()) {
            etPrice.error = "กรุณากรอกราคาสินค้า"
            return
        }
        if (quantityStr.isEmpty()) {
            etQuantity.error = "กรุณากรอกจำนวนสินค้า"
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null || price < 0) {
            etPrice.error = "ราคาไม่ถูกต้อง"
            return
        }
        val quantity = quantityStr.toIntOrNull()
        if (quantity == null || quantity < 0) {
            etQuantity.error = "จำนวนไม่ถูกต้อง"
            return
        }

        if (isEditMode) {
            val product = Product(id = productId, name = name, price = price, quantity = quantity)
            val rows = dbHelper.updateProduct(product)
            if (rows > 0) {
                Toast.makeText(this, "อัปเดตสินค้าเรียบร้อย", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "เกิดข้อผิดพลาดในการอัปเดต", Toast.LENGTH_SHORT).show()
            }
        } else {
            val id = dbHelper.insertProduct(name, price, quantity)
            if (id > 0) {
                Toast.makeText(this, "เพิ่มสินค้าเรียบร้อย", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "เกิดข้อผิดพลาดในการบันทึก", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
