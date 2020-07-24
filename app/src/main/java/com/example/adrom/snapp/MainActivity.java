package com.example.adrom.snapp;


import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.View;

import com.github.nkzawa.socketio.client.Socket;


import Lib.Service;
import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private AlarmManager manager;

    SharedPreferences sp;
    Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranSansWeb.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        setContentView(R.layout.activity_main);




        Intent background = new Intent(getApplicationContext(),MyService.class);
        startService(background);

        if(getIntent().hasExtra("Exit"))
        {
            //finish();
            onBackPressed();
        }


        sp=getSharedPreferences("Taxi_user_datas",MODE_PRIVATE);
        String token=sp.getString("token","no");
        if(!token.equals("no"))
        {
            check_order(token);
        }

    }
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void login(View view)
    {
        Intent j=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(j);
    }
    public void register(View view)
    {
        Intent j=new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(j);
    }

    public void check_order(String token)
    {
        Service.check_order(token, new Service.CheckOrderCallback() {
            @Override
            public void onResponse(String data){

                Intent j=new Intent(MainActivity.this,CedarMapActivity.class);
                j.putExtra("run_service","ok");
                j.putExtra("data",data);
                startActivity(j);
            }

            @Override
            public void onFailure(String error) {
                Intent j=new Intent(MainActivity.this,CedarMapActivity.class);
                startActivity(j);
            }
        });
    }

}
