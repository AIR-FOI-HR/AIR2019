package com.example.alarmio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Dao;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.database.DAO;
import com.example.database.MyDatabase;
import com.example.database.entities.Alarm;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KreirajAlarm extends AppCompatActivity implements View.OnClickListener {

    Button btnVrijeme, btnDatum, btnDodaj;
    String vrijemeObavijest;
    MyDatabase myDatabase;
    private static DAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj_alarm);
        btnVrijeme = findViewById(R.id.btnVrijeme);
        btnDatum = findViewById(R.id.btnDatum);
        btnDodaj = findViewById(R.id.btnDodaj);
        btnVrijeme.setOnClickListener(this);
        btnDatum.setOnClickListener(this);
        btnDodaj.setOnClickListener(this);
        myDatabase = MyDatabase.getInstance(getApplicationContext());
    }


    @Override
    public void onClick(View view) {
        if (view == btnVrijeme){
            odaberiVrijeme();
        }else if (view == btnDatum){
            odaberiDatum();
        }else{
            dodaj(this);
        }

    }

    private void dodaj(Context context) {
        if (btnVrijeme.getText().toString().equals("Odaberi vrijeme") || btnDatum.getText().toString().equals("Odaberi datum")){
            Toast.makeText(this, "Molimo odaberite vrijeme ili datum", Toast.LENGTH_SHORT).show();
        }else{
            Alarm alarm = new Alarm();
            String datum = btnDatum.getText().toString().trim();
            String vrijeme = btnVrijeme.getText().toString().trim();
            alarm.setDatum(datum);
            alarm.setVrijeme(vrijeme);

            dao = MyDatabase.getInstance(context).getDAO();
            dao.insertAlarmi(alarm);
            postaviAlarm(datum,vrijeme);
        }
    }

    private void odaberiDatum() {
        Calendar calendar = Calendar.getInstance();
        int godina = calendar.get(Calendar.YEAR);
        int mjesec = calendar.get(Calendar.MONTH);
        int dan = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int g, int m, int d) {
                btnDatum.setText(d + "/" + (m+1) +"/"+g);
            }
        },godina,mjesec,dan);
        datePickerDialog.show();
    }

    private void odaberiVrijeme() {
        Calendar calendar = Calendar.getInstance();
        int sati = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int s, int m) {
                vrijemeObavijest= s + ":" + m;
                btnVrijeme.setText(vrijemeObavijest);
            }
        },sati,minute, false);
        timePickerDialog.show();
    }

    private void postaviAlarm(String datum, String vrijeme){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(),Alarmio.class);
        intent.putExtra("datum", datum);
        intent.putExtra("vrijeme", vrijeme);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);
        String vrijeme_datum = datum + " " + vrijeme;
        DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");
        try{
            Date datum1 = formatter.parse(vrijeme_datum);
            alarmManager.set(AlarmManager.RTC_WAKEUP,datum1.getTime(),pendingIntent);
        }catch (ParseException e){
            e.printStackTrace();
        }
        finish();
    }


}