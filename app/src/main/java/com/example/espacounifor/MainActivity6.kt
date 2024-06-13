package com.example.espacounifor

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.widget.ImageView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.util.*

class MainActivity6 : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val db = Firebase.firestore
    private lateinit var tts: TextToSpeech
    private lateinit var btnSpeak: Button
    private var isTtsInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main6)

        btnSpeak = findViewById(R.id.btn_speak)
        tts = TextToSpeech(this, this)

        btnSpeak.setOnClickListener {
            if (isTtsInitialized) {
                speakOut()
            } else {
                Toast.makeText(this, "TTS não está pronto", Toast.LENGTH_SHORT).show()
            }
        }

        // Load image from Firestore
        loadImageFromFirestore()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("pt", "BR"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Idioma não suportado")
                Toast.makeText(this, "Idioma não suportado", Toast.LENGTH_SHORT).show()
            } else {
                isTtsInitialized = true
                Log.d("TTS", "Text-to-Speech inicializado com sucesso")
            }
        } else {
            Log.e("TTS", "Falha na inicialização do TTS")
            Toast.makeText(this, "Falha na inicialização do TTS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut() {
        val textToSpeak = buildString {
            append("${findViewById<TextView>(R.id.tv_exhibition_title).text}\n")
            append("${findViewById<TextView>(R.id.tv_intro_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_detailed_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_more_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_extra_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_complementary_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_additional_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_curator_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_movement_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_interactive_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_technology_text).text}\n")
            append("${findViewById<TextView>(R.id.tv_previous_exhibits).text}\n")
            append("${findViewById<TextView>(R.id.tv_service_title).text}\n")
            append("${findViewById<TextView>(R.id.tv_exhibit_details).text}\n")
            append("${findViewById<TextView>(R.id.tv_curators).text}\n")
            append("${findViewById<TextView>(R.id.tv_location).text}\n")
            append("${findViewById<TextView>(R.id.tv_opening).text}\n")
            append("${findViewById<TextView>(R.id.tv_lecture).text}\n")
            append("${findViewById<TextView>(R.id.tv_exhibition_dates).text}\n")
            append("${findViewById<TextView>(R.id.tv_visitation).text}\n")
        }

        val sentences = textToSpeak.split(". ")
        for (sentence in sentences) {
            if (sentence.isNotBlank()) {
                Log.d("TTS", "Speaking sentence: $sentence")
                tts.speak(sentence.trim(), TextToSpeech.QUEUE_ADD, null, null)
            }
        }
    }

    private fun loadImageFromFirestore() {
        val imageView = findViewById<ImageView>(R.id.iv_exhibition_image)

        db.collection("images").document("centelhasemmovimento").get()
            .addOnSuccessListener { document ->
                if (document != null && document.contains("imageBase64")) {
                    val base64Image = document.getString("imageBase64")
                    val bitmap = base64ToBitmap(base64Image!!)
                    imageView.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "Imagem não encontrada", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar imagem: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun base64ToBitmap(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}