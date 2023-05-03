package com.example.bookbee

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SplashActivity : AppCompatActivity() {

    //view binding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        firebaseAuth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
            checkUser()
        },1000)
    }

    private fun checkUser() {
        //get current user in loggeg in or not
        var firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{

            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get user type eg uer or admin
                        val userType = snapshot.child("userType").value
                        if (userType == "user"){
                            startActivity(Intent(this@SplashActivity, DashboardUserActivity::class.java))
                            finish()

                        }
                        else if(userType == "admin"){
                            startActivity(Intent(this@SplashActivity, DashboardAdminActivity::class.java))
                            finish()

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        }
    }
}

/*Keep user log in
* check if user is logged in
* check type of user */