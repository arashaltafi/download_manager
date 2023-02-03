package com.arash.altafi.downloadmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.downloadmanager.sample1.Sample1
import com.arash.altafi.downloadmanager.sample2.Sample2
import com.arash.altafi.downloadmanager.sample3.Sample3
import com.arash.altafi.downloadmanager.sample4.Sample4
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        btn_sample_1.setOnClickListener {
            startActivity(Intent(this , Sample1::class.java))
        }
        btn_sample_2.setOnClickListener {
            startActivity(Intent(this , Sample2::class.java))
        }
        btn_sample_3.setOnClickListener {
            startActivity(Intent(this , Sample3::class.java))
        }
        btn_sample_4.setOnClickListener {
            startActivity(Intent(this , Sample4::class.java))
        }
    }

}