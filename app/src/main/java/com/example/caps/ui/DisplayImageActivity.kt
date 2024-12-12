package com.example.caps.ui

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.caps.databinding.ActivityDisplayImageBinding
import com.example.caps.ui.helper.PredictionHelper

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDisplayImageBinding
    private lateinit var predictionHelper: PredictionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Confirm Image"

        // Inisialisasi PredictionHelper
        predictionHelper = PredictionHelper(
            context = this,
            onResult = { name, confidence ->
                // Tampilkan hasil prediksi
                Toast.makeText(this, "Prediksi: $name ($confidence%)", Toast.LENGTH_LONG).show()
            },
            onError = { error ->
                // Tampilkan error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            },
            onDownloadSuccess = {
                binding.analyze.isEnabled = true
            }
        )

        showImage()

        binding.cancel.setOnClickListener {
            finish()
        }

        binding.analyze.setOnClickListener {

            val drawable = binding.previewImageView.drawable
            if (drawable != null) {
                val bitmap = (drawable as BitmapDrawable).bitmap
                if (bitmap != null && bitmap.width > 0 && bitmap.height > 0) {
                    predictionHelper.predict(bitmap)
                } else {
                    Toast.makeText(this, "Bitmap is empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No image to analyze", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroy() {
        super.onDestroy()
        predictionHelper.close()
    }
}
