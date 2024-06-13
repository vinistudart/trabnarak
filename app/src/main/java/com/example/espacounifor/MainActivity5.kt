package com.example.espacounifor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import java.io.ByteArrayOutputStream

class MainActivity5 : AppCompatActivity() {

    private val db = Firebase.firestore
    lateinit var saibamaisBtn: Button
    lateinit var favoritosBtn: Button
    lateinit var recentWorksContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        saibamaisBtn = findViewById(R.id.saibamais_1)
        favoritosBtn = findViewById(R.id.favoritos_1)
        recentWorksContainer = findViewById(R.id.recent_works_container)

        saibamaisBtn.setOnClickListener {
            val intent = Intent(this, MainActivity6::class.java)
            startActivity(intent)
        }

        favoritosBtn.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }

        // Load works from Firestore
        loadWorksFromFirestore()
    }

    private fun loadWorksFromFirestore() {
        db.collection("Obras Adicionadas").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("titulo")
                    val body = document.getString("corpo")
                    val author = document.getString("autor")
                    val date = document.getString("data")
                    val base64Image = document.getString("imagem")

                    if (title != null && body != null && author != null && date != null && base64Image != null) {
                        val bitmap = base64ToBitmap(base64Image)
                        addWorkToLayout(title, body, author, date, bitmap, document.id)
                    } else {
                        Toast.makeText(this, "Erro ao carregar a obra ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar obras: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun addWorkToLayout(title: String, body: String, author: String, date: String, bitmap: Bitmap, documentId: String) {
        val inflater = LayoutInflater.from(this)
        val workItemView = inflater.inflate(R.layout.work_item, recentWorksContainer, false)

        val imageView = workItemView.findViewById<ImageView>(R.id.work_image)
        val titleView = workItemView.findViewById<TextView>(R.id.work_title)
        val bodyView = workItemView.findViewById<TextView>(R.id.work_body)
        val authorView = workItemView.findViewById<TextView>(R.id.work_author)
        val dateView = workItemView.findViewById<TextView>(R.id.work_date)
        val favoriteButton = workItemView.findViewById<ImageButton>(R.id.work_favorite_button)

        imageView.setImageBitmap(bitmap)
        titleView.text = title
        bodyView.text = body
        authorView.text = author
        dateView.text = date

        // Check if the work is already in favorites
        favoriteButton.setImageResource(R.drawable.ic_favorite_border) // Default to not favorited
        db.collection("Favoritos").document(documentId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    favoriteButton.setImageResource(R.drawable.ic_favorite)
                }
            }

        // Toggle favorite status
        favoriteButton.setOnClickListener {
            db.collection("Favoritos").document(documentId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Remove from favorites
                        db.collection("Favoritos").document(documentId).delete()
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                        Toast.makeText(this, "$title removido dos favoritos", Toast.LENGTH_SHORT).show()
                    } else {
                        // Add to favorites
                        val favoriteData = hashMapOf(
                            "titulo" to title,
                            "corpo" to body,
                            "autor" to author,
                            "data" to date,
                            "imagem" to Base64.encodeToString(bitmapToByteArray(bitmap), Base64.DEFAULT)
                        )
                        db.collection("Favoritos").document(documentId).set(favoriteData)
                        favoriteButton.setImageResource(R.drawable.ic_favorite)
                        Toast.makeText(this, "$title adicionado aos favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        recentWorksContainer.addView(workItemView)
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }
}