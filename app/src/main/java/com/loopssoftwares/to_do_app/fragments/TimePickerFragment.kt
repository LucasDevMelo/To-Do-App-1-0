package com.loopssoftwares.to_do_app.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment

class TimePickerFragment(val callback: (result: String) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c: Calendar = Calendar.getInstance()
        val hour = c[Calendar.HOUR_OF_DAY]
        val minute = c[Calendar.MINUTE]

        return TimePickerDialog(requireContext(), this, hour, minute, DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(time: TimePicker?, hour: Int, minute: Int) {
        val hourString = hour.toString()
        val minuteString = minute.toString()
        callback("$hourString : $minuteString")
    }
}