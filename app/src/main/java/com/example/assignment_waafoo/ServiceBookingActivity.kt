package com.example.assignment_waafoo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.assignment_waafoo.databinding.ActivityServiceBookingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ServiceBookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBookingBinding
    private lateinit var auth: FirebaseAuth
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityServiceBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        loadPurifierInfo()

        binding.preferredDate.setOnClickListener {
            showDatePicker()
        }

        binding.submitBookingButton.setOnClickListener {
            validateAndSubmitBooking()
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadPurifierInfo() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("users").child(userId)

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val purifierType = snapshot.child("purifierType").getValue(String::class.java) ?: "N/A"
                        val nextServiceDate = snapshot.child("nextServiceDate").getValue(String::class.java) ?: "N/A"

                        binding.purifierModelDisplay.text = "Purifier: $purifierType"
                        binding.nextServiceDisplay.text = "Next Service: $nextServiceDate"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ServiceBookingActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                selectedDate = formattedDate
                binding.preferredDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        // Set minimum date to today (can't book service in the past)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun validateAndSubmitBooking() {
        val name = binding.customerName.text.toString().trim()
        val phone = binding.phoneNumber.text.toString().trim()
        val address = binding.address.text.toString().trim()
        val preferredDate = selectedDate
        val issue = binding.issueDescription.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.customerName.error = "Name is required"
            binding.customerName.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            binding.phoneNumber.error = "Phone number is required"
            binding.phoneNumber.requestFocus()
            return
        }

        if (phone.length < 10) {
            binding.phoneNumber.error = "Enter valid phone number"
            binding.phoneNumber.requestFocus()
            return
        }

        if (address.isEmpty()) {
            binding.address.error = "Address is required"
            binding.address.requestFocus()
            return
        }

        if (preferredDate.isEmpty()) {
            Toast.makeText(this, "Please select a preferred date", Toast.LENGTH_SHORT).show()
            return
        }

        // Submit booking to Firebase
        submitBooking(name, phone, address, preferredDate, issue)
    }

    private fun submitBooking(name: String, phone: String, address: String, preferredDate: String, issue: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().getReference("bookings")

            // Generate unique booking ID
            val bookingId = database.push().key ?: return

            // Get current timestamp
            val timestamp = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

            // Create booking data
            val bookingData = mapOf(
                "bookingId" to bookingId,
                "userId" to userId,
                "customerName" to name,
                "phoneNumber" to phone,
                "address" to address,
                "preferredDate" to preferredDate,
                "issueDescription" to issue.ifEmpty { "No specific issue mentioned" },
                "status" to "Pending",
                "timestamp" to timestamp
            )

            // Save to Firebase
            database.child(bookingId).setValue(bookingData)
                .addOnSuccessListener {
                    // Show confirmation dialog
                    showConfirmationDialog(bookingId, preferredDate)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showConfirmationDialog(bookingId: String, preferredDate: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("✅ Booking Confirmed!")
        builder.setMessage(
            """
            Your service booking has been successfully submitted.
            
            Booking ID: ${bookingId.take(8).uppercase()}
            Preferred Date: $preferredDate
            Status: Pending
            
            Our technician will contact you soon to confirm the appointment.
            """.trimIndent()
        )
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish() // Return to dashboard
        }
        builder.setCancelable(false)
        builder.show()
    }
}
