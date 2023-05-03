package com.example.bookbee

import android.app.ProgressDialog
import android.content.Intent
import android.drm.ProcessedData
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.bookbee.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding:ActivityRegisterBinding

    //firebase authentication
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account | Register user
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //handle back button click
        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        //handle click , begin register
        binding.registerBtn.setOnClickListener{
            /*steps
            * Input Data
            * Validate Data
            * Create Account - Firebase auth
            * save user info - firebase realtime database */
            validateData()
        }



    }

    private var name = ""
    private var email = ""
    private var password = ""


    private fun validateData() {
        //Input Data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        var cpassword = binding.cPasswordEt.text.toString().trim()

        //Validate Data
        if (name.isEmpty()){
            Toast.makeText(this, "Enter Your Name...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email Pattern...", Toast.LENGTH_SHORT).show()
        }
        else if (password.isEmpty()){
            Toast.makeText(this, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else if (cpassword.isEmpty()){
            Toast.makeText(this, "Confirm Password...", Toast.LENGTH_SHORT).show()
        }
        else if (password != cpassword){
            Toast.makeText(this, "Password Doesn't match ...", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //Create Account - Firebase auth

        progressDialog.setMessage("Create Account... ")
        progressDialog.show()

        //create user in firebasr auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //account created
                updateUserInfo()
            }
            .addOnFailureListener{ e->
                //failed
                progressDialog.dismiss()
                Toast.makeText(this, "Failed creating account due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {
        //save user info - firebase realtime database
        progressDialog.setMessage("Saving user info...")
        val timestamp = System.currentTimeMillis()

        //get user uid, user is register so we can get it now
        val uid = firebaseAuth.uid

        //set data to add in dp
        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                //if saved, open user dashboard
                progressDialog.dismiss()
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()

            }
            .addOnFailureListener{ e->
                //failed adding data to dp
                progressDialog.dismiss()
                Toast.makeText(this, "Failed saving user info due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}