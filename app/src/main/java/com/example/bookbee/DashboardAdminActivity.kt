package com.example.bookbee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookbee.databinding.ActivityDashboardAdminBinding
import com.example.bookbee.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityDashboardAdminBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase  auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //handle logout click
        binding.logoutBtn.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

        //handle start add category page
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, CategoryAddActivity::class.java))
        }
    }

    private fun checkUser() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            //not log into
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{
            //Logged In
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}