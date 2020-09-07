package com.example.drappoint.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class SetTime implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    private TextView textView;
    private Calendar calendar;
    private Context context;

    public SetTime(TextView textView, Context context){
        this.textView = textView;
        this.textView.setOnClickListener(this);
        this.calendar = Calendar.getInstance();
        this.context = context;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // TODO Auto-generated method stub
        this.textView.setText( hourOfDay + ":" + minute);
    }

    @Override
    public void onClick(View v) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        new TimePickerDialog(context, this, hour, minute, true).show();
    }
}
