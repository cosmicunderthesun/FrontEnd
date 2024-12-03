package com.example.caps.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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