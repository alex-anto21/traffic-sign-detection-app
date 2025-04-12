package com.example.trafficsigndetectionproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trafficsigndetectionproject.databinding.ActivityLoginBinding
import com.example.trafficsigndetectionproject.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://traffic-sign-detection-fd4f0-default-rtdb.europe-west1.firebasedatabase.app/")
        databaseReference = firebaseDatabase.getReference("users")

        binding.signupButton.setOnClickListener {
            // get values from fields
            val firstname = binding.signupFirstname.text.toString()
            val lastname = binding.signupLastname.text.toString()
            val email = binding.signupMail.text.toString()
            val password = binding.signupPassword.text.toString()

            // make some checks
            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                if (password.length < 6){
                    Toast.makeText(this, "Password cannot be smaller than 6 characters", Toast.LENGTH_SHORT).show()
                }
                else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).
                    addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            // get current user from Firebase Authentification
                            val currentUser = firebaseAuth.currentUser
                            if (currentUser != null){
                                // save data to Realtime Database Firebase
                                saveUserToDatabase(currentUser.uid.toString(), firstname, lastname, email, password)
                            }
                        }
                        else {
                            Toast.makeText(this, "Authentification Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.loginpredirecttext.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun saveUserToDatabase(userId: String, firstname: String, lastname: String, email: String, password: String){
        val userHashMap = HashMap<String, String> ()
        userHashMap["firstName"] = firstname
        userHashMap["lastName"] = lastname
        userHashMap["email"] = email
        userHashMap["password"] = password
        databaseReference.child(userId).setValue(userHashMap)
            .addOnSuccessListener {
                Toast.makeText(this@RegisterActivity, "Register Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
        }
            .addOnFailureListener(){
                Toast.makeText(this@RegisterActivity, "Database error", Toast.LENGTH_SHORT).show()
            }
//        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if (!snapshot.exists()){
//                    databaseReference.child(userId).setValue(userHashMap)
//                    Toast.makeText(this@RegisterActivity, "Register Successfully", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(this@RegisterActivity, "Account already exists", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Toast.makeText(this@RegisterActivity, "Database error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
    }
}