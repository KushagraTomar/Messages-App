package com.kush.messages_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPaswd: EditText
    private lateinit var btnLogIn: Button
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        btnLogIn = findViewById(R.id.btnLogIn)
        edtEmail = findViewById(R.id.edt_email)
        edtPaswd = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            val intent = Intent(this@Login, SignUp::class.java)
            startActivity(intent)
        }

        btnLogIn.setOnClickListener {
            when {
                TextUtils.isEmpty(edtEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Login,
                        "Enter your email-id",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(edtPaswd.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@Login,
                        "Enter your password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email: String = edtEmail.text.toString().trim { it <= ' ' }
                    val password: String = edtPaswd.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Toast.makeText(
                                    this@Login, "Logged in successfully", Toast.LENGTH_SHORT
                                ).show()

                                val intent = Intent(this@Login, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra(
                                    "user_id",
                                    FirebaseAuth.getInstance().currentUser!!.uid
                                )
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@Login,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }
}