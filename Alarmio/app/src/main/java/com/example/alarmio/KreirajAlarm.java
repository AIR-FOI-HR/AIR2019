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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.database.DAO;
import com.example.database.MyDatabase;
import com.example.database.entities.Alarm;
import com.example.database.entities.PonavljaSeDanom;
import com.example.database.entities.PonavljajuciAlarm;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class KreirajAlarm extends AppCompatActivity implements View.OnClickListener {

    Button btnVrijeme, btnDatum, btnDodaj, btnPonavljanje;
    CheckBox chkPon, chkUto, chkSri, chkCet, chkPet, chkSub, chkNed;
    String vrijemeObavijest;
    MyDatabase myDatabase;
    EditText opisTekst;
    List<String> ponavljajuciDani = new ArrayList<String>();
    Boolean ponavljanje;
    private static DAO dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kreiraj_alarm);
        btnVrijeme = findViewById(R.id.btnVrijeme);
        btnDatum = findViewById(R.id.btnDatum);
        btnDodaj = findViewById(R.id.btnDodaj);
        btnPonavljanje = findViewById(R.id.btnSvakiDan);
        chkPon = findViewById(R.id.chkPon);
        chkUto = findViewById(R.id.chkUto);
        chkSri = findViewById(R.id.chkSri);
        chkCet = findViewById(R.id.chkCet);
        chkPet = findViewById(R.id.chkPet);
        chkSub = findViewById(R.id.chkSub);
        chkNed = findViewById(R.id.chkNed);
        chkPon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkPon.isChecked())
                {
                    ponavljajuciDani.add("Ponedjeljak");
                    Log.d("PonavljajuciDaniLista", "Pon u listu");
                }
            }
        });
        chkUto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkUto.isChecked())
                {
                    ponavljajuciDani.add("Utorak");
                    Log.d("PonavljajuciDaniLista", "Uto u listu");
                }
            }
        });
        chkSri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkSri.isChecked())
                {
                    ponavljajuciDani.add("Srijeda");
                    Log.d("PonavljajuciDaniLista", "Sri u listu");
                }
            }
        });
        chkCet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkCet.isChecked())
                {
                    ponavljajuciDani.add("Četvrtak");
                    Log.d("PonavljajuciDaniLista", "Čet u listu");
                }
            }
        });
        chkPet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkPet.isChecked())
                {
                    ponavljajuciDani.add("Petak");
                    Log.d("PonavljajuciDaniLista", "Pet u listu");
                }
            }
        });
        chkSub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkSub.isChecked())
                {
                    ponavljajuciDani.add("Subota");
                    Log.d("PonavljajuciDaniLista", "Sub u listu");
                }
            }
        });
        chkNed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(chkNed.isChecked())
                {
                    ponavljajuciDani.add("Nedjelja");
                    Log.d("PonavljajuciDaniLista", "Ned u listu");
                }
            }
        });
        btnVrijeme.setOnClickListener(this);
        btnDatum.setOnClickListener(this);
        btnDodaj.setOnClickListener(this);
        btnPonavljanje.setOnClickListener(this);
        myDatabase = MyDatabase.getInstance(getApplicationContext());
        opisTekst = findViewById(R.id.opisTekst);
    }


    @Override
    public void onClick(View view) {
        if (view == btnVrijeme){
            odaberiVrijeme();
        }else if (view == btnDatum){
            odaberiDatum();
        }else if (view == btnPonavljanje){
            postaviSve();
        }
        else{
            dodaj(this);
        }
    }

    private void postaviSve() {
        chkPon.setChecked(true);
        chkUto.setChecked(true);
        chkSri.setChecked(true);
        chkCet.setChecked(true);
        chkPet.setChecked(true);
        chkSub.setChecked(true);
        chkNed.setChecked(true);
    }

    private void dodaj(Context context) {
        if (btnVrijeme.getText().toString().equals("Odaberi vrijeme") || btnDatum.getText().toString().equals("Odaberi datum")){
            Toast.makeText(this, "Molimo odaberite vrijeme ili datum", Toast.LENGTH_SHORT).show();
        }else{
            if (ponavljajuciDani != null && ponavljajuciDani.isEmpty())
                ponavljanje = false;
            else
                ponavljanje = true;

            dao = MyDatabase.getInstance(context).getDAO();

            Alarm alarm = new Alarm();
            String datum = btnDatum.getText().toString().trim();
            String vrijeme = btnVrijeme.getText().toString().trim();
            String opis = opisTekst.getText().toString();
            alarm.setDatum(datum);
            alarm.setVrijeme(vrijeme);
            alarm.setOpis(opis);
            dao.insertAlarmi(alarm);

            if(ponavljanje=true)
            {
                PonavljajuciAlarm ponavljajuciAlarm = new PonavljajuciAlarm();
                ponavljajuciAlarm.setAlarmId(alarm.getAlarmId());
                dao.insertPonavljajuciAlarmi(ponavljajuciAlarm);
                for (String dan:ponavljajuciDani) {
                    if(dan=="Ponedjeljak")
                    {
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(1);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Pon u bazu");
                    }else if(dan=="Utorak")
                    {
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(2);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Uto u bazu");
                    }else if(dan=="Srijeda"){
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(3);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Sri u bazu");
                    }else if(dan=="Četvrtak"){
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(4);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Čet u bazu");
                    }else if(dan=="Petak"){
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(5);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Pet u bazu");
                    }else if(dan=="Subota"){
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(6);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Sub u bazu");
                    }else if(dan=="Nedjelja"){
                        PonavljaSeDanom ponavljaSeDanom = new PonavljaSeDanom();
                        ponavljaSeDanom.setDanId(7);
                        ponavljaSeDanom.setPonavljajuciAlarmId(ponavljajuciAlarm.getPonavljajuciAlarmId());
                        dao.insertPonavljaSeDanom(ponavljaSeDanom);
                        Log.d("PonavljaSeDanom", "Ned u bazu");
                    }
                }
            }

            postaviAlarm(datum, vrijeme, opis);
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

    private void postaviAlarm(String datum, String vrijeme, String opis){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(),Alarmio.class);
        intent.putExtra("datum", datum);
        intent.putExtra("vrijeme", vrijeme);
        intent.putExtra("opis", opis);

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