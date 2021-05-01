package com.uisrael.worknow.Views.Dialogs

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(var activity: Activity) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var calendar: Calendar
    private lateinit var onDateSetListener: DatePickerDialog.OnDateSetListener

    fun setup( calendar: Calendar, onDateSetListener: DatePickerDialog.OnDateSetListener ){
        this.calendar = calendar
        this.onDateSetListener = onDateSetListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val year = calendar.get(Calendar.YEAR);
        val month = calendar.get(Calendar.MONTH);
        val day = calendar.get(Calendar.DAY_OF_MONTH);

        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        onDateSetListener.onDateSet(view, year, month, dayOfMonth)
    }
}