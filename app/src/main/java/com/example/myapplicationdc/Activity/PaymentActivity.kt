package com.example.myapplicationdc.Activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Properly inflate the layout using View Binding
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val totalPrice = intent.getStringExtra("fees")
        binding.transactionInfo.text = totalPrice.toString()

        // Set up Pay Button listener
        binding.btnPayNow.setOnClickListener {
            val cardNumber = binding.cardNumberEditText.text.toString()
            val expiryDate = binding.expiryDateEditText.text.toString()
            val cvv = binding.cvvEditText.text.toString()

            if (cardNumber.isNotEmpty() && expiryDate.isNotEmpty() && cvv.isNotEmpty()) {
                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Please fill in all payment details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
