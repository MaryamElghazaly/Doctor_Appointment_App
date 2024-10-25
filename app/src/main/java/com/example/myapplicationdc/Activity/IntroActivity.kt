package com.example.myapplicationdc.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationdc.Activity.Authentication.SignInActivity
import com.example.myapplicationdc.Activity.Authentication.ChooseYourDirectionsActivity
import com.example.myapplicationdc.R
class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val getStartedButton = findViewById<Button>(R.id.getStartedButton)
        getStartedButton.setOnClickListener {
            val intent = Intent(this, ChooseYourDirectionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

