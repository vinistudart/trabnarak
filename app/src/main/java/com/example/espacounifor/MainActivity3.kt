package com.example.espacounifor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class MainActivity3 : AppCompatActivity() {

    private val db = Firebase.firestore
    lateinit var favoritesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        favoritesContainer = findViewById(R.id.ll_favorites)

        // Load favorites from Firestore
        loadFavoritesFromFirestore()
    }

    private fun loadFavoritesFromFirestore() {
        db.collection("Favoritos").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("titulo")
                    val body = document.getString("corpo")
                    val author = document.getString("autor")
                    val date = document.getString("data")
                    val base64Image = document.getString("imagem")

                    if (title != null && body != null && author != null && date != null && base64Image != null) {
                        val bitmap = base64ToBitmap(base64Image)
                        addFavoriteToLayout(title, body, author, date, bitmap, document.id)
                    } else {
                        Toast.makeText(this, "Erro ao carregar o favorito ${document.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar favoritos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    private fun addFavoriteToLayout(title: String, body: String, author: String, date: String, bitmap: Bitmap, documentId: String) {
        val inflater = LayoutInflater.from(this)
        val favoriteItemView = inflater.inflate(R.layout.work_item, favoritesContainer, false)

        val imageView = favoriteItemView.findViewById<ImageView>(R.id.work_image)
        val titleView = favoriteItemView.findViewById<TextView>(R.id.work_title)
        val bodyView = favoriteItemView.findViewById<TextView>(R.id.work_body)
        val authorView = favoriteItemView.findViewById<TextView>(R.id.work_author)
        val dateView = favoriteItemView.findViewById<TextView>(R.id.work_date)
        val favoriteButton = favoriteItemView.findViewById<ImageButton>(R.id.work_favorite_button)

        imageView.setImageBitmap(bitmap)
        titleView.text = title
        bodyView.text = body
        authorView.text = author
        dateView.text = date

        // Set favorite button to "favorited" state
        favoriteButton.setImageResource(R.drawable.ic_favorite)

        // Toggle favorite status
        favoriteButton.setOnClickListener {
            db.collection("Favoritos").document(documentId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Remove from favorites
                        db.collection("Favoritos").document(documentId).delete()
                        favoriteButton.setImageResource(R.drawable.ic_favorite_border)
                        Toast.makeText(this, "$title removido dos favoritos", Toast.LENGTH_SHORT).show()
                        favoritesContainer.removeView(favoriteItemView)
                    }
                }
        }

        favoritesContainer.addView(favoriteItemView)
    }
}
//favoritos