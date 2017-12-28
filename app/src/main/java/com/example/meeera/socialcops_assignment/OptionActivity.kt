package com.example.meeera.socialcops_assignment

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.Patterns
import kotlinx.android.synthetic.main.activity_option.*

/**
 * Created by meeera on 27/12/17.
 */
class OptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)
        btn1.setOnClickListener {
            if(!txt.text.isEmpty()) {
                var intent = Intent(this, MainActivity::class.java)
                intent.putExtra("url", txt.text.toString())
                startActivity(intent)
                finish()
            }
        }
        btn2.setOnClickListener {
            if((!txt.text.isEmpty())) {
                var intent = Intent(this, DownloadNotPlaying::class.java)
                intent.putExtra("url", txt.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}