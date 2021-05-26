package com.uisrael.worknow.Views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uisrael.worknow.R

class CommentsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val uidPub = intent.getStringExtra("uidPub")
        val uidUserAcceptProf = intent.getStringExtra("uidUserAcceptProf")
        val uidSolClient = intent.getStringExtra("uidSolClient")


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CommentsFragment.newInstance(uidPub, uidUserAcceptProf, uidSolClient, this))
                .commitNow()
        }
    }
}