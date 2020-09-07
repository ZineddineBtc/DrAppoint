package com.example.drappoint.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import com.example.drappoint.StaticClass;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SetDate implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView textView;
    private Calendar calendar;
    private Context context;

    public SetDate(TextView textView, Context context){
        this.textView = textView;
        this.textView.setOnClickListener(this);
        calendar = Calendar.getInstance();
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                StaticClass.mySimpleDateFormat, Locale.US);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        textView.setText(simpleDateFormat.format(calendar.getTime()));
    }
    @Override
    public void onClick(View v) {
        new DatePickerDialog(context, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
