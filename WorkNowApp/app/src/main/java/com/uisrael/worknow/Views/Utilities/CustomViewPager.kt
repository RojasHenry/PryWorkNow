package com.uisrael.worknow.Views.Utilities

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager


internal class CustomViewPager(context: Context?, attrs: AttributeSet?) : ViewPager(context!!, attrs) {
    var isPagingEnabled = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(event)
    }

}