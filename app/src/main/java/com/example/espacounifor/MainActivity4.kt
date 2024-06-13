package com.example.espacounifor

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity4 : AppCompatActivity() {

    private val db = Firebase.firestore
    private lateinit var adicionaBtn: Button
    private lateinit var ivRecent1: ImageView
    private lateinit var ivRecent2: ImageView
    private lateinit var ivRecent3: ImageView
    private lateinit var ivRecent4: ImageView
    private lateinit var ivRecent5: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)

        adicionaBtn = findViewById(R.id.adiciona_obra)
        ivRecent1 = findViewById(R.id.iv_recent_1)
        ivRecent2 = findViewById(R.id.iv_recent_2)
        ivRecent3 = findViewById(R.id.iv_recent_3)
        ivRecent4 = findViewById(R.id.iv_recent_4)
        ivRecent5 = findViewById(R.id.iv_recent_5)

        adicionaBtn.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Load images from Firestore
        loadImagesFromFirestore()
    }

    private fun loadImagesFromFirestore() {
        val images = mapOf(
            "centelhasemmovimento" to ivRecent1,
            "projetotragetorias" to ivRecent2,
            "tardecomarte" to ivRecent3,
            "mostrasanteriores" to ivRecent4,
            "confiratambem" to ivRecent5
        )

        for ((key, imageView) in images) {
            db.collection("images").document(key).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val base64Image = document.getString("imageBase64")
                        if (base64Image != null) {
                            val bitmap = convertBase64ToBitmap(base64Image)
                            imageView.setImageBitmap(bitmap)
                        } else {
                            Toast.makeText(this, "Imagem $key não encontrada", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar imagem $key: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun convertBase64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
}
//home adm, com o botão de add obras