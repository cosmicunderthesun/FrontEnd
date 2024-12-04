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

        supportActionBar?.title = "Confirm Image"

        showImage()

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.analyze.setOnClickListener {

        }
    }

    private fun showImage() {
        val imageUriString = intent.getStringExtra(CameraActivity.EXTRA_GALLERY_IMAGE)
        val cameraUriString = intent.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)

        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            binding.previewImageView.setImageURI(imageUri)
        } else if (cameraUriString != null) {
            val cameraUri = Uri.parse(cameraUriString)
            binding.previewImageView.setImageURI(cameraUri)
        } else {
            Toast.makeText(this, "No image URI found", Toast.LENGTH_SHORT).show()
        }
    }
}
