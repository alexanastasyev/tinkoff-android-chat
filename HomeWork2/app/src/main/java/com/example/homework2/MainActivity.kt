package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flexBox: FlexBoxLayout = findViewById(R.id.flexBox)
        flexBox.children.forEach { child ->
            child.setOnClickListener {
                child.isSelected = !child.isSelected
            }
        }
    }
}