package com.example.caps.ui.helper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import com.example.caps.R
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class PredictionHelper(
    private val context: Context,
    private val modelName: String = "Xception_V3.tflite",
    private val onResult: (String, Float) -> Unit,
    private val onError: (String) -> Unit,
    private val onDownloadSuccess: () -> Unit
) {
    private var interpreter: InterpreterApi? = null
    private val inputSize = 224

    init {
        val startTime = System.currentTimeMillis()
        Log.d("PredictionHelper", "TensorFlow Lite initialization started.")
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            Log.d("PredictionHelper", "TensorFlow Lite initialized successfully.")
            downloadModel()
        }.addOnFailureListener {
            onError("TensorFlow Lite failed to initialize.")
        }.addOnCompleteListener {
            val endTime = System.currentTimeMillis()
            Log.d("PredictionHelper", "Initialization completed in ${endTime - startTime} ms.")
        }
    }

    @Synchronized
    private fun downloadModel() {
        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Monumen-Detector", DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model: CustomModel ->
                if (model.file != null) {
                    Log.d("PredictionHelper", "Model berhasil diunduh.")
                    onDownloadSuccess()
                    initializeInterpreter(model)
                } else {
                    onError("Model berhasil diunduh tetapi file tidak ditemukan.")
                }
            }
            .addOnFailureListener { e: Exception ->
                onError(context.getString(R.string.model_gagal))
                Log.e("PredictionHelper", "Gagal mengunduh model: ${e.message}")
            }
    }


    private fun loadModel() {
        try {
            val buffer = loadModelFile(context.assets, modelName)
            Log.d("PredictionHelper", "Model loaded successfully from assets.")
            initializeInterpreter(buffer)
        } catch (e: IOException) {
            onError("Failed to load model: ${e.message}")
            Log.e("PredictionHelper", "Error loading model: ${e.message}")
        }
    }

    private fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_APPLICATION_ONLY)
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            } else if (model is CustomModel) {
                model.file?.let {
                    interpreter = InterpreterApi.create(it, options)
                }
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e("Intialize Interpreter", e.message.toString())
        }
    }

    fun predict(bitmap: Bitmap) {
        if (interpreter == null) {
            onError("Interpreter is not initialized yet.")
            return
        }
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(12) }
        try {
            interpreter?.run(input, output)
            val predictions = output[0]
            val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: -1
            val confidence = predictions[maxIndex] * 100
            val predictedClass = getClassLabel(maxIndex)
            onResult(predictedClass, confidence)
        } catch (e: Exception) {
            onError("Prediction failed: ${e.message}")
            Log.e("PredictionHelper", e.message.toString())
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(inputSize * inputSize)
        scaledBitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)
        for (pixel in pixels) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }
        return byteBuffer
    }


    private fun getClassLabel(index: Int): String {
        val labels = listOf(
            "Patung Persahabatan", "Monumen Nasional", "Patung Bung Karno",
            "Patung Pangeran Diponegoro", "Monumen Selamat Datang",
            "Patung Pahlawan (Tugu Tani)", "Patung R.A. Kartini",
            "Patung M.H. Thamrin", "Monumen Pembebasan Irian Barat",
            "Monumen Ikada", "Patung Kuda Arjuna Wijaya",
            "Monumen Perjuangan Senen"
        )
        return labels.getOrElse(index) { "Unknown" }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): ByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }


    fun close() {
        interpreter?.close()
    }
}
