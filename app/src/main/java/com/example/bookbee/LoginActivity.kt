package com.example.bookbee

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookbee.databinding.ActivityLoginBinding
import com.example.bookbee.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityLoginBinding

    //firebase authentication
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while login user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle click , register screen
        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        //handle click , login
        binding.loginBtn.setOnClickListener {
            /*
            * Input Data
            * Validate Data
            * Login Firebase Auth
            * check user type - firebase
            * If user - Move to User dashboard
            * If Admin - Move to admin dashboard*/
            validateData()
        }

    }

    private var email  = ""
    private var password = ""

    private fun validateData() {
        //Input Data
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        // Validate Data
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show()
        }
        else if(password.isEmpty()){
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }

    }

    private fun loginUser() {
        //Login Firebase Auth

        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //login success
                checkUser()

            }
            .addOnFailureListener { e->
                //failed login
                progressDialog.dismiss()
                Toast.makeText(this, "Login failed due to ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun checkUser() {
        //check user type - firebase
        //If user - Move to User dashboard
        //If Admin - Move to admin dashboard

        progressDialog.setMessage("Checking User... ")

        val firebaseUser = firebaseAuth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    progressDialog.dismiss()
                    //get user type eg uer or admin
                    val userType = snapshot.child("userType").value
                    if (userType == "user"){
                        startActivity(Intent(this@LoginActivity, DashboardUserActivity::class.java))
                        finish()

                    }
                    else if(userType == "admin"){
                        startActivity(Intent(this@LoginActivity, DashboardAdminActivity::class.java))
                        finish()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }
}