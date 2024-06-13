package com.example.espacounifor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var matriculaInput: EditText
    lateinit var senhaInput: EditText
    lateinit var loginBtn: Button
    lateinit var convidadoBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa os componentes da UI
        matriculaInput = findViewById(R.id.username_input)
        senhaInput = findViewById(R.id.password_input)
        loginBtn = findViewById(R.id.login_btn)
        convidadoBtn = findViewById(R.id.convidado_btn)

        Firebase.firestore.collection("Obras").add(mapOf(
            "nome" to "Narak",
            "ano" to  "1990",
            "imagem" to "imgcode"
        ))

        // Configura o ouvinte de cliques no botão de login
        loginBtn.setOnClickListener {
            val matricula = matriculaInput.text.toString()
            val senha = senhaInput.text.toString()

            if (matricula == "1810516" && senha == "1510") {
                // Intent para iniciar a MainActivity4
                val intent = Intent(this, MainActivity4::class.java)
                startActivity(intent)
            } else {
                // Mostra uma mensagem de erro
                Toast.makeText(this, "Matrícula ou senha incorretas", Toast.LENGTH_SHORT).show()
            }
        }

        convidadoBtn.setOnClickListener {
            val intent = Intent(this, MainActivity5::class.java)
            startActivity(intent)
        }
    }
}
//login