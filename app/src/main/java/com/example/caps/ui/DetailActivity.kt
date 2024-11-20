package com.example.caps.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.caps.R
import com.google.android.material.appbar.CollapsingToolbarLayout

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val collapsingToolbarLayout: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        collapsingToolbarLayout.title = "Monas"
    }
}