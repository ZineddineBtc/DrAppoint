package com.example.drappoint.daos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.drappoint.models.Appointment;
import com.example.drappoint.models.Doctor;

import java.util.ArrayList;

public class AppointmentHistoryDAO extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "history__.db";
    private static final String HISTORY_TABLE_NAME = "history__";
    private static final String DOCTOR_ID = "id";
    private static final String DOCTOR_NAME = "name";
    private static final String DOCTOR_SPECIALTY = "specialty";
    private static final String APPOINTMENT_TIME = "time";

    public AppointmentHistoryDAO(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table "+ HISTORY_TABLE_NAME +
                        " ("+ DOCTOR_ID +" text primary key, " +
                        DOCTOR_NAME +" text, "+
                        DOCTOR_SPECIALTY +" text, "+
                        APPOINTMENT_TIME +" text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+ HISTORY_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertAppointment(Doctor doctor, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCTOR_ID, doctor.getId());
        contentValues.put(DOCTOR_NAME, doctor.getName());
        contentValues.put(DOCTOR_SPECIALTY, doctor.getSpecialty());
        contentValues.put(APPOINTMENT_TIME, date);
        db.insert(HISTORY_TABLE_NAME, null, contentValues);
        return true;
    }

    public void deleteAppointment(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(HISTORY_TABLE_NAME,
                DOCTOR_ID +" = ? ",
                new String[] {id});
    }

    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle")
        Cursor cursor =  db.rawQuery( "select * from "+ HISTORY_TABLE_NAME,
                null );
        cursor.moveToLast();
        while(!cursor.isBeforeFirst()){
            Appointment appointment = new Appointment();
            Doctor doctor = new Doctor();
            doctor.setId(cursor.getString(cursor.getColumnIndex(DOCTOR_ID)));
            doctor.setName(cursor.getString(cursor.getColumnIndex(DOCTOR_NAME)));
            doctor.setSpecialty(cursor.getString(cursor.getColumnIndex(DOCTOR_SPECIALTY)));
            appointment.setDoctor(doctor);
            appointment.setDate(cursor.getString(cursor.getColumnIndex(APPOINTMENT_TIME)));
            appointments.add(appointment);
            cursor.moveToPrevious();
        }
        return appointments;
    }
    
}