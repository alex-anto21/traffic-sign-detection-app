package com.example.trafficsigndetectionproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trafficsigndetectionproject.databinding.ActivityMainBinding
import com.example.trafficsigndetectionproject.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://traffic-sign-detection-fd4f0-default-rtdb.europe-west1.firebasedatabase.app/")
        databaseReference = firebaseDatabase.reference.child("users")


        // get current user id
        val currentUser = firebaseAuth.currentUser?.uid

        // get user data
        databaseReference.child(currentUser.toString()).get().addOnSuccessListener {
            if(it.exists()){
                val firstName = it.child("firstName").value
                val lastName = it.child("lastName").value
                val email = it.child("email").value
                val username = "$firstName $lastName"

                // display user data
                binding.titleUsername.text = username
                binding.profileNameDB.text = firstName.toString()
                binding.profileLastNameDB.text = lastName.toString()
                binding.profileEmailDB.text = email.toString()
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Error while getting user data")
        }

        binding.bottomNavigationView.selectedItemId = R.id.bottom_profile
        // navigation bar functionality
        binding.bottomNavigationView.setOnItemReselectedListener{ item ->
            when(item.itemId){
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.bottom_profile -> {
                    true
                }
                R.id.bottom_signout -> {
                    firebaseAuth.signOut()
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
                    true
                }
            }
        }

        binding.deleteAccountButton.setOnClickListener {
            val user = firebaseAuth.currentUser!!
            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    databaseReference.child(currentUser.toString()).removeValue()
                    Toast.makeText(this@ProfileActivity, "Account successfully deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finish()
                } else{
                    Toast.makeText(this@ProfileActivity, "Error while deleting account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}