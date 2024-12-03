package com.example.caps.ui

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.caps.R
import com.example.caps.databinding.ActivityDisplayImageBinding

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showImage()
    }

    private fun showImage() {
        val imageUriString = intent.getStringExtra(CameraActivity.IMAGE_GALLERY)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)

            // Set the image URI to the ImageView
            binding.previewImageView.setImageURI(imageUri)
        } else {
            Toast.makeText(this, "No image URI found", Toast.LENGTH_SHORT).show()
        }
    }
}
