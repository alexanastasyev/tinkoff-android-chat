package com.example.homework2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.children

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val flexbox: FlexboxLayout = findViewById(R.id.flexbox)
        flexbox.children.forEach { child ->
            child.setOnClickListener(View.OnClickListener {
                child.isSelected = !child.isSelected
            })
        }
    }
}