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
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPaswd: EditText
    private lateinit var edtName: EditText
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPaswd = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            when {
                TextUtils.isEmpty(edtName.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUp,
                        "Enter your username",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(edtEmail.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUp,
                        "Enter your email-id",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(edtPaswd.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignUp,
                        "Enter your password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val username: String = edtName.text.toString().trim { it <= ' ' }
                    val email: String = edtEmail.text.toString().trim { it <= ' ' }
                    val password: String = edtPaswd.text.toString().trim { it <= ' ' }

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@SignUp, "Registered successfully", Toast.LENGTH_SHORT
                                ).show()

                                addUserToDataBase(username,email,firebaseUser.uid)

                                val intent = Intent(this@SignUp, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    this@SignUp,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }
    }

    private fun addUserToDataBase(username: String, email: String, uid: String) {
        FirebaseDatabase.getInstance().getReference().child("user")
            .child(uid).setValue(User(username,email,uid))

    }
}