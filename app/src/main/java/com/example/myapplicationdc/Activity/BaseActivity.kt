package com.example.myapplicationdc.Activity


import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.myapplicationdc.R

open class BaseActivity : AppCompatActivity() {
    private var pb: Dialog? = null

    fun showProgressBar() {
        if (pb == null) {
            pb = Dialog(this)
            pb?.setContentView(R.layout.progress_bar)
            pb?.setCancelable(false)
        }
        pb?.show()
    }

    fun hideProgressBar() {
        pb?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }
}