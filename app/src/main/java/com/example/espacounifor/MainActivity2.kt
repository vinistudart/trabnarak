package com.example.espacounifor

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etBody: EditText
    private lateinit var etAuthor: EditText
    private lateinit var etDate: EditText
    private lateinit var btnPublish: Button
    private lateinit var btnSelectImage: Button
    private lateinit var ivImage: ImageView
    private var imageBase64: String? = null
    private val db = Firebase.firestore
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        etTitle = findViewById(R.id.et_title)
        etBody = findViewById(R.id.et_body)
        etAuthor = findViewById(R.id.et_author)
        etDate = findViewById(R.id.et_date)
        btnPublish = findViewById(R.id.btn_publish)
        btnSelectImage = findViewById(R.id.btn_select_image)
        ivImage = findViewById(R.id.iv_image)

        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDateInView(calendar)
        }

        etDate.setOnClickListener {
            DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSelectImage.setOnClickListener {
            openImageSelector()
        }

        btnPublish.setOnClickListener {
            if (etTitle.text.isEmpty() || etBody.text.isEmpty() || etAuthor.text.isEmpty() || etDate.text.isEmpty() || imageBase64 == null) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                saveDataToFirestore()
            }
        }
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            ivImage.setImageBitmap(bitmap)
            imageBase64 = convertToBase64(bitmap)
        }
    }

    private fun convertToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun updateDateInView(calendar: Calendar) {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        etDate.setText(sdf.format(calendar.time))
    }

    private fun saveDataToFirestore() {
        val obra = hashMapOf(
            "titulo" to etTitle.text.toString(),
            "corpo" to etBody.text.toString(),
            "autor" to etAuthor.text.toString(),
            "data" to etDate.text.toString(),
            "imagem" to imageBase64
        )
        db.collection("Obras Adicionadas").add(obra)
            .addOnSuccessListener {
                Toast.makeText(this, "Obra adicionada com sucesso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity5::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha em adicionar a Obra: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}