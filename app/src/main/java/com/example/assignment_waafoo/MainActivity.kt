package com.example.assignment_waafoo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment_waafoo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

       auth = FirebaseAuth.getInstance()


        loadUserData()


        binding.bookServiceButton.setOnClickListener {
            startActivity(Intent(this, ServiceBookingActivity::class.java))
        }

        binding.checkWaterHealthButton.setOnClickListener {
            Toast.makeText(this, "Water Health feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.serviceHistoryButton.setOnClickListener {
            Toast.makeText(this, "Service History feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users").child(userId)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Get user data
                        val username = snapshot.child("username").getValue(String::class.java) ?: "User"
                        val purifierType = snapshot.child("purifierType").getValue(String::class.java) ?: "N/A"
                        val nextServiceDate = snapshot.child("nextServiceDate").getValue(String::class.java) ?: "N/A"

                        // Update UI
                        binding.usernameDisplay.text = username
                        binding.purifierModel.text = "Model: $purifierType"
                        binding.nextServiceDate.text = "Date: $nextServiceDate"

                        // Calculate days remaining and update status
                        calculateServiceStatus(nextServiceDate)
                    } else {
                        Toast.makeText(this@MainActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun calculateServiceStatus(nextServiceDate: String) {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val serviceDate = sdf.parse(nextServiceDate)
            val currentDate = Calendar.getInstance().time

            if (serviceDate != null) {
                // Calculate difference in days
                val diffInMillis = serviceDate.time - currentDate.time
                val daysRemaining = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()

                // Update status based on days remaining
                when {
                    daysRemaining > 7 -> {
                        binding.daysRemaining.text = "🟢 $daysRemaining days remaining"
                        binding.daysRemaining.setTextColor(Color.parseColor("#4CAF50"))
                        binding.purifierStatus.text = "● Working Fine"
                        binding.purifierStatus.setTextColor(Color.parseColor("#4CAF50"))
                    }
                    daysRemaining in 1..7 -> {
                        binding.daysRemaining.text = "🟡 $daysRemaining days remaining"
                        binding.daysRemaining.setTextColor(Color.parseColor("#FFC107"))
                        binding.purifierStatus.text = "● Service Due Soon"
                        binding.purifierStatus.setTextColor(Color.parseColor("#FFC107"))
                    }
                    daysRemaining == 0 -> {
                        binding.daysRemaining.text = "🔴 Service due today!"
                        binding.daysRemaining.setTextColor(Color.parseColor("#F44336"))
                        binding.purifierStatus.text = "● Service Required"
                        binding.purifierStatus.setTextColor(Color.parseColor("#F44336"))
                    }
                    else -> {
                        binding.daysRemaining.text = "🔴 Service overdue by ${-daysRemaining} days"
                        binding.daysRemaining.setTextColor(Color.parseColor("#F44336"))
                        binding.purifierStatus.text = "● Service Overdue"
                        binding.purifierStatus.setTextColor(Color.parseColor("#F44336"))
                    }
                }
            }
        } catch (e: Exception) {
            binding.daysRemaining.text = "Unable to calculate"
            Toast.makeText(this, "Date parsing error", Toast.LENGTH_SHORT).show()
        }
    }
}
