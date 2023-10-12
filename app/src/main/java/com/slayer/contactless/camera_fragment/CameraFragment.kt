package com.slayer.contactless.camera_fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.slayer.contactless.R
import com.slayer.contactless.common.Constants
import com.slayer.contactless.common.Utils
import com.slayer.contactless.databinding.FragmentCameraBinding

@ExperimentalGetImage
class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var camera: Camera

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)

        setupCamera()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val imageCapture = ImageCapture.Builder().build()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.surfaceView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )

            } catch (e: Exception) {
                Log.d("TAG", "onCreateView: $e")
            }

        }, ContextCompat.getMainExecutor(requireContext()))

        binding.btnCapture.setOnClickListener {
            takePhoto(imageCapture)
        }

        binding.btnFlash.setOnClickListener {
            if (camera.cameraInfo.torchState.value == TorchState.ON) {
                camera.cameraControl.enableTorch(false)
                binding.btnFlash.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_off_24)
            } else {
                camera.cameraControl.enableTorch(true)

                binding.btnFlash.icon =
                    ContextCompat.getDrawable(requireContext(), R.drawable.baseline_flash_on_24)
            }
        }
    }

    private fun takePhoto(imageCapture: ImageCapture) {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)

                    val bitmap = imageToBitmap(image.image)
                    processImage(bitmap!!, image)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.d("TAG", "onError: $exception")
                }
            }
        )
    }

    private fun imageToBitmap(image: Image?): Bitmap? {
        image?.let { img ->
            val buffer = img.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            // Create a Bitmap from the bytes
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            // Release the Image when done
            img.close()

            return bitmap
        }
        return null
    }

    private fun processImage(bitmap: Bitmap, imageProxy: ImageProxy) {
        val mediaImage = InputImage.fromBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(mediaImage)
            .addOnSuccessListener { visionText ->
                val mutableImage = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = createCanvas(mutableImage)
                val paint = createRectPaint()
                val result = StringBuilder()
                for (block in visionText.textBlocks) {
                    for (line in block.lines) {
                        for (element in line.elements) {
                            if (Utils.isPhoneNumber(element.text)) {
                                drawRect(canvas, element.boundingBox!!, paint)
                                result.append(element.text)
                            }
                        }
                    }
                }

                binding.apply {
                    ivResult.setImageBitmap(mutableImage)
                    groupCamera.visibility = View.GONE
                    groupResult.visibility = View.VISIBLE
                }

                binding.btnDone.setOnClickListener {
                    setFragmentResult(Constants.CAMERA_RESULT_REQUEST_KEY, Bundle().apply {
                        putString(Constants.CAMERA_RESULT_KEY, visionText.text)
                    })
                    findNavController().navigateUp()
                }

                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.d("rabbit", "processImage: $e")
            }

    }

    private fun createCanvas(bitmap: Bitmap): Canvas {
        return Canvas(bitmap)
    }

    private fun createRectPaint(): Paint {
        val rectPaint = Paint()
        rectPaint.color = Color.RED
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeWidth = 4.0f
        return rectPaint
    }

    private fun drawRect(canvas: Canvas, boundingBox: Rect, paint: Paint) {
        canvas.drawRect(boundingBox, paint)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}