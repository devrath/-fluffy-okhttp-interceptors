package com.demo.android.watchlist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.demo.R

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
}
